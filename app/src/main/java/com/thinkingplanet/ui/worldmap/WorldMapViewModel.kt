package com.thinkingplanet.ui.worldmap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thinkingplanet.domain.model.*
import com.thinkingplanet.domain.repository.ProgressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class WorldUiState(
    val theme: WorldTheme,
    val upperPackId: String,
    val lowerPackId: String,
    val upperTrophy: TrophyLevel,
    val lowerTrophy: TrophyLevel,
    val isUnlocked: Boolean
)

@HiltViewModel
class WorldMapViewModel @Inject constructor(
    private val progressRepository: ProgressRepository
) : ViewModel() {

    val worldStates: StateFlow<List<WorldUiState>> = progressRepository.getAllTrophies()
        .map { trophies -> buildWorldStates(trophies) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, buildWorldStates(emptyMap()))

    val totalStars: StateFlow<Int> = progressRepository.getTotalStars()
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    private fun buildWorldStates(trophies: Map<String, TrophyLevel>): List<WorldUiState> {
        return WorldTheme.entries.mapIndexed { index, theme ->
            val upperPack = "grade${theme.grade}_upper"
            val lowerPack = "grade${theme.grade}_lower"
            WorldUiState(
                theme = theme,
                upperPackId = upperPack,
                lowerPackId = lowerPack,
                upperTrophy = trophies[upperPack] ?: TrophyLevel.NONE,
                lowerTrophy = trophies[lowerPack] ?: TrophyLevel.NONE,
                isUnlocked = index == 0 || trophies.getOrDefault("grade${index}_upper", TrophyLevel.NONE) != TrophyLevel.NONE
            )
        }
    }
}
