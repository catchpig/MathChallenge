package com.thinkingplanet.ui.chapter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thinkingplanet.domain.model.*
import com.thinkingplanet.domain.repository.ProgressRepository
import com.thinkingplanet.domain.repository.QuestionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class DimensionUiState(
    val dimension: Dimension,
    val levels: List<LevelUiState>
)

data class LevelUiState(
    val levelId: String,
    val bestStars: Int,
    val totalQuestions: Int
)

@HiltViewModel
class ChapterViewModel @Inject constructor(
    private val questionRepository: QuestionRepository,
    private val progressRepository: ProgressRepository
) : ViewModel() {

    private val _packId = MutableStateFlow("")

    val dimensionStates: StateFlow<List<DimensionUiState>> = _packId
        .filter { it.isNotEmpty() }
        .flatMapLatest { packId ->
            progressRepository.getLevelStars(packId).map { starsMap ->
                val levels = questionRepository.getLevels(packId)
                Dimension.entries.mapNotNull { dim ->
                    val dimLevels = levels[dim] ?: return@mapNotNull null
                    DimensionUiState(dim, dimLevels.map { level ->
                        LevelUiState(level.levelId, starsMap[level.levelId] ?: 0, level.questions.size)
                    })
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun load(packId: String) { _packId.value = packId }
}
