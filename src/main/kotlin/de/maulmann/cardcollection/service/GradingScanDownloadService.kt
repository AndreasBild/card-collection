package de.maulmann.cardcollection.service

import de.maulmann.cardcollection.dto.GradingScanCardResult
import de.maulmann.cardcollection.dto.GradingScanDownloadSummary
import de.maulmann.cardcollection.model.Card
import de.maulmann.cardcollection.model.GradingCompany
import de.maulmann.cardcollection.model.GradingProvider
import de.maulmann.cardcollection.model.provider
import de.maulmann.cardcollection.repository.CardRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.File
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.time.Duration
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Semaphore
import java.util.regex.Pattern

@Service
class GradingScanDownloadService(
    private val cardRepository: CardRepository,
    @param:Value("\${grading.images.download-dir:gradingImages}")
    private val downloadDirSetting: String = "gradingImages",
    private val httpClient: HttpClient = HttpClient.newBuilder()
        .executor(Executors.newVirtualThreadPerTaskExecutor())
        .version(HttpClient.Version.HTTP_2)
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

    // Intentionally not transactional: cards are loaded fully (fetch joins) by the repository call below,
    // so no DB connection is held while performing slow outbound HTTP downloads.
    fun downloadAllGradingScans(overwrite: Boolean = false): GradingScanDownloadSummary {
        val targetDir = getDownloadDirectory()
        val allGradedCards = cardRepository.findAllGradedWithDetails()

        logger.info("Found {} graded cards with certificate numbers for scan download.", allGradedCards.size)

        if (allGradedCards.isEmpty()) {
            return GradingScanDownloadSummary(
                totalGradedCards = 0,
                successfulDownloads = 0,
                alreadyPresent = 0,
                notAvailableOrFailed = 0,
                outputDirectory = targetDir.absolutePath,
                cardResults = emptyList()
            )
        }

        val concurrencyLimit = 8
        val semaphore = Semaphore(concurrencyLimit)
        val results = Executors.newVirtualThreadPerTaskExecutor().use { executor ->
            allGradedCards.map { card ->
                executor.submit(Callable {
                    semaphore.acquire()
                    try {
                        downloadScansForCard(card, targetDir, overwrite)
                    } finally {
                        semaphore.release()
                    }
                })
            }.map { it.get() }
        }

        var successfulDownloads = 0
        var alreadyPresent = 0
        var notAvailableOrFailed = 0

        for (result in results) {
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

    /**
     * Finds the front and back scan URLs: first by probing the provider's direct image candidates, then (only if a
     * side is still missing) by scraping the provider's page. The company-specific rules live in [GradingProvider].
     */
    fun resolveScanUrls(company: GradingCompany, certNumber: String): Pair<String?, String?> {
        val provider = company.provider()
        val (front, back) = probeDirectCandidates(provider, certNumber)
        if (front != null && back != null) return front to back

        val html = fetchPageHtml(provider.scanPageUrl(certNumber)) ?: return front to back
        return provider.pickScans(extractImageUrls(html), certNumber, front, back)
    }

    private fun probeDirectCandidates(provider: GradingProvider, certNumber: String): Pair<String?, String?> {
        val frontCandidates = provider.directFrontCandidates(certNumber)
        val backCandidates = provider.directBackCandidates(certNumber)
        if (frontCandidates.isEmpty() && backCandidates.isEmpty()) return null to null

        return Executors.newVirtualThreadPerTaskExecutor().use { executor ->
            val frontFuture = executor.submit(Callable { findFirstValidCandidate(frontCandidates) })
            val backFuture = executor.submit(Callable { findFirstValidCandidate(backCandidates) })
            frontFuture.get() to backFuture.get()
        }
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

    private fun findFirstValidCandidate(candidates: List<String>): String? {
        if (candidates.isEmpty()) return null
        return Executors.newVirtualThreadPerTaskExecutor().use { executor ->
            val futures = candidates.map { url ->
                url to executor.submit(Callable { checkUrlExists(url) })
            }
            futures.firstOrNull { (_, future) -> future.get() }?.first
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

            val response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofFile(
                    targetFile.toPath(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.WRITE,
                    StandardOpenOption.TRUNCATE_EXISTING
                )
            )
            if (response.statusCode() in 200..299) {
                logger.info("Successfully downloaded scan from {} to {}", url, targetFile.name)
                true
            } else {
                logger.warn("Download from {} failed with status {}", url, response.statusCode())
                Files.deleteIfExists(targetFile.toPath())
                false
            }
        } catch (e: Exception) {
            logger.warn("Failed to download file from {}: {}", url, e.message)
            Files.deleteIfExists(targetFile.toPath())
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
