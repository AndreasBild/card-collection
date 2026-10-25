package de.maulmann.cardcollection.dto

import org.springframework.web.util.UriComponentsBuilder

/**
 * Builds `/cards` URLs that carry the active filters, page size and sort. Exposed to the page template as
 * `cardUrls`, replacing the long parameter list that used to be repeated in every pagination and sort link.
 * Only active filters are emitted; the result is percent-encoded and Thymeleaf HTML-escapes it on output.
 */
class CardsUrlBuilder(
    private val filter: CardFilter,
    private val pageSize: String,
    private val sort: String?
) {
    /** Link to [page] with the current filters, page size and sort. */
    fun page(page: Int): String = build(page, sort)

    /** Link to the first page sorted by [sortValue] (`property,direction`) with the current filters and size. */
    fun sort(sortValue: String): String = build(0, sortValue)

    private fun build(page: Int, sort: String?): String {
        val builder = UriComponentsBuilder.fromPath("/cards")
            .queryParam("page", page)
            .queryParam("size", pageSize)
        sort?.takeIf { it.isNotBlank() }?.let { builder.queryParam("sort", it) }
        filter.toQueryParams().forEach { (name, value) -> builder.queryParam(name, value) }
        return builder.build().encode().toUriString()
    }
}
