package com.mordaka.arkadiusz

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.junit.jupiter.SpringExtension


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class TaskControllerIntegrationTest {

    private val restTemplate = TestRestTemplate()

    @LocalServerPort
    protected var port = 0

    private fun url() = "http://localhost:$port/api"

    @ParameterizedTest
    @MethodSource("parameters")
    fun `should return correct aggregated transactions`(parameter: String, expectedTransactions: Int) {
        //when
        val response = restTemplate
            .withBasicAuth(DEFAULT_USER, DEFAULT_PASSWORD)
            .getForEntity(url() + parameter, List::class.java)

        //then
        assertEquals(200, response.statusCode.value())
        assertNotNull(response.body)
        assertEquals(expectedTransactions, response.body?.size)
    }

    @ParameterizedTest
    @ValueSource(strings = ["", "?customer_id=ALL", "?customer_id=5", "?customer_id=1,3,5"])
    fun `should not allow to return transactions for a non-authenticated user`(path: String) {
        //when
        val response = restTemplate
            .getForEntity(url() + path, Object::class.java)

        //then
        assertEquals(401, response.statusCode.value())
    }

    @ParameterizedTest
    @ValueSource(strings = ["", "?customer_id=ALL", "?customer_id=5", "?customer_id=1,3,5"])
    fun `should not allow to return transactions when password is incorrect`(path: String) {
        //when
        val response = restTemplate
            .withBasicAuth(DEFAULT_USER, "bad")
            .getForEntity(url() + path, Object::class.java)

        //then
        assertEquals(401, response.statusCode.value())
    }

    companion object {
        private const val DEFAULT_PASSWORD = "pass"
        private const val DEFAULT_USER = "user"

        @JvmStatic
        fun parameters(): List<Arguments> {
            return listOf(
                Arguments.of("", 5),
                Arguments.of("?customer_id=ALL", 5),
                Arguments.of("?customer_id=5", 1),
                Arguments.of("?customer_id=1,3,5", 3)
            )
        }
    }
}
