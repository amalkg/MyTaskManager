package com.cns.mytaskmanager.data

import com.cns.mytaskmanager.data.model.TaskListResponse
import retrofit2.Response

interface MainRepository {
    /**
     * Get all tasks from the server
     */
    suspend fun getTaskList(): Response<TaskListResponse>
}