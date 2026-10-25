package de.maulmann.cardcollection.model

/**
 * Everything that is specific to one grading company: the public verification link, the link behind the list's
 * scan icon, and how to find the scan images (candidate image URLs to probe, the page to scrape, which scraped
 * image is the front and which the back). All functions are pure (no HTTP), so they are unit-testable offline;
 * `GradingScanDownloadService` only does the network I/O.
 */
sealed interface GradingProvider {

    /** Public certificate verification page shown to users. */
    fun verificationUrl(cert: String): String

    /** Link behind the "scan" icon in the card list, or null when the company has none. */
    fun frontScanUrl(cert: String): String?

    /** Page whose HTML is scraped for scan image URLs. */
    fun scanPageUrl(cert: String): String

    /** Front image URLs worth probing directly (HEAD) before scraping; empty when the company has none. */
    fun directFrontCandidates(cert: String): List<String> = emptyList()

    /** Back image URLs worth probing directly (HEAD) before scraping; empty when the company has none. */
    fun directBackCandidates(cert: String): List<String> = emptyList()

    /** Whether a scraped image URL plausibly belongs to this company's scan of [cert]. */
    fun isRelevantImage(url: String, cert: String): Boolean

    fun isFrontImage(url: String): Boolean = url.contains("front", ignoreCase = true)

    fun isBackImage(url: String): Boolean = url.contains("back", ignoreCase = true)

    /**
     * Chooses front and back from the scraped [images], keeping whichever of [front]/[back] is already known.
     * The front falls back to the first relevant image; the back must differ from the chosen front.
     */
    fun pickScans(images: List<String>, cert: String, front: String?, back: String?): Pair<String?, String?> {
        val relevant = images.filter { isRelevantImage(it, cert) }
        val resolvedFront = front ?: (relevant.firstOrNull { isFrontImage(it) } ?: relevant.firstOrNull())
        val resolvedBack = back ?: relevant.firstOrNull { isBackImage(it) && it != resolvedFront }
        return resolvedFront to resolvedBack
    }

    object Psa : GradingProvider {
        private const val CLOUDFRONT = "https://d1htnxwo4o0jhw.cloudfront.net/cert"
        private const val CERT_HOST = "https://cert.psacard.com"

        override fun verificationUrl(cert: String) = "https://www.psacard.com/cert/$cert"

        override fun frontScanUrl(cert: String) = "$CLOUDFRONT/$cert/front-large.jpg"

        // PSA's cert page is also where dynamic / zoom images are found when the direct URLs miss.
        override fun scanPageUrl(cert: String) = verificationUrl(cert)

        override fun directFrontCandidates(cert: String) = candidates(cert, "front")

        override fun directBackCandidates(cert: String) = candidates(cert, "back")

        private fun candidates(cert: String, side: String) = listOf(
            "$CLOUDFRONT/$cert/$side-original.jpg",
            "$CLOUDFRONT/$cert/$side-large.jpg",
            "$CLOUDFRONT/$cert/$side.jpg",
            "$CERT_HOST/$cert/$side-large.jpg",
            "$CERT_HOST/$cert/$side.jpg"
        )

        override fun isRelevantImage(url: String, cert: String) =
            url.contains(cert, ignoreCase = true) ||
                url.contains("cert", ignoreCase = true) ||
                url.contains("cloudfront", ignoreCase = true)
    }

    object Bgs : GradingProvider {
        override fun verificationUrl(cert: String) =
            "https://www.beckett.com/grading/card-lookup?item_type=BGS&item_id=$cert"

        // Beckett has no stable image URL, so the icon links to the lookup page.
        override fun frontScanUrl(cert: String) = verificationUrl(cert)

        override fun scanPageUrl(cert: String) = verificationUrl(cert)

        override fun isRelevantImage(url: String, cert: String) =
            url.contains("beckett.com", ignoreCase = true) || url.contains(cert, ignoreCase = true)

        override fun isFrontImage(url: String) =
            url.contains("front", ignoreCase = true) || url.contains("f.jpg", ignoreCase = true)

        override fun isBackImage(url: String) =
            url.contains("back", ignoreCase = true) || url.contains("b.jpg", ignoreCase = true)
    }

    object Mba : GradingProvider {
        // The user-facing link is the generic lookup page; scraping passes the serial so the page lists the scan.
        override fun verificationUrl(cert: String) = "https://checkcoa.com/mba-grading-certificate-lookup/"

        override fun frontScanUrl(cert: String): String? = null

        override fun scanPageUrl(cert: String) = "https://checkcoa.com/mba-grading-certificate-lookup/?serial=$cert"

        override fun isRelevantImage(url: String, cert: String) =
            url.contains(cert, ignoreCase = true) ||
                url.contains("mba", ignoreCase = true) ||
                url.contains("checkcoa", ignoreCase = true)
    }
}

/** The provider for this company; the exhaustive `when` makes adding a company a compile error until handled. */
fun GradingCompany.provider(): GradingProvider = when (this) {
    GradingCompany.PSA -> GradingProvider.Psa
    GradingCompany.BGS -> GradingProvider.Bgs
    GradingCompany.MBA -> GradingProvider.Mba
}
