package com.zestworks.tdsapplication.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zestworks.tdsapplication.repository.NetworkResult
import com.zestworks.tdsapplication.repository.Repository
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class EmployeeViewModel(private val repository: Repository) : ViewModel() {

    private val _currentState = MutableLiveData<EmployeeViewState>()
    val currentState = _currentState as LiveData<EmployeeViewState>

    private val compositeDisposable = CompositeDisposable()

    fun onUILoad() {
        var refreshJob: Job? = null
        compositeDisposable.add(
            repository
                .emergencyStream()
                .subscribe { emergency ->
                    if (emergency) {
                        if (refreshJob == null) {
                            refreshJob = viewModelScope.launch {
                                while (isActive) {
                                    when (val employeeNetworkResult =
                                        repository.fetchEmployeeDetails()) {
                                        is NetworkResult.Success -> {
                                            val results = employeeNetworkResult.results
                                            val emergencyViewState = EmployeeViewState.Emergency(
                                                total = results.size,
                                                numOfPeopleAbove60 = results.count { it.employeeAge!!.toInt() > 60 },
                                                numOfPeopleBetween18And60 = results.count { it.employeeAge!!.toInt() in 18..60 },
                                                numOfPeopleUnder18 = results.count { it.employeeAge!!.toInt() < 18 })
                                            _currentState.postValue(emergencyViewState)
                                            delay(5000)
                                        }
                                        is NetworkResult.Error -> {
                                            _currentState.postValue(EmployeeViewState.NetworkError)
                                        }
                                        is NetworkResult.Cancelled -> {

                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        refreshJob?.cancel()
                        refreshJob = null
                        _currentState.postValue(EmployeeViewState.NoEmergency)
                    }

                }
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}