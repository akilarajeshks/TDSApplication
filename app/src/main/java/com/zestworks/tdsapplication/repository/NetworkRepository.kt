package com.zestworks.tdsapplication.repository

import io.reactivex.rxjava3.core.Observable
import java.util.*
import java.util.concurrent.CancellationException
import java.util.concurrent.TimeUnit


class NetworkRepository(private val employeeNetworkService: EmployeeNetworkService) :
    Repository {
    override suspend fun fetchEmployeeDetails(): NetworkResult {
        return try {
            val employeeService = employeeNetworkService.getEmployeeService()

            if (employeeService.isSuccessful) {
                val body = employeeService.body()?.data
                if (body == null) {
                    NetworkResult.Error("Response body is null")
                }
                NetworkResult.Success(body!!)
            } else {
                NetworkResult.Error("Network fetch failed")
            }
        } catch (e: CancellationException) {
            NetworkResult.Cancelled
        } catch (exception: Exception) {
            NetworkResult.Error(exception.toString())
        }
    }

    override fun emergencyStream(): Observable<Boolean> {
        val r = Random()
        val low = 10
        val high = 15
        val result: Long = (r.nextInt(high - low) + low).toLong()
        var i1 = 0
        val arrayOf = arrayOf(true, true, false, true, false)
        return Observable
            .interval(0, result, TimeUnit.SECONDS)
            .map { i ->
                i1++
                arrayOf[i1 % 4]
            }
    }
}
