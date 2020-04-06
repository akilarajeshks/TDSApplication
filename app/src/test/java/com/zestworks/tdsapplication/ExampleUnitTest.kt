package com.zestworks.tdsapplication

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.zestworks.tdsapplication.repository.NetworkResult
import com.zestworks.tdsapplication.repository.Repository
import com.zestworks.tdsapplication.view.EmployeeViewModel
import com.zestworks.tdsapplication.view.EmployeeViewState
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.android.plugins.RxAndroidPlugins
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.Callable


class ExampleUnitTest {

    private val repository: Repository = mockk()
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var employeeViewModel: EmployeeViewModel


    @ExperimentalCoroutinesApi
    @Before
    fun setUp() {
        employeeViewModel = EmployeeViewModel(repository)
        Dispatchers.setMain(TestCoroutineDispatcher())
    }

    @Test
    fun test() {
        every { repository.emergencyStream() }.answers {
            Observable.just(true)
        }

        every {
            runBlocking {
                repository.fetchEmployeeDetails()
            }
        }.returns(NetworkResult.Success(listOf()))

        employeeViewModel.onUILoad()

        assert(employeeViewModel.currentState.value is EmployeeViewState.Emergency)
    }

    @ExperimentalCoroutinesApi
    @After
    fun reset(){
        Dispatchers.resetMain()
    }
}