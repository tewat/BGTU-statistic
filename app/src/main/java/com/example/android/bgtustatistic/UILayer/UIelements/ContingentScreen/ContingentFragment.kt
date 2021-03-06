package com.example.android.bgtustatistic.UILayer.UIelements.ContingentScreen

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.distinctUntilChanged
import com.example.android.bgtustatistic.DataLayer.ContingentScreen.ContingentApi
import com.example.android.bgtustatistic.DataLayer.ContingentScreen.ContingentRemoteDataSource
import com.example.android.bgtustatistic.DataLayer.ContingentScreen.ContingentRepository
import com.example.android.bgtustatistic.DataLayer.LoginFeature.LoginApi
import com.example.android.bgtustatistic.DataLayer.LoginFeature.LoginRemoteDataSource
import com.example.android.bgtustatistic.DataLayer.LoginFeature.LoginRepository
import com.example.android.bgtustatistic.DataLayer.RetrofitBuilder.ServiceBuilder
import com.example.android.bgtustatistic.UILayer.UIelements.NoDataFragment
import com.example.android.bgtustatistic.databinding.FragmentMovementBinding
import kotlinx.coroutines.Dispatchers

class ContingentFragment : Fragment() {
    private var _binding: FragmentMovementBinding? = null
    private lateinit var binding: FragmentMovementBinding
    private val viewModel: ContingentViewModel by activityViewModels {
        ContingentViewModelFactory(
            ContingentRepository(
                dataSource = ContingentRemoteDataSource(
                    contingentApi = ServiceBuilder.buildService(ContingentApi::class.java),
                    ioDispatcher = Dispatchers.IO
                )
            ),
            LoginRepository(
                loginRemoteDataSource = LoginRemoteDataSource(
                    loginApi = ServiceBuilder.buildService(LoginApi::class.java),
                    ioDispatcher = Dispatchers.IO
                )
            )
        )
    }
    private var isNoDataDisplaying = true
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovementBinding.inflate(inflater)
        binding = _binding!!
        viewModel.fetchContingent()
        binding.apply {
            updateMovButton.setOnClickListener {
                viewModel.updateToken()
            }
            settingsButton.setOnClickListener {
                showSettingsBottomSheet()
            }
        }
        viewModel.uiStateContingent.value?.contingentList?.let {
            isNoDataDisplaying = false
            setFragment(ContingentPlotsFragment())
        }?:let {
            isNoDataDisplaying = true
            setFragment(NoDataFragment())
        }
        return binding.root
    }
    private fun setFragment(fragment: Fragment){
        childFragmentManager.beginTransaction()
            .replace(binding.movContainer.id, fragment)
            .commit()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.uiStateContingent.distinctUntilChanged().observe(requireActivity()){ state ->
            if(state.relogined){
                viewModel.fetchContingent()
            }
            state.contingentList?.let {
                if(!isAdded) return@let
                if(state.contingentListFiltered?.isEmpty() == true){
                    isNoDataDisplaying = true
                    setFragment(NoDataFragment())
                }
                else
                    if(isNoDataDisplaying){
                        isNoDataDisplaying = false
                        setFragment(ContingentPlotsFragment())
                    }
            }
        }
        viewModel.uiStateSettings.distinctUntilChanged().observe(requireActivity()){state ->
            viewModel.filterContingent()
        }
        viewModel.uiStateContingent.distinctUntilChanged().observe(requireActivity()){
            Log.e("distinctLiveData", "here")
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
    private fun showSettingsBottomSheet(){
        val bottomSheet = SettingsBottomSheet()

        bottomSheet.show(childFragmentManager, bottomSheet.tag)
    }

}