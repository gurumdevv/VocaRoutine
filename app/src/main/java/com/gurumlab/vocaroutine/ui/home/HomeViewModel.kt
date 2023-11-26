package com.gurumlab.vocaroutine.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gurumlab.vocaroutine.R
import com.gurumlab.vocaroutine.data.model.ListInfo
import com.gurumlab.vocaroutine.data.model.Review
import com.gurumlab.vocaroutine.data.model.onError
import com.gurumlab.vocaroutine.data.model.onException
import com.gurumlab.vocaroutine.data.model.onSuccess
import com.gurumlab.vocaroutine.data.source.remote.HomeRepository
import com.gurumlab.vocaroutine.ui.common.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.lang.Exception
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HomeRepository
) : ViewModel() {

    private val _reviewList = MutableLiveData<Event<ListInfo>>()
    val reviewList: LiveData<Event<ListInfo>> = _reviewList
    private val _isEmpty = MutableLiveData<Event<Boolean>>()
    val isEmpty: LiveData<Event<Boolean>> = _isEmpty
    private val _isFinish = MutableLiveData<Event<Boolean>>()
    val isFinish: LiveData<Event<Boolean>> = _isFinish
    private val _isLoading = MutableLiveData<Event<Boolean>>()
    val isLoading: LiveData<Event<Boolean>> = _isLoading
    private val _isCompleted = MutableLiveData<Event<Boolean>>()
    val isCompleted: LiveData<Event<Boolean>> = _isCompleted
    private val _isError = MutableLiveData<Event<Boolean>>()
    val isError: LiveData<Event<Boolean>> = _isError
    private val _snackbarMessage = MutableLiveData<Event<Int>>()
    val snackbarMessage: LiveData<Event<Int>> = _snackbarMessage
    private var listKey = ""
    private val currentDate = getToday()
    private val yesterday = getYesterday()

    fun loadLists() {
        _isLoading.value = Event(true)

        viewModelScope.launch {
            repository.deleteOutOfDateReviews(yesterday)

            val uid = repository.getUid()
            val reviewListIds = repository.getReviewListIds(currentDate)

            if (reviewListIds.isNotEmpty()) {
                val result = repository.getListsById(uid, reviewListIds.first())
                result.onSuccess { listInfo ->
                    _isLoading.value = Event(false)
                    _isCompleted.value = Event(true)

                    if (listInfo.isNotEmpty()) {
                        _reviewList.value = Event(listInfo.values.first())
                        listKey = listInfo.keys.first()
                    } else {
                        _isEmpty.value = Event(true)
                        repository.deleteAlarm(reviewListIds.first(), currentDate)
                    }
                }.onError { code, message ->
                    _isLoading.value = Event(false)
                    _isError.value = Event(true)

                    Log.d("HomeViewModel", "Error code: $code message: $message")
                }.onException { throwable ->
                    _isLoading.value = Event(false)
                    _isError.value = Event(true)

                    Log.d("HomeViewModel", "Exception: $throwable")
                }
            } else {
                _isLoading.value = Event(false)
                _isEmpty.value = Event(true)
            }
        }
    }

    fun finishReview() {
        viewModelScope.launch {
            val uid = repository.getUid()
            val alarmCode = repository.getAlarmCode(reviewList.value!!.content.id, currentDate)

            if (listKey.isNotEmpty()) {
                updateReviewCount(uid, listKey, alarmCode)
                _isFinish.value = Event(true)
            } else {
                _snackbarMessage.value = Event(R.string.review_count_update_error)
                _isFinish.value = Event(true)
            }
            repository.deleteAlarm(reviewList.value!!.content.id, currentDate)
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
                    _snackbarMessage.value = Event(R.string.review_count_update_error)
                }
            }

            1 -> {
                try {
                    val review = Review(
                        firstReview = reviewList.value!!.content.review.firstReview,
                        secondReview = true,
                        thirdReview = false
                    )
                    repository.updateSecondReviewCount(uid, listKey, review)
                } catch (e: Exception) {
                    _snackbarMessage.value = Event(R.string.review_count_update_error)
                }
            }

            2 -> {
                try {
                    val review = Review(
                        firstReview = reviewList.value!!.content.review.firstReview,
                        secondReview = reviewList.value!!.content.review.secondReview,
                        thirdReview = true
                    )
                    repository.updateThirdReviewCount(uid, listKey, review)
                } catch (e: Exception) {
                    _snackbarMessage.value = Event(R.string.review_count_update_error)
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