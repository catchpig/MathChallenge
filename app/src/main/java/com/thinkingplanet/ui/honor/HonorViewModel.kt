package com.thinkingplanet.ui.honor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thinkingplanet.domain.model.*
import com.thinkingplanet.domain.repository.ProgressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class HonorViewModel @Inject constructor(private val progressRepository: ProgressRepository) : ViewModel() {

    val medals: StateFlow<List<Medal>> = progressRepository.getAllMedals()
        .map { list -> list.map { (packId, dimension, level) -> Medal(packId, dimension, level) } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val trophies: StateFlow<List<Pair<String, TrophyLevel>>> = progressRepository.getAllTrophies()
        .map { it.entries.map { e -> e.key to e.value } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val currentTitle: StateFlow<PlayerTitle> = progressRepository.getTotalStars()
        .map { stars ->
            when {
                stars >= 500 -> PlayerTitle.LEGEND
                stars >= 200 -> PlayerTitle.ALL_ROUNDER
                stars >= 100 -> PlayerTitle.STAR_COLLECTOR
                else -> PlayerTitle.NONE
            }
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, PlayerTitle.NONE)
}
