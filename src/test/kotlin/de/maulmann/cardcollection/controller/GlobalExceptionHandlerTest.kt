package de.maulmann.cardcollection.controller

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.ui.ConcurrentModel

class GlobalExceptionHandlerTest {

    private val handler = GlobalExceptionHandler()

    @Test
    fun `handleBadRequest returns JSON error response for api request`() {
        val request = MockHttpServletRequest("GET", "/api/filters/brands")
        val model = ConcurrentModel()
        val exception = IllegalArgumentException("Invalid parameter")

        val result = handler.handleBadRequest(exception, request, model)

        assertTrue(result is ResponseEntity<*>)
        val response = result as ResponseEntity<GlobalExceptionHandler.ErrorResponse>
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals("Invalid parameter", response.body?.message)
        assertEquals("/api/filters/brands", response.body?.path)
    }

    @Test
    fun `handleBadRequest redirects for browser request`() {
        val request = MockHttpServletRequest("GET", "/cards")
        val model = ConcurrentModel()
        val exception = IllegalArgumentException("Bad input")

        val view = handler.handleBadRequest(exception, request, model)

        assertEquals("redirect:/cards", view)
        assertEquals("Bad input", model["error"])
    }

    @Test
    fun `handleNotFound returns 404 for api request`() {
        val request = MockHttpServletRequest("GET", "/api/cards/999")
        val model = ConcurrentModel()
        val exception = NoSuchElementException("Card not found")

        val result = handler.handleNotFound(exception, request, model)

        assertTrue(result is ResponseEntity<*>)
        val response = result as ResponseEntity<GlobalExceptionHandler.ErrorResponse>
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals("Card not found", response.body?.message)
    }

    @Test
    fun `handleGeneralException returns 500 for api request`() {
        val request = MockHttpServletRequest("GET", "/api/export/data")
        val model = ConcurrentModel()
        val exception = RuntimeException("Database timeout")

        val result = handler.handleGeneralException(exception, request, model)

        assertTrue(result is ResponseEntity<*>)
        val response = result as ResponseEntity<GlobalExceptionHandler.ErrorResponse>
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode)
        assertEquals("An unexpected error occurred", response.body?.message)
    }
}
