package de.maulmann.cardcollection.service

import de.maulmann.cardcollection.model.Grading
import de.maulmann.cardcollection.model.GradingCompany
import de.maulmann.cardcollection.repository.GradingRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import java.util.*

@ExtendWith(MockitoExtension::class)
class GradingServiceTest {

    @Mock
    private lateinit var gradingRepository: GradingRepository

    @InjectMocks
    private lateinit var gradingService: GradingService

    @Test
    fun `saveGrading should save valid grading`() {
        val grading = Grading(grade = 9.5f, gradingCompany = GradingCompany.PSA)
        `when`(gradingRepository.save(any<Grading>())).thenReturn(grading.copy(id = 1L))

        val savedGrading = gradingService.saveGrading(grading)

        assertNotNull(savedGrading.id)
        assertEquals(9.5f, savedGrading.grade)
        assertEquals(GradingCompany.PSA, savedGrading.gradingCompany)
    }

    @Test
    fun `saveGrading should throw exception for grade below minimum`() {
        val grading = Grading(grade = 5.5f, gradingCompany = GradingCompany.PSA)
        val exception = assertThrows<IllegalArgumentException> {
            gradingService.saveGrading(grading)
        }
        assertEquals("Grade must be between 6.0 and 10.0.", exception.message)
    }

    @Test
    fun `saveGrading should throw exception for grade above maximum`() {
        val grading = Grading(grade = 10.5f, gradingCompany = GradingCompany.BGS)
        val exception = assertThrows<IllegalArgumentException> {
            gradingService.saveGrading(grading)
        }
        assertEquals("Grade must be between 6.0 and 10.0.", exception.message)
    }

    @Test
    fun `saveGrading should throw exception for grade not in 0_5 steps`() {
        val grading = Grading(grade = 7.2f, gradingCompany = GradingCompany.PSA)
        val exception = assertThrows<IllegalArgumentException> {
            gradingService.saveGrading(grading)
        }
        assertEquals("Grade must be in 0.5 steps (e.g., 6.0, 6.5, 7.0).", exception.message)
    }

    @Test
    fun `isValidGrade should return true for valid grades`() {
        assertTrue(gradingService.isValidGrade(6.0f))
        assertTrue(gradingService.isValidGrade(7.5f))
        assertTrue(gradingService.isValidGrade(10.0f))
    }

    @Test
    fun `isValidGrade should return false for invalid grades`() {
        assertFalse(gradingService.isValidGrade(5.5f)) // Below min
        assertFalse(gradingService.isValidGrade(10.5f)) // Above max
        assertFalse(gradingService.isValidGrade(7.2f)) // Incorrect step
        assertFalse(gradingService.isValidGrade(6.0001f)) // Edge case for precision
    }

    @Test
    fun `updateGrading should update existing grading`() {
        val existingGrading = Grading(id = 1L, grade = 8.0f, gradingCompany = GradingCompany.PSA)
        val updatedDetails = Grading(grade = 9.0f, gradingCompany = GradingCompany.BGS)

        `when`(gradingRepository.findById(1L)).thenReturn(Optional.of(existingGrading))
        `when`(gradingRepository.save(any<Grading>())).thenAnswer { invocation ->
            invocation.getArgument(0) as Grading
        }

        val result = gradingService.updateGrading(1L, updatedDetails)

        assertEquals(1L, result.id)
        assertEquals(9.0f, result.grade)
        assertEquals(GradingCompany.BGS, result.gradingCompany)
    }

    @Test
    fun `updateGrading should throw if grading not found`() {
        val updatedDetails = Grading(grade = 9.0f, gradingCompany = GradingCompany.BGS)
        `when`(gradingRepository.findById(1L)).thenReturn(Optional.empty())

        assertThrows<NoSuchElementException> {
            gradingService.updateGrading(1L, updatedDetails)
        }
    }
}
