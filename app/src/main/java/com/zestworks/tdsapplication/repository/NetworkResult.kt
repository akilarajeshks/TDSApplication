package com.zestworks.tdsapplication.repository

import com.zestworks.tdsapplication.model.Data

sealed class NetworkResult{
    data class Success(val results : List<Data>) : NetworkResult()
    data class Error(val reason:String) :NetworkResult()
}