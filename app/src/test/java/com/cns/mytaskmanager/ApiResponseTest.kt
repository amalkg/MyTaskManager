package com.cns.mytaskmanager

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.HttpURLConnection

class ApiResponseTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: ApiServiceTest

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        apiService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiServiceTest::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun testApiCall() {
        val mockResponse = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(
                """
                {
                  "todos": [
                    {
                        "id":2,
                        "Title":"Memorize",
                        "Category":"Personal",
                        "todo":"Memorize the fifty states and their capitals",
                        "completed":false,
                        "userId":48,
                        "date":"12-10-2023",
                        "priority":"High"
                    }
                  ]
                }
                """.trimIndent()
            )

        mockWebServer.enqueue(mockResponse)

        val response = apiService.getTaskList().execute()
        val todoResponse = response.body()
        // Add assertions to test the response data
        assertNotNull(todoResponse)
        assertEquals(1, todoResponse?.todos?.size)
        val todo = todoResponse?.todos?.get(0)
        assertEquals(2, todo?.id)
        assertEquals("Memorize", todo?.title)
        assertEquals("Personal", todo?.category)
        assertEquals("Memorize the fifty states and their capitals", todo?.todo)
        assertTrue(todo?.completed == false)
        assertEquals(48, todo?.userId)
        assertEquals("12-10-2023", todo?.date)
        assertEquals("High", todo?.priority)
    }
}