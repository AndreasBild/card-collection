package de.maulmann.cardcollection.service

import de.maulmann.cardcollection.dto.GradingScanCardResult
import de.maulmann.cardcollection.dto.GradingScanDownloadSummary
import de.maulmann.cardcollection.model.Card
import de.maulmann.cardcollection.model.GradingCompany
import de.maulmann.cardcollection.repository.CardRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.File
import java.io.InputStream
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.time.Duration
import java.util.regex.Pattern

@Service
class GradingScanDownloadService(
    private val cardRepository: CardRepository,
    @Value("\${grading.images.download-dir:gradingImages}")
    private val downloadDirSetting: String = "gradingImages",
    private val httpClient: HttpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(10))
        .followRedirects(HttpClient.Redirect.NORMAL)
        .build()
) {

    private val logger = LoggerFactory.getLogger(GradingScanDownloadService::class.java)

    companion object {
        private const val USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36"
        private val IMAGE_URL_PATTERN = Pattern.compile("https?://[^\"'\\s<>]+\\.(?:jpg|jpeg|png|webp)", Pattern.CASE_INSENSITIVE)
    }

    fun getDownloadDirectory(): File {
        val path = Paths.get(downloadDirSetting)
        val dir = if (path.isAbsolute) path.toFile() else Paths.get("").resolve(path).toFile()
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    @Transactional(readOnly = true)
    fun downloadAllGradingScans(overwrite: Boolean = false): GradingScanDownloadSummary {
        val targetDir = getDownloadDirectory()
        val allGradedCards = cardRepository.findAllWithDetails()
            .filter { it.grading != null && !it.gradingCertNumber.isNullOrBlank() }

        logger.info("Found {} graded cards with certificate numbers for scan download.", allGradedCards.size)

        var successfulDownloads = 0
        var alreadyPresent = 0
        var notAvailableOrFailed = 0
        val results = mutableListOf<GradingScanCardResult>()

        for (card in allGradedCards) {
            val result = downloadScansForCard(card, targetDir, overwrite)
            results.add(result)

            when (result.status) {
                "SUCCESS" -> successfulDownloads++
                "ALREADY_EXISTS" -> alreadyPresent++
                else -> notAvailableOrFailed++
            }
        }

        return GradingScanDownloadSummary(
            totalGradedCards = allGradedCards.size,
            successfulDownloads = successfulDownloads,
            alreadyPresent = alreadyPresent,
            notAvailableOrFailed = notAvailableOrFailed,
            outputDirectory = targetDir.absolutePath,
            cardResults = results
        )
    }

    fun downloadScansForCard(card: Card, targetDir: File, overwrite: Boolean): GradingScanCardResult {
        val cert = card.gradingCertNumber?.trim() ?: return GradingScanCardResult(
            cardId = card.id,
            company = "UNKNOWN",
            certNumber = "",
            frontImageDownloaded = false,
            backImageDownloaded = false,
            status = "NO_CERT",
            message = "Card has no grading certificate number."
        )

        val company = card.grading?.gradingCompany ?: return GradingScanCardResult(
            cardId = card.id,
            company = "UNKNOWN",
            certNumber = cert,
            frontImageDownloaded = false,
            backImageDownloaded = false,
            status = "NO_COMPANY",
            message = "Card has no grading company specified."
        )

        val frontFile = File(targetDir, "${company.name}_${cert}_front.jpg")
        val backFile = File(targetDir, "${company.name}_${cert}_back.jpg")

        if (!overwrite && frontFile.exists() && backFile.exists()) {
            return GradingScanCardResult(
                cardId = card.id,
                company = company.name,
                certNumber = cert,
                frontImageDownloaded = true,
                backImageDownloaded = true,
                frontImagePath = frontFile.absolutePath,
                backImagePath = backFile.absolutePath,
                status = "ALREADY_EXISTS",
                message = "Front and back scans already exist on disk."
            )
        }

        return try {
            val (frontUrl, backUrl) = resolveScanUrls(company, cert)

            var frontDownloaded = frontFile.exists() && !overwrite
            var backDownloaded = backFile.exists() && !overwrite

            if ((overwrite || !frontFile.exists()) && frontUrl != null) {
                frontDownloaded = downloadFile(frontUrl, frontFile)
            }

            if ((overwrite || !backFile.exists()) && backUrl != null) {
                backDownloaded = downloadFile(backUrl, backFile)
            }

            val status = when {
                frontDownloaded && backDownloaded -> "SUCCESS"
                frontDownloaded || backDownloaded -> "PARTIAL"
                else -> "NOT_FOUND"
            }

            GradingScanCardResult(
                cardId = card.id,
                company = company.name,
                certNumber = cert,
                frontImageDownloaded = frontDownloaded,
                backImageDownloaded = backDownloaded,
                frontImagePath = if (frontDownloaded) frontFile.absolutePath else null,
                backImagePath = if (backDownloaded) backFile.absolutePath else null,
                status = status,
                message = if (status == "NOT_FOUND") "No high-res scan images found online for cert $cert" else null
            )
        } catch (e: Exception) {
            logger.warn("Failed to download scans for {} cert {}: {}", company, cert, e.message)
            GradingScanCardResult(
                cardId = card.id,
                company = company.name,
                certNumber = cert,
                frontImageDownloaded = frontFile.exists(),
                backImageDownloaded = backFile.exists(),
                frontImagePath = if (frontFile.exists()) frontFile.absolutePath else null,
                backImagePath = if (backFile.exists()) backFile.absolutePath else null,
                status = "ERROR",
                message = e.message
            )
        }
    }

    fun resolveScanUrls(company: GradingCompany, certNumber: String): Pair<String?, String?> {
        return when (company) {
            GradingCompany.PSA -> resolvePsaScanUrls(certNumber)
            GradingCompany.BGS -> resolveBgsScanUrls(certNumber)
            GradingCompany.MBA -> resolveMbaScanUrls(certNumber)
        }
    }

    private fun resolvePsaScanUrls(certNumber: String): Pair<String?, String?> {
        // Direct PSA CDN high-res scan pattern
        val directFrontCdn = "https://d1htnxwo4o0jhw.cloudfront.net/cert/$certNumber/front.jpg"
        val directBackCdn = "https://d1htnxwo4o0jhw.cloudfront.net/cert/$certNumber/back.jpg"

        if (checkUrlExists(directFrontCdn)) {
            val backUrl = if (checkUrlExists(directBackCdn)) directBackCdn else null
            return Pair(directFrontCdn, backUrl)
        }

        // Secondary CDN pattern
        val certPsaFront = "https://cert.psacard.com/$certNumber/front.jpg"
        val certPsaBack = "https://cert.psacard.com/$certNumber/back.jpg"
        if (checkUrlExists(certPsaFront)) {
            val backUrl = if (checkUrlExists(certPsaBack)) certPsaBack else null
            return Pair(certPsaFront, backUrl)
        }

        // Fallback: parse PSA cert page
        val certPageUrl = "https://www.psacard.com/cert/$certNumber"
        val html = fetchPageHtml(certPageUrl) ?: return Pair(null, null)

        val imageUrls = extractImageUrls(html).filter { url ->
            url.contains(certNumber, ignoreCase = true) || url.contains("cert", ignoreCase = true)
        }

        val frontUrl = imageUrls.firstOrNull { it.contains("front", ignoreCase = true) } ?: imageUrls.firstOrNull()
        val backUrl = imageUrls.firstOrNull { it.contains("back", ignoreCase = true) && it != frontUrl }
        return Pair(frontUrl, backUrl)
    }

    private fun resolveBgsScanUrls(certNumber: String): Pair<String?, String?> {
        val certPageUrl = "https://www.beckett.com/grading/card-lookup?item_type=BGS&item_id=$certNumber"
        val html = fetchPageHtml(certPageUrl) ?: return Pair(null, null)

        val imageUrls = extractImageUrls(html).filter { url ->
            url.contains("beckett.com", ignoreCase = true) || url.contains(certNumber, ignoreCase = true)
        }

        val frontUrl = imageUrls.firstOrNull { it.contains("front", ignoreCase = true) || it.contains("f.jpg", ignoreCase = true) } ?: imageUrls.firstOrNull()
        val backUrl = imageUrls.firstOrNull { (it.contains("back", ignoreCase = true) || it.contains("b.jpg", ignoreCase = true)) && it != frontUrl }
        return Pair(frontUrl, backUrl)
    }

    private fun resolveMbaScanUrls(certNumber: String): Pair<String?, String?> {
        val lookupUrl = "https://checkcoa.com/mba-grading-certificate-lookup/?serial=$certNumber"
        val html = fetchPageHtml(lookupUrl) ?: return Pair(null, null)

        val imageUrls = extractImageUrls(html).filter { url ->
            url.contains(certNumber, ignoreCase = true) || url.contains("mba", ignoreCase = true) || url.contains("checkcoa", ignoreCase = true)
        }

        val frontUrl = imageUrls.firstOrNull { it.contains("front", ignoreCase = true) } ?: imageUrls.firstOrNull()
        val backUrl = imageUrls.firstOrNull { it.contains("back", ignoreCase = true) && it != frontUrl }
        return Pair(frontUrl, backUrl)
    }

    private fun fetchPageHtml(url: String): String? {
        return try {
            val request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent", USER_AGENT)
                .timeout(Duration.ofSeconds(8))
                .GET()
                .build()

            val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
            if (response.statusCode() in 200..299) {
                response.body()
            } else {
                logger.debug("Page fetch for {} returned status {}", url, response.statusCode())
                null
            }
        } catch (e: Exception) {
            logger.debug("Failed to fetch page {}: {}", url, e.message)
            null
        }
    }

    private fun checkUrlExists(url: String): Boolean {
        return try {
            val request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent", USER_AGENT)
                .timeout(Duration.ofSeconds(5))
                .method("HEAD", HttpRequest.BodyPublishers.noBody())
                .build()

            val response = httpClient.send(request, HttpResponse.BodyHandlers.discarding())
            response.statusCode() in 200..299
        } catch (e: Exception) {
            false
        }
    }

    private fun downloadFile(url: String, targetFile: File): Boolean {
        return try {
            val request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent", USER_AGENT)
                .timeout(Duration.ofSeconds(15))
                .GET()
                .build()

            val response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream())
            if (response.statusCode() in 200..299) {
                response.body().use { input ->
                    Files.copy(input, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
                }
                logger.info("Successfully downloaded scan from {} to {}", url, targetFile.name)
                true
            } else {
                logger.warn("Download from {} failed with status {}", url, response.statusCode())
                false
            }
        } catch (e: Exception) {
            logger.warn("Failed to download file from {}: {}", url, e.message)
            false
        }
    }

    private fun extractImageUrls(html: String): List<String> {
        val matcher = IMAGE_URL_PATTERN.matcher(html)
        val urls = mutableListOf<String>()
        while (matcher.find()) {
            urls.add(matcher.group())
        }
        return urls.distinct()
    }
}
