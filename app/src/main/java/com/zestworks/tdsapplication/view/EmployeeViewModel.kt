package com.zestworks.tdsapplication.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zestworks.tdsapplication.repository.NetworkResult
import com.zestworks.tdsapplication.repository.Repository
import io.reactivex.rxjava3.disposables.CompositeDisposable

class EmployeeViewModel(private val repository: Repository) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private var refreshLoopDisposable = CompositeDisposable()

    private val _currentState = MutableLiveData<EmployeeViewState>()
    val currentState = _currentState as LiveData<EmployeeViewState>

    init {
        compositeDisposable.add(
            repository.emergencyStream().distinctUntilChanged().subscribe { isEmergency ->
                if (isEmergency) {
                    refreshLoopDisposable.add(repository.employeeDetailsStream().subscribe { networkResult ->
                        when (networkResult) {
                            is NetworkResult.Success -> {
                                val results = networkResult.results
                                val emergencyViewState =
                                    EmployeeViewState.Emergency(
                                        total = results.size,
                                        numOfPeopleAbove60 = results.count { it.employeeAge.toInt() > 60 },
                                        numOfPeopleBetween18And60 = results.count { it.employeeAge.toInt() in 18..60 },
                                        numOfPeopleUnder18 = results.count { it.employeeAge.toInt() < 18 })
                                _currentState.postValue(emergencyViewState)
                            }
                            is NetworkResult.Error -> _currentState.postValue(EmployeeViewState.NetworkError(networkResult.reason))
                        }
                    })
                } else {
                    _currentState.postValue(EmployeeViewState.NoEmergency)
                    refreshLoopDisposable.dispose()
                    refreshLoopDisposable = CompositeDisposable()

                }
            }
        )
    }

    override fun onCleared() {
        super.onCleared()
        refreshLoopDisposable.dispose()
        compositeDisposable.dispose()
    }
}