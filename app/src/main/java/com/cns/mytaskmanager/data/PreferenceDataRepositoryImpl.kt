package com.cns.mytaskmanager.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap


const val DataStore_NAME = "CategoryList"

val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = DataStore_NAME)

class PreferenceDataRepositoryImpl(private val context: Context) : PreferenceDataRepository {
    companion object {
        var CATEGORY_NAME = stringPreferencesKey("CATEGORY_NAME")
    }

    /**
     * Add categories to datastore
     */
    override suspend fun saveCategoryList(categoryList: String) {
        context.datastore.edit {
            it[CATEGORY_NAME] = categoryList
        }
    }

    /**
     * Get all categories saved in the datastore
     */
    override suspend fun getCategoryList(): LiveData<String?> {
        return context.datastore.data.asLiveData()
            .switchMap { MutableLiveData(it[CATEGORY_NAME]) }
    }
}