package com.cns.mytaskmanager

import com.cns.mytaskmanager.data.model.TaskListResponse
import retrofit2.Call
import retrofit2.http.*

interface ApiServiceTest {
    @GET("v1/970ec59d-1762-492b-90c0-2e60fa2d1bb4")
    fun getTaskList(): Call<TaskListResponse>
}