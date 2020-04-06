package com.zestworks.tdsapplication

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.zestworks.tdsapplication.repository.NetworkResult
import com.zestworks.tdsapplication.repository.Repository
import com.zestworks.tdsapplication.view.EmployeeViewModel
import com.zestworks.tdsapplication.view.EmployeeViewState
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

class EmployeeViewModelTest {

    private val repository: Repository = mockk()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var employeeViewModel: EmployeeViewModel

    private val testObserver: Observer<EmployeeViewState> = mockk(relaxed = true)

    private val testCoroutineDispatcher = TestCoroutineDispatcher()
    @ExperimentalCoroutinesApi
    @Before
    fun setUp() {
        employeeViewModel = EmployeeViewModel(repository)
        employeeViewModel.currentState.observeForever(testObserver)
        Dispatchers.setMain(testCoroutineDispatcher)
    }

    @ExperimentalCoroutinesApi
    @After
    fun reset() {
        Dispatchers.resetMain()
    }

    @Test
    fun testSimpleLoading() = runBlockingTest {
        every { repository.emergencyStream() } answers {
            Observable.just(true)
        }

        coEvery {
            repository.fetchEmployeeDetails()
        } returns NetworkResult.Success(listOf())

        employeeViewModel.onUILoad()

        verifyOrder {
            testObserver.onChanged(EmployeeViewState.Emergency(
                total = 0,
                numOfPeopleUnder18 = 0,
                numOfPeopleBetween18And60 = 0,
                numOfPeopleAbove60 = 0
            ))
        }

    }
}