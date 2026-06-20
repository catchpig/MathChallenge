package com.thinkingplanet.data.source

import android.content.Context
import com.thinkingplanet.domain.model.Dimension
import com.thinkingplanet.domain.model.Level
import com.thinkingplanet.domain.model.Question
import com.thinkingplanet.domain.model.QuestionType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import javax.inject.Inject

@Serializable
private data class PackJson(
    @SerialName("pack_id") val packId: String,
    @SerialName("grade") val grade: Int,
    @SerialName("is_upper") val isUpper: Boolean,
    @SerialName("dimensions") val dimensions: Map<String, List<LevelJson>>
)

@Serializable
private data class LevelJson(
    @SerialName("level_id") val levelId: String,
    @SerialName("questions") val questions: List<QuestionJson>
)

@Serializable
private data class QuestionJson(
    @SerialName("id") val id: String,
    @SerialName("type") val type: String,
    @SerialName("text") val text: String,
    @SerialName("image_res") val imageRes: String? = null,
    @SerialName("options") val options: List<String>,
    @SerialName("answer_index") val answerIndex: Int,
    @SerialName("hint") val hint: String
)

class QuestionPackLoader @Inject constructor(private val context: Context) {
    private val json = Json { ignoreUnknownKeys = true }

    fun load(packId: String): Map<Dimension, List<Level>> {
        val raw = context.assets.open("packs/$packId.json").bufferedReader().readText()
        val pack = json.decodeFromString<PackJson>(raw)
        return pack.dimensions.mapNotNull { (key, levels) ->
            val dimension = Dimension.entries.find { it.name == key } ?: return@mapNotNull null
            val domainLevels = levels.map { levelJson ->
                Level(
                    levelId = levelJson.levelId,
                    dimension = dimension,
                    questions = levelJson.questions.map { q ->
                        Question(
                            id = q.id,
                            type = QuestionType.valueOf(q.type),
                            text = q.text,
                            imageRes = q.imageRes,
                            options = q.options,
                            answerIndex = q.answerIndex,
                            hint = q.hint
                        )
                    }
                )
            }
            dimension to domainLevels
        }.toMap()
    }
}
