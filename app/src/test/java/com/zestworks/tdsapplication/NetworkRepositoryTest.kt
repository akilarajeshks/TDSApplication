package com.zestworks.tdsapplication

import com.zestworks.tdsapplication.model.Employee
import com.zestworks.tdsapplication.repository.EmployeeNetworkService
import com.zestworks.tdsapplication.repository.NetworkRepository
import com.zestworks.tdsapplication.repository.NetworkResult
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.rxjava3.observers.TestObserver
import io.reactivex.rxjava3.schedulers.TestScheduler
import org.junit.Test
import retrofit2.Call
import retrofit2.Response
import java.util.concurrent.TimeUnit

class NetworkRepositoryTest {

    private val networkService: EmployeeNetworkService = mockk()
    private val testScheduler = TestScheduler()
    private val networkRepository = NetworkRepository(networkService, testScheduler)
    private val testObserver: TestObserver<NetworkResult> = mockk(relaxed = true)

    @Test
    fun checkPeriodOfNetworkCall(){
        val mockCall: Call<Employee> = mockk()
        every { networkService.getEmployees() } returns mockCall
        val mockResponse: Response<Employee> = mockk()
        every { mockCall.execute() } returns mockResponse
        every { mockResponse.body() } returns Employee(
            listOf(), ""
        )
        networkRepository.employeeDetailsStream().subscribe(testObserver)
        testScheduler.advanceTimeBy(10, TimeUnit.SECONDS)
        verify(exactly = 3) {
            testObserver.onNext(NetworkResult.Success(listOf()))
        }
    }
}