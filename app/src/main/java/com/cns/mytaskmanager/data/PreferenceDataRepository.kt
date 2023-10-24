package com.cns.mytaskmanager.data

import androidx.lifecycle.LiveData

interface PreferenceDataRepository {

    /**
     * Add categories to datastore
     */
    suspend fun saveCategoryList(categoryList: String)

    /**
     * Get all categories saved in the datastore
     */
    suspend fun getCategoryList(): LiveData<String?>
}