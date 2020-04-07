package com.zestworks.tdsapplication.repository

import io.reactivex.rxjava3.core.Observable

interface Repository {
    fun employeeDetailsStream(): Observable<NetworkResult>
    fun emergencyStream(): Observable<Boolean>
}