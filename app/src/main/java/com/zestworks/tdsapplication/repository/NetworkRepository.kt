package com.zestworks.tdsapplication.repository

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.Random
import java.util.concurrent.TimeUnit

class NetworkRepository(
    private val employeeNetworkService: EmployeeNetworkService,
    private val scheduler: Scheduler
) : Repository {

    override fun employeeDetailsStream(): Observable<NetworkResult> {
        return Observable.interval(
            0,
            5,
            TimeUnit.SECONDS,
            scheduler
        ).observeOn(Schedulers.io())
            .map {
                return@map try {
                    val employeeService = employeeNetworkService.getEmployees()
                    NetworkResult.Success(employeeService.execute().body()!!.data)
                } catch (exception: Exception) {
                    NetworkResult.Error(exception.toString())
                }
            }
    }

    override fun emergencyStream(): Observable<Boolean> {
        val r = Random()
        val low = 30
        val high = 90
        return Observable
            .interval(0, (r.nextInt(high - low) + low).toLong(), TimeUnit.SECONDS)
            .map {
                r.nextBoolean()
            }
    }
}
