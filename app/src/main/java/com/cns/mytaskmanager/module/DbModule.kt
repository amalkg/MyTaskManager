package com.cns.mytaskmanager.module

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.cns.mytaskmanager.TodoList
import com.cns.mytaskmanager.data.DataStoreRepository
import com.cns.mytaskmanager.data.DefaultDataRepository
import com.google.protobuf.InvalidProtocolBufferException
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.InputStream
import java.io.OutputStream

@Module
@InstallIn(SingletonComponent::class)
object DbModule {
    object TodoListSerializer : Serializer<TodoList> {
        override val defaultValue: TodoList = TodoList.getDefaultInstance()

        override suspend fun readFrom(input: InputStream): TodoList {
            try {
                return TodoList.parseFrom(input)
            } catch (exception: InvalidProtocolBufferException) {
                throw CorruptionException("Cannot read proto.", exception)
            } catch (e: java.io.IOException) {
                e.printStackTrace()
                throw e
            }
        }

        override suspend fun writeTo(t: TodoList, output: OutputStream) = t.writeTo(output)
    }

    val Context.todoListDataStore: DataStore<TodoList> by dataStore(
        fileName = "TodoList.pb",
        serializer = TodoListSerializer
    )

    @Provides
    @Reusable
    fun provideProtoDataStore(@ApplicationContext context: Context) =
        context.todoListDataStore

    @Provides
    @Reusable
    internal fun providesDataRepository(
        @ApplicationContext context: Context,
        todoListDataStore: DataStore<TodoList>
    ): DataStoreRepository {
        return DefaultDataRepository(
            context,
            todoListDataStore
        )
    }
}