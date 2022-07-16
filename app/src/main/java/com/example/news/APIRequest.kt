package com.example.news

import com.example.news.api.NewsAPIJson
import retrofit2.http.GET

interface APIRequest {

    @GET("latest-news?apiKey=KaUYmwu-fIPlohMiUxQNPmVGBmLO7_2W34M-JoL2GWLP2tuo")
    suspend fun getNews() : NewsAPIJson
}