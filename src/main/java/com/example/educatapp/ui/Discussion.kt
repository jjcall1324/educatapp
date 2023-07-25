package com.example.educatapp.ui

class Discussion(
    val title: String?,
    val content: String?,
    val author: String?,
    val timestamp: String // Change the type to String
) {
    // Add an empty constructor required for Firebase
    constructor() : this("", "", "", "")
}

