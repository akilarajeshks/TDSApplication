package com.zestworks.tdsapplication.repository

import com.zestworks.tdsapplication.model.Employee
import retrofit2.Response
import retrofit2.http.GET

interface EmployeeNetworkService{
    @GET("employees")
    suspend fun getEmployeeService() : Response<Employee>
}