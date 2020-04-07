package com.zestworks.tdsapplication

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.zestworks.tdsapplication.repository.NetworkResult
import com.zestworks.tdsapplication.repository.Repository
import com.zestworks.tdsapplication.view.EmployeeViewModel
import com.zestworks.tdsapplication.view.EmployeeViewState
import io.mockk.every
import io.mockk.mockk
import io.mockk.verifyOrder
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class EmployeeViewModelTest {

    private val repository: Repository = mockk()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var employeeViewModel: EmployeeViewModel

    private val testObserver: Observer<EmployeeViewState> = mockk(relaxed = true)

    @Before
    fun setUp() {

    }

    @Test
    fun testSimpleLoadingEmergency() {
        every { repository.emergencyStream() } answers {
            Observable.just(true)
        }
        every {
            repository.employeeDetailsStream()
        } answers { Observable.just(NetworkResult.Success(listOf())) }

        employeeViewModel = EmployeeViewModel(repository)
        employeeViewModel.currentState.observeForever(testObserver)

        verifyOrder {
            testObserver.onChanged(
                EmployeeViewState.Emergency(
                    total = 0,
                    numOfPeopleUnder18 = 0,
                    numOfPeopleBetween18And60 = 0,
                    numOfPeopleAbove60 = 0
                )
            )
        }
    }

    @Test
    fun testNetworkCallInEmergency(){
        every { repository.emergencyStream() } answers {
            Observable.just(true)
        }
        val mockEmployeeDetails = PublishSubject.create<NetworkResult>()
        every {
            repository.employeeDetailsStream()
        } answers { mockEmployeeDetails }

        employeeViewModel = EmployeeViewModel(repository)
        employeeViewModel.currentState.observeForever(testObserver)

        mockEmployeeDetails.onNext(NetworkResult.Success(listOf()))
        mockEmployeeDetails.onNext(NetworkResult.Success(listOf()))

        verifyOrder {
            testObserver.onChanged(
                EmployeeViewState.Emergency(
                    total = 0,
                    numOfPeopleUnder18 = 0,
                    numOfPeopleBetween18And60 = 0,
                    numOfPeopleAbove60 = 0
                )
            )
            testObserver.onChanged(
                EmployeeViewState.Emergency(
                    total = 0,
                    numOfPeopleUnder18 = 0,
                    numOfPeopleBetween18And60 = 0,
                    numOfPeopleAbove60 = 0
                )
            )
        }
    }

    @Test
    fun testNoEmergency(){
        val mockEmergencies = PublishSubject.create<Boolean>()
        every { repository.emergencyStream() } answers {
            mockEmergencies
        }
        every {
            repository.employeeDetailsStream()
        } answers { Observable.just(NetworkResult.Success(listOf())) }

        employeeViewModel = EmployeeViewModel(repository)
        employeeViewModel.currentState.observeForever(testObserver)

        mockEmergencies.onNext(true)
        mockEmergencies.onNext(false)

        verifyOrder {
            testObserver.onChanged( EmployeeViewState.Emergency(
                total = 0,
                numOfPeopleUnder18 = 0,
                numOfPeopleBetween18And60 = 0,
                numOfPeopleAbove60 = 0
            ))
            testObserver.onChanged(EmployeeViewState.NoEmergency)
        }
    }

    @Test
    fun testNetworkFailureResponse(){
        val errorMessage = "Network Failure"
        every { repository.emergencyStream() } answers { Observable.just(true)}
        every {
            repository.employeeDetailsStream()
        } answers { Observable.just(NetworkResult.Error(errorMessage)) }
        employeeViewModel = EmployeeViewModel(repository)
        employeeViewModel.currentState.observeForever(testObserver)

        verifyOrder {
            testObserver.onChanged(EmployeeViewState.NetworkError(reason = errorMessage))
        }
    }
}