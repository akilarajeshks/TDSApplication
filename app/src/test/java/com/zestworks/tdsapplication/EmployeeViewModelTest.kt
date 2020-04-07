package com.zestworks.tdsapplication

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.zestworks.tdsapplication.repository.NetworkResult
import com.zestworks.tdsapplication.repository.Repository
import com.zestworks.tdsapplication.view.EmployeeViewModel
import com.zestworks.tdsapplication.view.EmployeeViewState
import io.kotest.matchers.doubles.exactly
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verifyOrder
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
@ExperimentalCoroutinesApi
class EmployeeViewModelTest {

    private val repository: Repository = mockk()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var employeeViewModel: EmployeeViewModel

    private val testObserver: Observer<EmployeeViewState> = mockk(relaxed = true)

    private val testCoroutineDispatcher = TestCoroutineDispatcher()

    @Before
    fun setUp() {
        employeeViewModel = EmployeeViewModel(repository)
        employeeViewModel.currentState.observeForever(testObserver)
        Dispatchers.setMain(testCoroutineDispatcher)
    }

    @After
    fun reset() {
        Dispatchers.resetMain()
    }

    @Test
    fun testSimpleLoadingEmergency() = runBlockingTest {
        every { repository.emergencyStream() } answers {
            Observable.just(true)
        }
        every {
            repository.employeeDetailsStream()
        } answers { Observable.just(NetworkResult.Success(listOf())) }

        employeeViewModel.onUILoad()

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

        every {
            repository.employeeDetailsStream()
        } answers { Observable.just(NetworkResult.Success(listOf()), NetworkResult.Success(listOf())) }

        employeeViewModel.onUILoad()

        testCoroutineDispatcher.advanceTimeBy(10000)

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
        every { repository.emergencyStream() } answers {
            Observable.just(true,false)
        }
        every {
            repository.employeeDetailsStream()
        } answers { Observable.just(NetworkResult.Success(listOf())) }

        employeeViewModel.onUILoad()

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
        employeeViewModel.onUILoad()

        verifyOrder {
            testObserver.onChanged(EmployeeViewState.NetworkError(reason = errorMessage))
        }
    }
}