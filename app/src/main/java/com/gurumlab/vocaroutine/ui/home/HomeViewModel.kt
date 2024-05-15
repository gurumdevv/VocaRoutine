package com.gurumlab.vocaroutine.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.gurumlab.vocaroutine.R
import com.gurumlab.vocaroutine.data.model.ListInfo
import com.gurumlab.vocaroutine.data.model.Review
import com.gurumlab.vocaroutine.data.source.repository.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.lang.Exception
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HomeRepository,
    private val crashlytics: FirebaseCrashlytics
) : ViewModel() {

    val reviewList: StateFlow<List<ListInfo>> = loadLists().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _isEmpty: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isEmpty: StateFlow<Boolean> = _isEmpty

    private val _isFinish: MutableSharedFlow<Boolean> = MutableSharedFlow()
    val isFinish: SharedFlow<Boolean> = _isFinish

    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isError: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isError: StateFlow<Boolean> = _isError

    private val _snackbarMessage: MutableSharedFlow<Int> = MutableSharedFlow()
    val snackbarMessage: SharedFlow<Int> = _snackbarMessage

    private var listKey = ""
    private val currentDate = getToday()
    private val yesterday = getYesterday()

    private fun loadLists(): Flow<List<ListInfo>> = flow {
        repository.deleteOutOfDateReviews(yesterday)

        val uid = repository.getUid()
        val reviewListIds = repository.getReviewListIds(currentDate)

        if (reviewListIds.isNotEmpty()) {
            val list = repository.getListsById(
                uid,
                reviewListIds.first(),
                onComplete = { _isLoading.value = false },
                onError = {
                    _isError.value = true
                    if (!it.isNullOrBlank()) {
                        crashlytics.log(it)
                    }
                },
                onException = {
                    if (!it.isNullOrBlank()) {
                        crashlytics.log(it)
                    }
                }
            ).map { data ->
                listKey = data.keys.first()
                data.values.toList()
            }

            emitAll(list)
        } else {
            _isLoading.value = false
            _isEmpty.value = true
            emptyList<ListInfo>()
        }
    }

    fun finishReview() {
        viewModelScope.launch {
            val uid = repository.getUid()
            val alarmCode = repository.getAlarmCode(reviewList.value.first().id, currentDate)

            if (listKey.isNotBlank()) {
                updateReviewCount(uid, listKey, alarmCode)
                _isFinish.emit(true)
            } else {
                _snackbarMessage.emit(R.string.review_count_update_error)
                _isFinish.emit(true)
            }
            repository.deleteAlarm(reviewList.value.first().id, currentDate)
        }
    }

    private suspend fun updateReviewCount(uid: String, listKey: String, alarmCode: Int) {
        when (alarmCode % 10) {
            0 -> {
                try {
                    val review = Review(
                        firstReview = true,
                        secondReview = false,
                        thirdReview = false
                    )
                    repository.updateFirstReviewCount(uid, listKey, review)
                } catch (e: Exception) {
                    _snackbarMessage.emit(R.string.review_count_update_error)
                }
            }

            1 -> {
                try {
                    val review = Review(
                        firstReview = reviewList.value.first().review.firstReview,
                        secondReview = true,
                        thirdReview = false
                    )
                    repository.updateSecondReviewCount(uid, listKey, review)
                } catch (e: Exception) {
                    _snackbarMessage.emit(R.string.review_count_update_error)
                }
            }

            2 -> {
                try {
                    val review = Review(
                        firstReview = reviewList.value.first().review.firstReview,
                        secondReview = reviewList.value.first().review.secondReview,
                        thirdReview = true
                    )
                    repository.updateThirdReviewCount(uid, listKey, review)
                } catch (e: Exception) {
                    _snackbarMessage.emit(R.string.review_count_update_error)
                }
            }
        }
    }

    private fun getYesterday(): String {
        val yesterday = LocalDate.now().minusDays(1)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        return yesterday.format(formatter)
    }

    private fun getToday(): String {
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        return currentDate.format(formatter)
    }
}