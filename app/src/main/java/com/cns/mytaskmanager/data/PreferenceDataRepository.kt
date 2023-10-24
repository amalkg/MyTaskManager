package com.cns.mytaskmanager.data

import kotlinx.coroutines.flow.Flow

interface PreferenceDataRepository {

    suspend fun saveCategoryList(categoryList: String)

    suspend fun getCategoryList(): Flow<String?>
}