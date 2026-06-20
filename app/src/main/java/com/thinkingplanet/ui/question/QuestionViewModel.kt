package com.thinkingplanet.ui.question

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thinkingplanet.domain.model.Dimension
import com.thinkingplanet.domain.model.Question
import com.thinkingplanet.domain.repository.ProgressRepository
import com.thinkingplanet.domain.repository.QuestionRepository
import com.thinkingplanet.domain.usecase.CalculateStarsUseCase
import com.thinkingplanet.domain.usecase.UpdateMedalUseCase
import com.thinkingplanet.domain.usecase.UpdateTrophyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QuestionUiState(
    val question: Question?,
    val currentIndex: Int,
    val total: Int,
    val selectedIndex: Int?,
    val isAnswered: Boolean,
    val correctCount: Int,
    val isFinished: Boolean,
    val finalStars: Int,
    val showHint: Boolean
)

@HiltViewModel
class QuestionViewModel @Inject constructor(
    private val questionRepository: QuestionRepository,
    private val progressRepository: ProgressRepository,
    private val calculateStars: CalculateStarsUseCase,
    private val updateMedal: UpdateMedalUseCase,
    private val updateTrophy: UpdateTrophyUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(QuestionUiState(null, 0, 0, null, false, 0, false, 0, false))
    val state: StateFlow<QuestionUiState> = _state

    private var questions: List<Question> = emptyList()
    private var packId: String = ""
    private var dimension: Dimension = Dimension.MATH
    private var levelId: String = ""

    fun load(packId: String, dimensionName: String, levelId: String) {
        this.packId = packId
        this.dimension = Dimension.valueOf(dimensionName)
        this.levelId = levelId
        val levels = questionRepository.getLevels(packId)
        questions = levels[this.dimension]?.find { it.levelId == levelId }?.questions ?: emptyList()
        _state.value = _state.value.copy(
            question = questions.firstOrNull(), currentIndex = 0, total = questions.size,
            selectedIndex = null, isAnswered = false, correctCount = 0, isFinished = false, showHint = false
        )
    }

    fun selectAnswer(index: Int) {
        if (_state.value.isAnswered) return
        _state.value = _state.value.copy(selectedIndex = index, isAnswered = true)
    }

    fun showHint() { _state.value = _state.value.copy(showHint = true) }

    fun next() {
        val s = _state.value
        val wasCorrect = s.selectedIndex == questions[s.currentIndex].answerIndex
        val correct = s.correctCount + if (wasCorrect) 1 else 0
        val nextIndex = s.currentIndex + 1
        if (nextIndex >= questions.size) {
            val stars = calculateStars(correct, questions.size)
            viewModelScope.launch {
                progressRepository.saveLevelResult(packId, levelId, dimension, stars)
                val starsByLevel = progressRepository.getLevelStars(packId).first()
                val levels = questionRepository.getLevels(packId)[dimension] ?: emptyList()
                val avg = levels.map { (starsByLevel[it.levelId] ?: 0).toFloat() }.average().toFloat()
                val medal = updateMedal(avg)
                progressRepository.saveMedal(packId, dimension, medal)
                val allMedals = progressRepository.getMedals(packId).first().values.toList()
                val trophy = updateTrophy(allMedals)
                progressRepository.saveTrophy(packId, trophy)
            }
            _state.value = s.copy(correctCount = correct, isFinished = true, finalStars = stars)
        } else {
            _state.value = s.copy(
                currentIndex = nextIndex,
                question = questions[nextIndex],
                selectedIndex = null,
                isAnswered = false,
                correctCount = correct,
                showHint = false
            )
        }
    }
}
