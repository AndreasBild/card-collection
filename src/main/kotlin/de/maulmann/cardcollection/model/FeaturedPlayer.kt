package de.maulmann.cardcollection.model

/**
 * Display name of the collection owner: listed first on multi-player cards and used as export fallback.
 *
 * Entities cannot receive Spring beans, so the configured value (`collection.owner-name`) is published here once
 * at startup by `CollectionConfig`. Outside a Spring context (plain unit tests) the default applies.
 */
object FeaturedPlayer {
    const val DEFAULT_NAME = "Juwan Howard"

    @Volatile
    var name: String = DEFAULT_NAME
}
