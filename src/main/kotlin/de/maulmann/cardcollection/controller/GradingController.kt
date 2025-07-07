package de.maulmann.cardcollection.controller

import de.maulmann.cardcollection.service.GradingService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/gradings") // Base path, though no public endpoints yet
class GradingController(private val gradingService: GradingService) {

    // No public @GetMapping or @PostMapping methods for now.
    // These could be added later if direct manipulation of Grading entities
    // via HTTP requests is needed (e.g., for an admin interface or AJAX calls).

    // Example of what could be added later:
    /*
    @GetMapping("/new")
    fun showCreateGradingForm(model: Model): String {
        model.addAttribute("grading", Grading(grade = 0.0f, gradingCompany = GradingCompany.PSA)) // Default empty
        model.addAttribute("gradingCompanies", gradingService.getAllGradingCompanies())
        return "grading-form" // A Thymeleaf template for creating/editing gradings
    }

    @PostMapping
    fun createGrading(@Valid @ModelAttribute("grading") grading: Grading, bindingResult: BindingResult, model: Model): String {
        if (!gradingService.isValidGrade(grading.grade)) {
            bindingResult.addError(FieldError("grading", "grade", "Grade must be between 6.0 and 10.0, in 0.5 steps."))
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("gradingCompanies", gradingService.getAllGradingCompanies())
            return "grading-form"
        }
        gradingService.saveGrading(grading)
        return "redirect:/some-success-page" // Or wherever appropriate
    }
    */
}
