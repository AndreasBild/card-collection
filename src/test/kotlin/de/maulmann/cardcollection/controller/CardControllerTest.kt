package de.maulmann.cardcollection.controller

import de.maulmann.cardcollection.model.*
import de.maulmann.cardcollection.model.CardManufacturer
import de.maulmann.cardcollection.service.CardManufacturerService
import de.maulmann.cardcollection.service.CardService
import de.maulmann.cardcollection.service.PlayerService
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.*
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.mockito.Mockito.mock
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import kotlin.math.ceil
import kotlin.math.min
import org.mockito.Mockito.`when` as whenever


@WebMvcTest(CardController::class)
class CardControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @TestConfiguration
    class TestConfig {
        @Bean
        @Primary
        fun cardService() = mock(CardService::class.java)!!

        @Bean
        @Primary
        fun cardManufacturerService() = mock(CardManufacturerService::class.java)!!

        @Bean
        @Primary
        fun playerService() = mock(PlayerService::class.java)!!
    }

    private val cardService = mock(CardService::class.java)
    private val cardManufacturerService = mock(CardManufacturerService::class.java)
    private val playerService = mock(PlayerService::class.java)

    private lateinit var mockCards: List<Card>

    // Helper to create a Card instance with minimal valid related entities
    private fun createMockCard(id: Long, playerName: String, playerSurname: String, manufacturerIdVal: Long? = null): Card {
        val sport = Sport(id = 1, name = "Basketball") // Reused basic sport
        val team = Team(id = 1, name = "Some Team")     // Reused basic team
        val player = Player(id = id, name = playerName, surname = playerSurname, sport = sport, team = team)
        
        val manufacturer = CardManufacturer(id = manufacturerIdVal ?: id, name = "Test Manufacturer")
        val brand = CardBrand(id = id, name = "Test Brand", manufacturer = manufacturer)
        val theme = CardTheme(id = id, name = "Test Theme", brand = brand)
        val variant = Variant(id = id, name = "Base")
        
        return Card(
            id = id,
            player = player,
            theme = theme,
            variant = variant,
            printRun = (id * 10).toInt(), // Example data
            serialNumber = id.toInt(),    // Example data
            season = "2023-24",
            number = "C$id",
            rookieCard = (id % 2 == 0L),  // Example data
            gameUsedMaterial = (id % 3 == 0L), // Example data
            autograph = (id % 4 == 0L)      // Example data
        )
    }

    @BeforeEach
    fun setUp() {
        mockCards = (1L..25L).map { id ->
            // Pad surname for predictable sorting: Surname01, Surname02, ...
            createMockCard(id, "PlayerName$id", "Surname${String.format("%02d", id)}", if (id <=5) 1L else id) 
        }

        // Mock filter data services (called by controller to populate model for dropdowns)
        `when`(cardManufacturerService.getAllCardManufacturers()).thenReturn(emptyList())
        `when`(playerService.getPlayers()).thenReturn(emptyList())
        `when`(cardService.getAllBrands(any())).thenReturn(emptyList())
        `when`(cardService.getAllThemes(any(), any())).thenReturn(emptyList())
        `when`(cardService.getAllSports()).thenReturn(emptyList())
        `when`(cardService.getAllSeasons()).thenReturn(emptyList())
        `when`(cardService.getAllVariants()).thenReturn(emptyList())
        `when`(cardService.getAllTeams()).thenReturn(emptyList())
        // PrintRunRange.entries.toTypedArray() is used directly in controller, no service mock needed for it.

        // General mock for getCardsFiltered, this will be the default behavior.
        // Specific tests can add more specific whenever().thenAnswer() blocks if needed,
        // or this one can be made more sophisticated.
        whenever(cardService.getCardsFiltered(
            manufacturerId = any(), brandId = any(), themeId = any(), sportId = any(),
            playerId = any(), season = any(), gameUsed = any(), autograph = any(),
            variantId = any(), rookieCard = any(), printRunRangeKey = any(), teamId = any(),
            pageable = any(Pageable::class.java)
        )).thenAnswer { invocation ->
            val pageable = invocation.getArgument<Pageable>(12) // 13th argument, index 12
            
            // Retrieve filter arguments to simulate filtering if needed for a more complex general mock
            // For now, this general mock does not filter, only sorts and paginates the full mockCards list.
            // Specific filter tests will override this mock.
            var cardsToProcess = mockCards.toList() // Make a mutable copy for sorting

            // Apply sorting from pageable
            if (pageable.sort.isSorted) {
                pageable.sort.forEach { order ->
                    val comparator = compareBy<Card> { card ->
                        when (order.property) {
                            "player.surname" -> card.player.surname
                            "id" -> card.id // id is Long, directly comparable
                            // Add other sortable properties as needed for tests
                            else -> null // Properties not explicitly handled won't be sorted
                        }
                    }
                    cardsToProcess = if (order.isDescending) {
                        cardsToProcess.sortedWith(comparator.reversed())
                    } else {
                        cardsToProcess.sortedWith(comparator)
                    }
                }
            }
            
            val start = pageable.offset.toInt()
            val end = (start + pageable.pageSize).coerceAtMost(cardsToProcess.size)
            val sublist = if (start >= cardsToProcess.size) emptyList() else cardsToProcess.subList(start, end)
            PageImpl(sublist, pageable, cardsToProcess.size.toLong())
        }
    }

    @Test
    fun `getCards should return first page with default size when no pagination params provided`() {
        val expectedPageSize = 20
        val expectedCards = mockCards.subList(0, min(expectedPageSize, mockCards.size))

        mockMvc.perform(get("/cards"))
            .andExpect(status().isOk)
            .andExpect(view().name("cards"))
            .andExpect(model().attribute("currentPage", 0))
            .andExpect(model().attribute("pageSize", expectedPageSize))
            .andExpect(model().attribute("totalPages", ceil(mockCards.size.toDouble() / expectedPageSize.toDouble()).toInt()))
            .andExpect(model().attribute("totalItems", mockCards.size.toLong()))
            .andExpect(model().attribute("cards", hasSize<Collection<Any>>(min(expectedPageSize, mockCards.size))))
            .andExpect(model().attribute("cards", expectedCards))
    }

    @Test
    fun `getCards should return specific page and size when params provided`() {
        val page = 1
        val size = 10
        val expectedCards = mockCards.subList(size * page, min(size * (page + 1), mockCards.size))


        mockMvc.perform(get("/cards?page=$page&size=$size"))
            .andExpect(status().isOk)
            .andExpect(view().name("cards"))
            .andExpect(model().attribute("currentPage", page))
            .andExpect(model().attribute("pageSize", size))
            .andExpect(model().attribute("totalPages", ceil(mockCards.size.toDouble() / size.toDouble()).toInt()))
            .andExpect(model().attribute("totalItems", mockCards.size.toLong()))
            .andExpect(model().attribute("cards", hasSize<Collection<Any>>(min(size, mockCards.size - (size * page) ))))
            .andExpect(model().attribute("cards", expectedCards))
    }

    @Test
    fun `getCards should return empty list for cards when page is out of bounds`() {
        val page = 100 // Well beyond the number of pages for 25 items
        val size = 10

        mockMvc.perform(get("/cards?page=$page&size=$size"))
            .andExpect(status().isOk)
            .andExpect(view().name("cards"))
            .andExpect(model().attribute("currentPage", page))
            .andExpect(model().attribute("pageSize", size))
            .andExpect(model().attribute("totalPages", ceil(mockCards.size.toDouble() / size.toDouble()).toInt()))
            .andExpect(model().attribute("totalItems", mockCards.size.toLong()))
            .andExpect(model().attribute("cards", hasSize<Collection<Any>>(0)))
    }

    @Test
    fun `getCards with filter should apply pagination to filtered subset`() {
        val filterManufacturerId = 1L
        val filteredMockCards = mockCards.filter { it.theme.brand.manufacturer.id == filterManufacturerId } // Cards with manufacturerId = 1L
        val page = 0
        val size = 3
        
        // Specific mock for this filter condition
        whenever(cardService.getCardsFiltered(
            manufacturerId = eq(filterManufacturerId), brandId = any(), themeId = any(), sportId = any(),
            playerId = any(), season = any(), gameUsed = any(), autograph = any(),
            variantId = any(), rookieCard = any(), printRunRangeKey = any(), teamId = any(),
            pageable = any(Pageable::class.java)
        )).thenAnswer { invocation ->
            val pageable = invocation.getArgument<Pageable>(12)
            // Apply sorting if any specified in pageable - for this test, none specified in URL
            var localFilteredCards = filteredMockCards.toList()
             if (pageable.sort.isSorted) { // Handle default sort if any applied by controller
                pageable.sort.forEach { order ->
                    val comparator = compareBy<Card> { card ->
                        when (order.property) {
                            "id" -> card.id
                            "player.surname" -> card.player.surname
                            else -> null
                        }
                    }
                    localFilteredCards = if (order.isDescending) localFilteredCards.sortedWith(comparator.reversed()) else localFilteredCards.sortedWith(comparator)
                }
            }
            val start = pageable.offset.toInt()
            val end = (start + pageable.pageSize).coerceAtMost(localFilteredCards.size)
            val sublist = if (start >= localFilteredCards.size) emptyList() else localFilteredCards.subList(start, end)
            PageImpl(sublist, pageable, localFilteredCards.size.toLong())
        }
        
        val expectedCards = filteredMockCards.subList(0, min(size, filteredMockCards.size))

        mockMvc.perform(get("/cards?manufacturerId=$filterManufacturerId&page=$page&size=$size"))
            .andExpect(status().isOk)
            .andExpect(view().name("cards"))
            .andExpect(model().attribute("currentPage", page))
            .andExpect(model().attribute("pageSize", size))
            .andExpect(model().attribute("totalPages", ceil(filteredMockCards.size.toDouble() / size.toDouble()).toInt()))
            .andExpect(model().attribute("totalItems", filteredMockCards.size.toLong()))
            .andExpect(model().attribute("cards", hasSize<Collection<Card>>(min(size, filteredMockCards.size))))
            .andExpect(model().attribute("cards", expectedCards))
    }

   /* @Test
    fun `getCards with sort parameter should pass sort to service and apply it`() {
        val pageableCaptor = ArgumentCaptor.forClass(Pageable::class.java)
        val page = 0
        val size = 7
        
        // The general mock in setUp already handles sorting.
        // We just need to verify the captured Pageable and the sorted output.
        val expectedSortedFirstPage = mockCards.sortedWith(compareBy { it.player.surname }).reversed().subList(page * size, min((page + 1) * size, mockCards.size))


        mockMvc.perform(get("/cards?sort=player.surname,desc&page=$page&size=$size"))
            .andExpect(status().isOk)
            .andExpect(view().name("cards"))
            .andExpect(model().attribute("currentPage", page))
            .andExpect(model().attribute("pageSize", size))
            .andExpect(model().attribute("totalPages", ceil(mockCards.size.toDouble() / size.toDouble()).toInt()))
            .andExpect(model().attribute("totalItems", mockCards.size.toLong()))
            .andExpect(model().attribute("cards", hasSize<Collection<Card>>(min(size, mockCards.size))))
            .andExpect(model().attribute("cards", expectedSortedFirstPage))

        verify(cardService).getCardsFiltered(
            manufacturerId = any(), brandId = any(), themeId = any(), sportId = any(),
            playerId = any(), season = any(), gameUsed = any(), autograph = any(),
            variantId = any(), rookieCard = any(), printRunRangeKey = any(), teamId = any(),
            pageable = capture(pageableCaptor)
        )

        val capturedPageable = pageableCaptor.value
        val sort = capturedPageable.sort
        assert(sort.isSorted) { "Sort should be applied" }
        val sortOrder = sort.getOrderFor("player.surname")
        assert(sortOrder != null) { "Sort order for 'player.surname' should exist" }
        assert(sortOrder!!.direction == Sort.Direction.DESC) { "Sort direction should be DESC" }
        assert(capturedPageable.pageNumber == page) { "Captured page number should be $page" }
        assert(capturedPageable.pageSize == size) { "Captured page size should be $size" }
    }

    */
}
