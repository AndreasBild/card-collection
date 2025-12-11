package de.maulmann.cardcollection.model

import jakarta.validation.Validation
import jakarta.validation.Validator
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GradingTest {

    private lateinit var validator: Validator

    @BeforeEach
    fun setUp() {
        val factory = Validation.buildDefaultValidatorFactory()
        validator = factory.validator
    }

    @Test
    fun `test valid grading`() {
        val grading = Grading(grade = 8.5f, gradingCompany = GradingCompany.PSA)
        val violations = validator.validate(grading)
        assertTrue(violations.isEmpty())
    }

    @Test
    fun `test grade too low`() {
        val grading = Grading(grade = 5.5f, gradingCompany = GradingCompany.PSA)
        val violations = validator.validate(grading)
        assertEquals(1, violations.size)
        assertEquals("Grade must be at least 6.0", violations.first().message)
    }

    @Test
    fun `test grade too high`() {
        val grading = Grading(grade = 10.5f, gradingCompany = GradingCompany.PSA)
        val violations = validator.validate(grading)
        assertEquals(1, violations.size)
        assertEquals("Grade must be at most 10.0", violations.first().message)
    }

    @Test
    fun `test null grading company`() {
        val grading = Grading(grade = 8.0f, gradingCompany = null)
        val violations = validator.validate(grading)
        assertEquals(1, violations.size)
        assertEquals("Grading company cannot be null", violations.first().message)
    }

    @Test
    fun `test null grade`() {
        val grading = Grading(grade = null, gradingCompany = GradingCompany.PSA)
        val violations = validator.validate(grading)
        assertEquals(1, violations.size)
        assertEquals("Grade cannot be null", violations.first().message)
    }
}