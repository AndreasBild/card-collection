package de.maulmann.cardcollection.controller

import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

@ControllerAdvice
class GlobalExceptionHandler {

    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    data class ErrorResponse(
        val status: Int,
        val error: String,
        val message: String,
        val path: String
    )

    @ExceptionHandler(MethodArgumentTypeMismatchException::class, IllegalArgumentException::class)
    fun handleBadRequest(ex: Exception, request: HttpServletRequest, model: Model): Any {
        logger.warn("Bad request on {}: {}", request.requestURI, ex.message)
        val uri = request.requestURI
        if (uri.startsWith("/api") || uri.startsWith("/export")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorResponse(
                    status = HttpStatus.BAD_REQUEST.value(),
                    error = "Bad Request",
                    message = ex.message ?: "Invalid request parameters",
                    path = uri
                )
            )
        }
        model.addAttribute("error", ex.message ?: "Invalid request parameter")
        return "redirect:/cards"
    }

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNotFound(ex: NoSuchElementException, request: HttpServletRequest, model: Model): Any {
        logger.warn("Resource not found on {}: {}", request.requestURI, ex.message)
        val uri = request.requestURI
        if (uri.startsWith("/api") || uri.startsWith("/export")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ErrorResponse(
                    status = HttpStatus.NOT_FOUND.value(),
                    error = "Not Found",
                    message = ex.message ?: "Requested resource not found",
                    path = uri
                )
            )
        }
        model.addAttribute("error", ex.message ?: "Requested resource not found")
        return "redirect:/cards"
    }

    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleGeneralException(ex: Exception, request: HttpServletRequest, model: Model): Any {
        logger.error("Unhandled exception on {}: {}", request.requestURI, ex.message, ex)
        val uri = request.requestURI
        if (uri.startsWith("/api") || uri.startsWith("/export")) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ErrorResponse(
                    status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    error = "Internal Server Error",
                    message = "An unexpected error occurred",
                    path = uri
                )
            )
        }
        model.addAttribute("error", "An unexpected error occurred. Please try again.")
        return "redirect:/cards"
    }
}
