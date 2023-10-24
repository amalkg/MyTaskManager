package com.cns.mytaskmanager.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


const val DataStore_NAME = "CategoryList"

val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = DataStore_NAME)

class PreferenceDataRepositoryImpl(private val context: Context) : PreferenceDataRepository {
    companion object {
        var CATEGORY_NAME = stringPreferencesKey("CATEGORY_NAME")
    }

    override suspend fun saveCategoryList(categoryList: String) {
        context.datastore.edit {
            it[CATEGORY_NAME] = categoryList
        }
    }

    override suspend fun getCategoryList(): Flow<String?> {
        return context.datastore.data.map {
            it[CATEGORY_NAME]
        }
    }
}