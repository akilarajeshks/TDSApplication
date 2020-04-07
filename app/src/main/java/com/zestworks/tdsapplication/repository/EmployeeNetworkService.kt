package com.zestworks.tdsapplication.repository

import com.zestworks.tdsapplication.model.Employee
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET

interface EmployeeNetworkService{
    @GET("employees")
    fun getEmployees() : Call<Employee>
}