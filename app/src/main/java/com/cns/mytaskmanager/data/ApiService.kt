package com.cns.mytaskmanager.data

import com.cns.mytaskmanager.data.model.TaskListResponse
import retrofit2.Response
import retrofit2.http.*

interface ApiService{
    /**
     * API call to get all tasks from the server
     */
    @GET("970ec59d-1762-492b-90c0-2e60fa2d1bb4")
    suspend fun getTaskList() : Response<TaskListResponse>
}