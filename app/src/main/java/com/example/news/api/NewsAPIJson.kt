package com.example.news.api

data class NewsAPIJson(
    val news: List<New>,
    val page: Int,
    val status: String
)