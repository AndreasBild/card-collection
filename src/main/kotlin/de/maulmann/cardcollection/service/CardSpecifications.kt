package de.maulmann.cardcollection.service

import de.maulmann.cardcollection.dto.CardFilter
import de.maulmann.cardcollection.model.*
import jakarta.persistence.criteria.JoinType
import org.springframework.data.jpa.domain.Specification

/**
 * Builds the dynamic [Specification] for card queries.
 *
 * Filters on to-many relations use EXISTS subqueries and fetches are to-one only, so result rows cannot
 * duplicate and no DISTINCT is required. Fetch joins are applied to the entity select only and never to the
 * pagination count query (which returns Long).
 */
object CardSpecifications {

    fun fromFilter(filter: CardFilter): Specification<Card> {
        val specifications = buildList {
            filter.manufacturerId?.let { add(toOneIdEquals<CardManufacturer>("manufacturer", it)) }
            filter.brandId?.let { add(toOneIdEquals<CardBrand>("brand", it)) }
            filter.themeId?.let { add(toOneIdEquals<CardTheme>("theme", it)) }
            filter.seasonId?.let { add(toOneIdEquals<Season>("season", it)) }
            filter.variantId?.let { add(toOneIdEquals<Variant>("variant", it)) }
            filter.sportId?.let { sportId ->
                add(existsCardPlayer { cardPlayer, cb ->
                    cb.equal(cardPlayer.get<Player>("player").get<Sport>("sport").get<Long>("id"), sportId)
                })
            }
            filter.playerId?.let { playerId ->
                add(existsCardPlayer { cardPlayer, cb ->
                    cb.equal(cardPlayer.get<Player>("player").get<Long>("id"), playerId)
                })
            }
            filter.teamId?.let { teamId ->
                add(existsCardPlayer { cardPlayer, cb ->
                    cb.equal(cardPlayer.get<Team>("team").get<Long>("id"), teamId)
                })
            }
            filter.gameUsed?.let { value ->
                add(Specification<Card> { root, _, cb -> cb.equal(root.get<Boolean>("gameUsedMaterial"), value) })
            }
            filter.autograph?.let { value ->
                add(Specification<Card> { root, _, cb -> cb.equal(root.get<Boolean>("autograph"), value) })
            }
            filter.rookieCard?.let { value ->
                add(Specification<Card> { root, _, cb -> cb.equal(root.get<Boolean>("rookieCard"), value) })
            }
            filter.isGradedNullable?.let { isGraded ->
                add(Specification<Card> { root, _, cb ->
                    if (isGraded) cb.isNotNull(root.get<Any>("grading")) else cb.isNull(root.get<Any>("grading"))
                })
            }
            PrintRunRange.fromKey(filter.printRunRangeKey)?.let { add(printRunIn(it)) }
        }

        val filters = specifications.reduceOrNull { acc, spec -> acc.and(spec) }
            ?: Specification { _, _, _ -> null }
        return filters.and(fetchToOneRelations())
    }

    private inline fun <reified T> toOneIdEquals(attribute: String, id: Long): Specification<Card> =
        Specification { root, _, cb -> cb.equal(root.get<T>(attribute).get<Long>("id"), id) }

    private fun existsCardPlayer(
        condition: (jakarta.persistence.criteria.Root<CardPlayer>, jakarta.persistence.criteria.CriteriaBuilder) ->
        jakarta.persistence.criteria.Predicate
    ): Specification<Card> = Specification { root, query, cb ->
        val subquery = query!!.subquery(Long::class.java)
        val cardPlayer = subquery.from(CardPlayer::class.java)
        subquery.select(cardPlayer.get<Card>("card").get("id"))
            .where(cb.equal(cardPlayer.get<Card>("card"), root), condition(cardPlayer, cb))
        cb.exists(subquery)
    }

    private fun printRunIn(range: PrintRunRange): Specification<Card> = Specification { root, _, cb ->
        val printRun = root.get<Int?>("printRun")
        when {
            range.exactValue != null -> cb.equal(printRun, range.exactValue)
            range.maxValue != null -> cb.and(cb.isNotNull(printRun), cb.lessThanOrEqualTo(root.get("printRun"), range.maxValue))
            else -> cb.isNotNull(printRun)
        }
    }

    private fun fetchToOneRelations(): Specification<Card> = Specification { root, query, _ ->
        if (query != null && Card::class.java.isAssignableFrom(query.resultType)) {
            root.fetch<Card, Season>("season", JoinType.LEFT)
            root.fetch<Card, Variant>("variant", JoinType.LEFT)
            root.fetch<Card, CardTheme>("theme", JoinType.LEFT)
            root.fetch<Card, CardBrand>("brand", JoinType.LEFT)
            root.fetch<Card, CardManufacturer>("manufacturer", JoinType.LEFT)
            root.fetch<Card, Grading>("grading", JoinType.LEFT)
        }
        null
    }
}
