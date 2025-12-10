package de.maulmann.cardcollection.repository

import de.maulmann.cardcollection.model.Grading
import de.maulmann.cardcollection.model.GradingCompany
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest
class GradingRepositoryTest {

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var repository: GradingRepository

    @Test
    fun `should save and find a grading`() {
        val grading = Grading(grade = 9.5f, gradingCompany = GradingCompany.PSA)
        val savedGrading = repository.save(grading)

        entityManager.flush()
        entityManager.clear()

        val foundGrading = repository.findById(savedGrading.id).orElse(null)

        assertNotNull(foundGrading)
        assertEquals(9.5f, foundGrading!!.grade)
        assertEquals(GradingCompany.PSA, foundGrading.gradingCompany)
    }
}
