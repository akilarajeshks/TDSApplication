package com.zestworks.tdsapplication.view

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.zestworks.tdsapplication.R
import kotlinx.android.synthetic.main.fragment_employee_risk.*
import org.koin.android.viewmodel.ext.android.viewModel

class EmployeeRiskFragment : Fragment(R.layout.fragment_employee_risk) {

    private val employeeViewModel: EmployeeViewModel by viewModel()

    private val render = Observer<EmployeeViewState> {
        when (it) {
            is EmployeeViewState.Emergency -> {
                emergency.text = getString(R.string.emergency)
                emergency.setTextColor(Color.RED)
                emergency_view_group.visibility = View.VISIBLE
                total.text = it.total.toString()
                ageLessThan18.text = it.numOfPeopleUnder18.toString()
                ageBetween18And60.text = it.numOfPeopleBetween18And60.toString()
                ageAbove60.text = it.numOfPeopleAbove60.toString()
                emergency.visibility = View.VISIBLE
                error_text_view.visibility = View.GONE
            }
            EmployeeViewState.NoEmergency -> {
                emergency.text = getString(R.string.no_emergency)
                emergency.setTextColor(Color.GREEN)
                emergency_view_group.visibility = View.GONE
                emergency.visibility = View.VISIBLE
                error_text_view.visibility = View.GONE
            }
            is EmployeeViewState.NetworkError -> {
                error_text_view.visibility = View.VISIBLE
                emergency_view_group.visibility = View.GONE
                emergency.visibility = View.GONE
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        employeeViewModel.currentState.observe(viewLifecycleOwner, render)
    }
}