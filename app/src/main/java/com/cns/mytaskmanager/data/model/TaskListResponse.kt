package com.cns.mytaskmanager.data.model

import com.google.gson.annotations.SerializedName

data class TaskListResponse(
    @SerializedName("todos") val todos: List<Todos>
) {
    data class Todos(
        @SerializedName("id") val id: Int = 0,
        @SerializedName("Title") val title: String = "",
        @SerializedName("Category") val category: String = "",
        @SerializedName("todo") val todo: String = "",
        @SerializedName("completed") val completed: Boolean = false,
        @SerializedName("userId") val userId: Int = 0,
        @SerializedName("date") val date: String = "",
        @SerializedName("priority") val priority: String = ""
    )
}