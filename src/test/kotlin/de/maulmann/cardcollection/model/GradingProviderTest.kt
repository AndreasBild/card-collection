package de.maulmann.cardcollection.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class GradingProviderTest {

    private val cert = "83921049"

    @Test
    fun `every grading company has a provider`() {
        assertThat(GradingCompany.entries.map { it.provider() }.toSet()).hasSize(GradingCompany.entries.size)
    }

    @Test
    fun `verification and scan icon urls are unchanged per company`() {
        assertThat(GradingProvider.Psa.verificationUrl(cert)).isEqualTo("https://www.psacard.com/cert/$cert")
        assertThat(GradingProvider.Psa.frontScanUrl(cert))
            .isEqualTo("https://d1htnxwo4o0jhw.cloudfront.net/cert/$cert/front-large.jpg")

        val bgs = "https://www.beckett.com/grading/card-lookup?item_type=BGS&item_id=$cert"
        assertThat(GradingProvider.Bgs.verificationUrl(cert)).isEqualTo(bgs)
        assertThat(GradingProvider.Bgs.frontScanUrl(cert)).isEqualTo(bgs)

        assertThat(GradingProvider.Mba.verificationUrl(cert))
            .isEqualTo("https://checkcoa.com/mba-grading-certificate-lookup/")
        assertThat(GradingProvider.Mba.frontScanUrl(cert)).isNull()
    }

    @Test
    fun `scan page urls match what the downloader scraped before`() {
        assertThat(GradingProvider.Psa.scanPageUrl(cert)).isEqualTo("https://www.psacard.com/cert/$cert")
        assertThat(GradingProvider.Bgs.scanPageUrl(cert))
            .isEqualTo("https://www.beckett.com/grading/card-lookup?item_type=BGS&item_id=$cert")
        assertThat(GradingProvider.Mba.scanPageUrl(cert))
            .isEqualTo("https://checkcoa.com/mba-grading-certificate-lookup/?serial=$cert")
    }

    @Test
    fun `psa probes five ordered direct candidates per side and the others none`() {
        assertThat(GradingProvider.Psa.directFrontCandidates(cert)).containsExactly(
            "https://d1htnxwo4o0jhw.cloudfront.net/cert/$cert/front-original.jpg",
            "https://d1htnxwo4o0jhw.cloudfront.net/cert/$cert/front-large.jpg",
            "https://d1htnxwo4o0jhw.cloudfront.net/cert/$cert/front.jpg",
            "https://cert.psacard.com/$cert/front-large.jpg",
            "https://cert.psacard.com/$cert/front.jpg"
        )
        assertThat(GradingProvider.Psa.directBackCandidates(cert)).containsExactly(
            "https://d1htnxwo4o0jhw.cloudfront.net/cert/$cert/back-original.jpg",
            "https://d1htnxwo4o0jhw.cloudfront.net/cert/$cert/back-large.jpg",
            "https://d1htnxwo4o0jhw.cloudfront.net/cert/$cert/back.jpg",
            "https://cert.psacard.com/$cert/back-large.jpg",
            "https://cert.psacard.com/$cert/back.jpg"
        )
        assertThat(GradingProvider.Bgs.directFrontCandidates(cert)).isEmpty()
        assertThat(GradingProvider.Mba.directBackCandidates(cert)).isEmpty()
    }

    @Test
    fun `pickScans chooses front and back from scraped images`() {
        val images = listOf(
            "https://img.example/logo.png",
            "https://images.example.com/x/$cert/back.jpg",
            "https://images.example.com/x/$cert/front.jpg"
        )

        val (front, back) = GradingProvider.Psa.pickScans(images, cert, null, null)

        assertThat(front).isEqualTo("https://images.example.com/x/$cert/front.jpg")
        assertThat(back).isEqualTo("https://images.example.com/x/$cert/back.jpg")
    }

    @Test
    fun `pickScans keeps an already known side and never reuses the front as the back`() {
        val images = listOf("https://images.example.com/$cert/front-back.jpg")

        val (front, back) = GradingProvider.Psa.pickScans(images, cert, "https://known/front.jpg", null)

        assertThat(front).isEqualTo("https://known/front.jpg")
        assertThat(back).isEqualTo("https://images.example.com/$cert/front-back.jpg")

        val (f2, b2) = GradingProvider.Psa.pickScans(images, cert, null, null)
        assertThat(f2).isEqualTo("https://images.example.com/$cert/front-back.jpg")
        assertThat(b2).isNull()
    }

    @Test
    fun `bgs recognises f and b suffix images and ignores unrelated hosts`() {
        val images = listOf(
            "https://other.example/banner.jpg",
            "https://www.beckett.com/img/$cert-b.jpg",
            "https://www.beckett.com/img/$cert-f.jpg"
        )

        val (front, back) = GradingProvider.Bgs.pickScans(images, cert, null, null)

        assertThat(front).isEqualTo("https://www.beckett.com/img/$cert-f.jpg")
        assertThat(back).isEqualTo("https://www.beckett.com/img/$cert-b.jpg")
    }

    @Test
    fun `pickScans returns nulls when nothing relevant was scraped`() {
        assertThat(GradingProvider.Mba.pickScans(listOf("https://elsewhere.example/a.jpg"), cert, null, null))
            .isEqualTo(null to null)
        assertThat(GradingProvider.Mba.pickScans(emptyList(), cert, null, null)).isEqualTo(null to null)
    }
}
