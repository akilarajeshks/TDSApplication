package com.zestworks.tdsapplication

import android.app.Application
import com.zestworks.tdsapplication.repository.EmployeeNetworkService
import com.zestworks.tdsapplication.repository.NetworkRepository
import com.zestworks.tdsapplication.repository.Repository
import com.zestworks.tdsapplication.view.EmployeeViewModel
import io.reactivex.rxjava3.schedulers.Schedulers
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class EmployeeApplication:Application() {

    override fun onCreate() {
        super.onCreate()

        val module = module {
            single { provideRetrofit() }
            single<Repository> {
                NetworkRepository(
                    get(),
                    Schedulers.io()
                )
            }
            viewModel { EmployeeViewModel(get()) }
        }
        startKoin {
            androidContext(this@EmployeeApplication)
            modules(module)
        }
    }

    private fun provideRetrofit() : EmployeeNetworkService {
        return Retrofit
            .Builder()
            .baseUrl("https://dummy.restapiexample.com/api/v1/")
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
            .create(EmployeeNetworkService::class.java)
    }
}