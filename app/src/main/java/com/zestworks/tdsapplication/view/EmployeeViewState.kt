package com.zestworks.tdsapplication.view

sealed class EmployeeViewState {
    data class Emergency(
        val total: Int,
        val numOfPeopleUnder18: Int,
        val numOfPeopleBetween18And60: Int,
        val numOfPeopleAbove60: Int
    ) : EmployeeViewState()

    object NoEmergency : EmployeeViewState()

    object NetworkError:EmployeeViewState()
}