package com.example.ui.ai

import com.example.BuildConfig
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@JsonClass(generateAdapter = true)
data class GenerateContentRequest(
    val contents: List<Content>,
    val generationConfig: GenerationConfig? = null
)

@JsonClass(generateAdapter = true)
data class Content(
    val parts: List<Part>
)

@JsonClass(generateAdapter = true)
data class Part(
    val text: String
)

@JsonClass(generateAdapter = true)
data class GenerationConfig(
    val temperature: Float? = null,
    val responseMimeType: String? = null
)

@JsonClass(generateAdapter = true)
data class GenerateContentResponse(
    val candidates: List<Candidate>? = null
)

@JsonClass(generateAdapter = true)
data class Candidate(
    val content: Content? = null
)

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse
}

object RetrofitGeminiClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val service: GeminiApiService by lazy {
        val moshi = Moshi.Builder().build()
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
        retrofit.create(GeminiApiService::class.java)
    }
}

object GeminiService {
    suspend fun generateTaskChecklist(
        title: String,
        description: String,
        groupName: String
    ): List<String> = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            // Return simulation list as a graceful fallback if key is missing/placeholder
            return@withContext listOf(
                "Perform in-depth market research on $title and identify user pain points.",
                "Detail minimum viable product (MVP) features and user flows.",
                "Draw low-fidelity whiteboard sketching for mobile view mockups.",
                "Evaluate existing competitor strategies and pricing models in $groupName.",
                "Outline structured timeline deadlines and milestone sprints."
            )
        }

        val prompt = """
            You are a brilliant, actionable AI Coach. Help the user break down their project into a clear, highly-detailed list of step-by-step actionable sub-tasks to get started instantly.
            
            Project details:
            - Title: $title
            - Description: $description
            - Group/Category: $groupName
            
            Output a JSON list of exactly 4 to 6 specific, extremely practical, highly actionable starter sub-tasks (one short, impact-oriented sentence per item, without bullet numbers). Use friendly but professional, direct action verbs.
            Do not provide any conversational introduction, notes, or wrap text. Output ONLY the JSON array of strings!
            Example format:
            ["Conduct target audience survey to understand pain points", "Map out core database schema for project tasks"]
        """.trimIndent()

        val request = GenerateContentRequest(
            contents = listOf(
                Content(parts = listOf(Part(text = prompt)))
            ),
            generationConfig = GenerationConfig(
                temperature = 0.2f,
                responseMimeType = "application/json"
            )
        )

        try {
            val response = RetrofitGeminiClient.service.generateContent(apiKey, request)
            val rawText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            if (!rawText.isNullOrBlank()) {
                parseJsonArray(rawText)
            } else {
                throw Exception("Response content is empty")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback content on errors / issues
            listOf(
                "Deconstruct requirements for $title to outline core functional criteria.",
                "Research common design standards and patterns within $groupName apps.",
                "Interview 3 potential users to gather direct feedback on usability.",
                "Create a step-by-step execution timeline from start to delivery."
            )
        }
    }

    private fun parseJsonArray(jsonString: String): List<String> {
        val cleanJson = jsonString.trim()
            .removePrefix("```json")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()

        return try {
            val moshi = Moshi.Builder().build()
            val listType = Types.newParameterizedType(List::class.java, String::class.java)
            val adapter = moshi.adapter<List<String>>(listType)
            adapter.fromJson(cleanJson) ?: emptyList()
        } catch (e: Exception) {
            // Manual fallback parsing in case model formats output slightly differently
            jsonString.lines()
                .map { it.replace(Regex("^[-*\\d.\\s\\[\\]]+"), "").trim().trim('"').trim() }
                .filter { it.isNotEmpty() && !it.startsWith("[") && !it.startsWith("]") }
        }
    }
}
