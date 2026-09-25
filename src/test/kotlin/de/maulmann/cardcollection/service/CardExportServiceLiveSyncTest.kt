package de.maulmann.cardcollection.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@TestPropertySource(properties = [
    "spring.datasource.url=jdbc:mysql://localhost:3306/card_collection?cachePrepStmts=true&prepStmtCacheSize=250&prepStmtCacheSqlLimit=2048&useServerPrepStmts=true&rewriteBatchedStatements=true&elideSetAutoCommits=true&useLocalSessionState=true&maintainTimeStats=false",
    "spring.datasource.username=root",
    "spring.datasource.password=MsgWii29102009.r4!",
    "spring.datasource.driverClassName=com.mysql.cj.jdbc.Driver",
    "spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect",
    "spring.jpa.hibernate.ddl-auto=none",
    "spring.flyway.enabled=false"
])
class CardExportServiceLiveSyncTest {

    @Autowired
    private lateinit var cardExportService: CardExportService

    @Test
    fun `export and sync all cards from live database to cards json`() {
        val dtos = cardExportService.exportAllCardsToJsonDtos()
        assertThat(dtos).isNotEmpty
        val file = cardExportService.syncCardsJsonToStaticSite()
        assertThat(file).exists()
        assertThat(file.length()).isGreaterThan(0L)
        println("Successfully exported and synced ${dtos.size} cards from live database to ${file.absolutePath}")

        val flairLegacy = dtos.firstOrNull { it.id.contains("flair-showcase-showtime-legacy-collection-row-0-seat-5") }
        println("Flair Showcase Legacy Collection in export: packOdds=${flairLegacy?.packOdds}")
        assertThat(flairLegacy?.packOdds).isEqualTo("1:30 Packs")

        val flairRow0Base = dtos.firstOrNull { it.id.contains("flair-showcase-showtime-row-0-seat-5") }
        println("Flair Showcase Row 0 Base in export: packOdds=${flairRow0Base?.packOdds}")
        assertThat(flairRow0Base?.packOdds).isEqualTo("1:24 Packs")
    }
}
