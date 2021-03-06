package com.example.news

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.news.adapters.RecyclerAdapter
import com.example.news.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL = "https://api.currentsapi.services/v1/"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    lateinit var countdownTimer: CountDownTimer
    private var seconds = 3L

    private var titlesList = mutableListOf<String>()
    private var descList = mutableListOf<String>()
    private var imagesList = mutableListOf<String>()
    private var linksList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        makeAPIRequest()
    }

    private fun fadeIn() {
        binding.vBlackScreen.animate().apply {
            alpha(0f)
            duration = 3000
        }.start()
    }


    private fun makeAPIRequest() {
        binding.progressBar.visibility = View.VISIBLE

        val api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(APIRequest::class.java)

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = api.getNews()

                for (article in response.news) {
                    Log.d("MainActivity", "Result + $article")
                    addToList(article.title, article.description, article.image, article.url)
                }

                withContext(Dispatchers.Main) {
                    setUpRecyclerView()
                    fadeIn()
                    binding.progressBar.visibility = View.GONE
                }
            } catch (e: Exception) {
                Log.d("MainActivity", e.toString())
                withContext(Dispatchers.Main) {
                    attemptRequestAgain(seconds)

                }
            }
        }
    }

    private fun attemptRequestAgain(seconds: Long) {
        countdownTimer = object : CountDownTimer(seconds * 1010, 1000) {
            override fun onFinish() {
                makeAPIRequest()
                countdownTimer.cancel()
                binding.tvNoInternetCountDown.visibility = View.GONE
                this@MainActivity.seconds += 3
            }

            override fun onTick(millisUntilFinished: Long) {
                binding.tvNoInternetCountDown.visibility = View.VISIBLE
                binding.tvNoInternetCountDown.text =
                    "Cannot retrieve data...\nTrying again in: ${millisUntilFinished / 1000}"
                Log.d(
                    "MainActivity",
                    "Could not retrieve data. Trying again in ${millisUntilFinished / 1000} seconds"
                )
            }
        }
        countdownTimer.start()
    }

    private fun setUpRecyclerView() {
        binding.rvRecyclerView.layoutManager = LinearLayoutManager(applicationContext)
        binding.rvRecyclerView.adapter =
            RecyclerAdapter(titlesList, descList, imagesList, linksList)
    }

    //adds the items to our recyclerview
    private fun addToList(title: String, description: String, image: String, link: String) {
        linksList.add(link)
        titlesList.add(title)
        descList.add(description)
        imagesList.add(image)
    }
}