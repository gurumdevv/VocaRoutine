package com.gurumlab.vocaroutine.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gurumlab.vocaroutine.data.model.ListInfo
import com.gurumlab.vocaroutine.data.model.onError
import com.gurumlab.vocaroutine.data.model.onException
import com.gurumlab.vocaroutine.data.model.onSuccess
import com.gurumlab.vocaroutine.data.source.remote.HomeRepository
import com.gurumlab.vocaroutine.ui.common.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
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
                    } else {
                        _isEmpty.value = Event(true)
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
        _isFinish.value = Event(true)
        viewModelScope.launch {
            repository.deleteAlarm(reviewList.value!!.content.id, currentDate)
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