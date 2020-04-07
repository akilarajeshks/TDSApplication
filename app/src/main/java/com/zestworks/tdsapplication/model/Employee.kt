package com.zestworks.tdsapplication.model

import com.google.gson.annotations.SerializedName

data class Employee(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("status")
    val status: String
)

data class Data(
    @SerializedName("employee_age")
    val employeeAge: String,
    @SerializedName("employee_name")
    val employeeName: String,
    @SerializedName("employee_salary")
    val employeeSalary: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("profile_image")
    val profileImage: String
)

