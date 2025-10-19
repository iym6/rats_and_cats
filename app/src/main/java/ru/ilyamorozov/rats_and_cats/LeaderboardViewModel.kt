package ru.ilyamorozov.rats_and_cats

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class LeaderboardViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ScoreRepository

    init {
        val dao = AppDatabase.getDatabase(application).scoreDao()
        repository = ScoreRepository(dao)
    }

    val allScores: Flow<List<ScoreRecord>> = repository.allScores

    private val _top5Remote = MutableLiveData<List<RemoteRecord>>()
    val top5Remote: LiveData<List<RemoteRecord>> = _top5Remote

    fun loadTop5() {
        viewModelScope.launch {
            try {
                val top5 = repository.getTop5Remote()
                _top5Remote.value = top5
            } catch (e: Exception) {
                // Обработка ошибки, например, Toast в Fragment
            }
        }
    }

    fun insertScore(score: ScoreRecord) {
        viewModelScope.launch {
            repository.insert(score)
        }
    }
}