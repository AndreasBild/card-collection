package de.maulmann.cardcollection.service

import de.maulmann.cardcollection.model.Grading
import de.maulmann.cardcollection.model.GradingCompany
import de.maulmann.cardcollection.repository.GradingRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import jakarta.validation.Valid

@Service
class GradingService(private val gradingRepository: GradingRepository) {

    @Transactional
    fun saveGrading(@Valid grading: Grading): Grading {
        validateGradeSteps(grading.grade)
        // Basic validation for company is handled by enum type safety
        // and @NotNull on the entity.
        return gradingRepository.save(grading)
    }

    fun findGradingById(id: Long): Grading? {
        return gradingRepository.findById(id).orElse(null)
    }

    @Transactional
    fun updateGrading(id: Long, updatedGrading: Grading): Grading {
        val existingGrading = gradingRepository.findById(id)
            .orElseThrow { NoSuchElementException("Grading not found with id: $id") }

        validateGradeSteps(updatedGrading.grade)

        // Create a new instance with updated fields
        val newGradingInstance = existingGrading.copy(
            grade = updatedGrading.grade,
            gradingCompany = updatedGrading.gradingCompany
        )
        return gradingRepository.save(newGradingInstance)
    }

    @Transactional
    fun deleteGrading(id: Long) {
        if (!gradingRepository.existsById(id)) {
            throw NoSuchElementException("Grading not found with id: $id")
        }
        // Consider implications: if a Card references this Grading,
        // the Card's grading_id should be set to null.
        // This is handled by ON DELETE SET NULL in the FK constraint.
        gradingRepository.deleteById(id)
    }

    private fun validateGradeSteps(grade: Float?) {
        if (grade == null) {
            // This should ideally be caught by @NotNull validation, but as a safeguard:
            throw IllegalArgumentException("Grade cannot be null for validation.")
        }
        if (grade < 6.0f || grade > 10.0f) {
            throw IllegalArgumentException("Grade must be between 6.0 and 10.0.")
        }
        // Check if the grade is a multiple of 0.5
        // (grade * 10) % 5 == 0 means it's like X.0 or X.5
        if ((grade * 10).rem(5).toInt() != 0) {
            throw IllegalArgumentException("Grade must be in 0.5 steps (e.g., 6.0, 6.5, 7.0).")
        }
    }

    fun isValidGrade(grade: Float?): Boolean {
        if (grade == null) return false
        if (grade < 6.0f || grade > 10.0f) return false
        return (grade * 10).rem(5).toInt() == 0
    }

    fun getAllGradingCompanies(): List<GradingCompany> {
        return GradingCompany.entries
    }
}
