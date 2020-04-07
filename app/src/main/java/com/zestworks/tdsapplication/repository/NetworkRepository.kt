package com.zestworks.tdsapplication.repository

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.Random
import java.util.concurrent.TimeUnit

class NetworkRepository(private val employeeNetworkService: EmployeeNetworkService) : Repository {

    override fun employeeDetailsStream(): Observable<NetworkResult> {
        return Observable.interval(
            0,
            5,
            TimeUnit.SECONDS
        ).observeOn(Schedulers.io())
            .map {
                return@map try {
                    val employeeService = employeeNetworkService.getEmployees().execute()
                    if (employeeService.isSuccessful) {
                        val body = employeeService.body()?.data
                        if (body == null) {
                            NetworkResult.Error("Response body is null")
                        }
                        NetworkResult.Success(body!!)
                    } else {
                        NetworkResult.Error("Network fetch failed")
                    }
                } catch (exception: Exception) {
                    NetworkResult.Error(exception.toString())
                }
            }
    }

    override fun emergencyStream(): Observable<Boolean> {
        val r = Random()
        val low = 1
        val high = 5
        var i1 = 0
        val arrayOf = arrayOf(true, true, false, true, false)
        return Observable
            .interval(0, (r.nextInt(high - low) + low).toLong(), TimeUnit.SECONDS)
            .map {
                i1++
                arrayOf[i1 % 4]
            }
    }
}
