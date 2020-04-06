package com.zestworks.tdsapplication.view

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.zestworks.tdsapplication.R
import kotlinx.android.synthetic.main.fragment_employee_risk.*
import org.koin.android.viewmodel.ext.android.viewModel


class EmployeeRiskFragment : Fragment() {

    val employeeViewModel : EmployeeViewModel by viewModel()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_employee_risk, container, false)
    }

    override fun onStart() {
        super.onStart()

        employeeViewModel.currentState.observe(this, Observer {
            when(it){
                is EmployeeViewState.Emergency -> {
                    emergency.text=getString(R.string.emergency)
                    emergency.setTextColor(Color.RED)
                    emergency_view_group.visibility = View.VISIBLE
                    total.text=it.total.toString()
                    ageLessThan18.text=it.numOfPeopleUnder18.toString()
                    ageBetween18And60.text=it.numOfPeopleBetween18And60.toString()
                    ageAbove60.text = it.numOfPeopleAbove60.toString()
                }
                EmployeeViewState.NoEmergency -> {
                    emergency.text=getString(R.string.no_emergency)
                    emergency.setTextColor(Color.GREEN)
                    emergency_view_group.visibility=View.INVISIBLE
                }
                EmployeeViewState.NetworkError ->{

                }
            }
        })
        employeeViewModel.onUILoad()
    }
}