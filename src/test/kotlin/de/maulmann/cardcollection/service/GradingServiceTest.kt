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
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.*

@ExtendWith(MockitoExtension::class)
class GradingServiceTest {

    @Mock
    private lateinit var gradingRepository: GradingRepository

    @InjectMocks
    private lateinit var gradingService: GradingService

    @Test
    fun `saveGrading should save a valid grading`() {
        val grading = Grading(grade = 9.5f, gradingCompany = GradingCompany.PSA)
        whenever(gradingRepository.save(grading)).thenReturn(grading)

        val result = gradingService.saveGrading(grading)

        assertNotNull(result)
        assertEquals(grading.grade, result.grade)
        verify(gradingRepository).save(grading)
    }

    @Test
    fun `saveGrading should throw exception for invalid grade step`() {
        val grading = Grading(grade = 9.2f, gradingCompany = GradingCompany.PSA)

        assertThrows<IllegalArgumentException> {
            gradingService.saveGrading(grading)
        }
    }

    @Test
    fun `saveGrading should throw exception for grade out of range`() {
        val grading = Grading(grade = 5.0f, gradingCompany = GradingCompany.PSA)

        assertThrows<IllegalArgumentException> {
            gradingService.saveGrading(grading)
        }
    }

    @Test
    fun `findGradingById should return grading when found`() {
        val gradingId = 1L
        val grading = Grading(id = gradingId, grade = 10.0f, gradingCompany = GradingCompany.BGS)
        whenever(gradingRepository.findById(gradingId)).thenReturn(Optional.of(grading))

        val result = gradingService.findGradingById(gradingId)

        assertNotNull(result)
        assertEquals(gradingId, result?.id)
    }

    @Test
    fun `findGradingById should return null when not found`() {
        val gradingId = 1L
        whenever(gradingRepository.findById(gradingId)).thenReturn(Optional.empty())

        val result = gradingService.findGradingById(gradingId)

        assertNull(result)
    }

    @Test
    fun `deleteGrading should call repository delete when grading exists`() {
        val gradingId = 1L
        whenever(gradingRepository.existsById(gradingId)).thenReturn(true)

        gradingService.deleteGrading(gradingId)

        verify(gradingRepository).deleteById(gradingId)
    }

    @Test
    fun `deleteGrading should throw exception when grading does not exist`() {
        val gradingId = 1L
        whenever(gradingRepository.existsById(gradingId)).thenReturn(false)

        assertThrows<NoSuchElementException> {
            gradingService.deleteGrading(gradingId)
        }
    }

    @Test
    fun `isValidGrade should return true for valid grades`() {
        assertTrue(gradingService.isValidGrade(8.5f))
        assertTrue(gradingService.isValidGrade(10.0f))
        assertTrue(gradingService.isValidGrade(6.0f))
    }

    @Test
    fun `isValidGrade should return false for invalid grades`() {
        assertFalse(gradingService.isValidGrade(8.2f))
        assertFalse(gradingService.isValidGrade(5.5f))
        assertFalse(gradingService.isValidGrade(10.1f))
    }

    @Test
    fun `getAllGradingCompanies should return all enum values`() {
        val companies = gradingService.getAllGradingCompanies()
        assertEquals(GradingCompany.entries.size, companies.size)
    }
}
