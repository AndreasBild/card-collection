package de.maulmann.cardcollection.model

import jakarta.persistence.*
import jakarta.validation.constraints.DecimalMax
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotNull

@Entity
 class Grading(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @field:NotNull(message = "Grade cannot be null")
    @field:DecimalMin(value = "6.0", message = "Grade must be at least 6.0")
    @field:DecimalMax(value = "10.0", message = "Grade must be at most 10.0")
    // Further validation for 0.5 steps will be in the service layer or a custom validator
    var grade: Float?,

    @field:NotNull(message = "Grading company cannot be null")
    @Enumerated(EnumType.STRING)
    var gradingCompany: GradingCompany?
) {
    val displayGrade: String
        get() {
            val currentGrade = grade ?: return ""
            val currentCompany = gradingCompany ?: return ""
            val gradeValStr = if (currentCompany == GradingCompany.PSA && currentGrade % 1.0f == 0.0f) {
                currentGrade.toInt().toString()
            } else {
                currentGrade.toString()
            }
            return "$currentCompany $gradeValStr"
        }
}
