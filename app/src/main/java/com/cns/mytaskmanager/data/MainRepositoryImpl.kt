package com.cns.mytaskmanager.data

import javax.inject.Inject

class MainRepositoryImpl @Inject constructor(private val apiService: ApiService) : MainRepository {
    override suspend fun getTaskList() = apiService.getTaskList()
}