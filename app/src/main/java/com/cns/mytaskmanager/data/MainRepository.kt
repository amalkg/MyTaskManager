package com.cns.mytaskmanager.data

import com.cns.mytaskmanager.data.model.TaskListResponse
import retrofit2.Response

interface MainRepository {
    suspend fun getTaskList(): Response<TaskListResponse>
}