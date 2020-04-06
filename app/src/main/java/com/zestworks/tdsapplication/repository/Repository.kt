package com.zestworks.tdsapplication.repository

import com.zestworks.tdsapplication.model.Data
import io.reactivex.rxjava3.core.Observable

interface Repository {
    suspend fun fetchEmployeeDetails(): NetworkResult
    fun emergencyStream(): Observable<Boolean>

}