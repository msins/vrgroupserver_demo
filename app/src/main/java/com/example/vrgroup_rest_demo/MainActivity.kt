package com.example.vrgroup_rest_demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vrgroup_rest_demo.model.AnswerDto
import com.example.vrgroup_rest_demo.model.GameDto
import com.example.vrgroup_rest_demo.model.User
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.UUID
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var currentGameDto: GameDto
    private lateinit var service: ServerService
    private val messages: ArrayList<String> = ArrayList()
    private val adapter: RecyclerViewAdapter = RecyclerViewAdapter(messages)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        logger.adapter = adapter
        logger.layoutManager = LinearLayoutManager(this)
        logger.setHasFixedSize(true)

        getBtn.setOnClickListener {
            postBtn.isEnabled = false
            val url = "http://${ip.text}/api/"

            println(url)
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(url)
                .client(OkHttpClient())
                .build()

            service = retrofit.create(ServerService::class.java)

            service.getGameDto(game_name.text.toString()).enqueue(object : Callback<GameDto> {
                override fun onFailure(call: Call<GameDto>, t: Throwable) {
                    addMessage(t.message.toString())
                }

                override fun onResponse(call: Call<GameDto>, response: Response<GameDto>) {
                    if (response.body() != null) {
                        currentGameDto = response.body()!!
                        postBtn.isEnabled = true
                    }
                    addMessage("GET: $currentGameDto")
                }
            })
        }

        postBtn.setOnClickListener {
            try {
                val random = UUID.randomUUID().toString()
                val scenarioDto = currentGameDto.scenarioDtos.random()
                val question = scenarioDto.questions.random()
                val answerResponse = AnswerDto(
                    User(
                        random,
                        "$random@fer.hr",
                        "MFO".random().toString(),
                        Random.nextInt(18, 64)
                    ),
                    scenarioDto.scenario,
                    question,
                    question.choices.random()
                )

                service.submitAnswer(game_name.text.toString(), answerResponse)
                    .enqueue(object : Callback<Void> {
                        override fun onFailure(call: Call<Void>, t: Throwable) {
                            addMessage(t.message.toString())
                        }

                        override fun onResponse(call: Call<Void>, response: Response<Void>) {
                            addMessage(response.message())
                        }
                    })
            } catch (e : NoSuchElementException) {
                addMessage("Json is valid but the scenario has no questions.")
            }
        }

    }

    fun addMessage(message: String) {
        messages.add(message)
        adapter.notifyItemInserted(adapter.itemCount - 1)
        logger.smoothScrollToPosition(adapter.itemCount - 1)
    }
}
