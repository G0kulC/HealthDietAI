package com.healthdietapp.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.healthdietapp.R
import com.healthdietapp.data.model.RecommendationRequest
import com.healthdietapp.data.model.toParcelable
import com.healthdietapp.databinding.FragmentProfileBinding
import com.healthdietapp.utils.NetworkResult
import com.healthdietapp.utils.hide
import com.healthdietapp.utils.show
import com.healthdietapp.utils.showErrorSnackbar
import com.healthdietapp.viewmodel.RecommendationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RecommendationViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDropdowns()
        setupSliders()
        setupButton()
        observeState()
    }

    private fun setupDropdowns() {
        fun setAdapter(viewId: Int, items: List<String>) {
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, items)
            when (viewId) {
                1 -> binding.actvGender.setAdapter(adapter)
                2 -> binding.actvFamilyHistory.setAdapter(adapter)
                3 -> binding.actvFavc.setAdapter(adapter)
                4 -> binding.actvCaec.setAdapter(adapter)
                5 -> binding.actvSmoke.setAdapter(adapter)
                6 -> binding.actvScc.setAdapter(adapter)
                7 -> binding.actvCalc.setAdapter(adapter)
                8 -> binding.actvMtrans.setAdapter(adapter)
                9 -> binding.actvNcp.setAdapter(adapter)
                10 -> binding.actvTue.setAdapter(adapter)
            }
        }
        setAdapter(1, listOf("Male", "Female"))
        setAdapter(2, listOf("yes", "no"))
        setAdapter(3, listOf("yes", "no"))
        setAdapter(4, listOf("no", "Sometimes", "Frequently", "Always"))
        setAdapter(5, listOf("yes", "no"))
        setAdapter(6, listOf("yes", "no"))
        setAdapter(7, listOf("no", "Sometimes", "Frequently", "Always"))
        setAdapter(8, listOf("Automobile", "Bike", "Motorbike", "Public_Transportation", "Walking"))
        setAdapter(9, listOf("1", "2", "3", "4", "5", "6"))
        setAdapter(10, listOf("0", "1", "2", "3", "4", "5"))
    }

    private fun setupSliders() {
        binding.sliderPhysicalActivity.addOnChangeListener { _, value, _ ->
            binding.tvPhysicalActivityValue.text = "Value: $value"
        }
        binding.sliderWaterIntake.addOnChangeListener { _, value, _ ->
            binding.tvWaterIntakeValue.text = "Value: ${value}L"
        }
    }

    private fun setupButton() {
        binding.btnGetRecommendation.setOnClickListener {
            if (validateFields()) {
                val request = buildRequest()
                viewModel.getRecommendation(request)
            }
        }
    }

    private fun validateFields(): Boolean {
        var valid = true
        val fields = listOf(
            binding.etAge to binding.tilAge,
            binding.etHeight to binding.tilHeight,
            binding.etWeight to binding.tilWeight
        )
        fields.forEach { (et, til) ->
            if (et.text.toString().trim().isEmpty()) {
                til.error = "This field is required"
                valid = false
            } else {
                til.error = null
            }
        }
        val dropdowns = listOf(
            binding.actvGender to binding.tilGender,
            binding.actvFamilyHistory to binding.tilFamilyHistory,
            binding.actvFavc to binding.tilFavc,
            binding.actvCaec to binding.tilCaec,
            binding.actvSmoke to binding.tilSmoke,
            binding.actvScc to binding.tilScc,
            binding.actvCalc to binding.tilCalc,
            binding.actvMtrans to binding.tilMtrans,
            binding.actvNcp to binding.tilNcp,
            binding.actvTue to binding.tilTue
        )
        dropdowns.forEach { (actv, til) ->
            if (actv.text.toString().trim().isEmpty()) {
                til.error = "Please select an option"
                valid = false
            } else {
                til.error = null
            }
        }
        return valid
    }

    private fun buildRequest(): RecommendationRequest {
        val heightCm = binding.etHeight.text.toString().toFloat()
        val activityValue = binding.sliderPhysicalActivity.value
        val waterValue = binding.sliderWaterIntake.value
        return RecommendationRequest(
            age = binding.etAge.text.toString().toInt(),
            height = heightCm / 100f,
            weight = binding.etWeight.text.toString().toFloat(),
            gender = binding.actvGender.text.toString(),
            family_history_with_overweight = binding.actvFamilyHistory.text.toString(),
            favc = binding.actvFavc.text.toString(),
            caec = binding.actvCaec.text.toString(),
            smoke = binding.actvSmoke.text.toString(),
            scc = binding.actvScc.text.toString(),
            calc = binding.actvCalc.text.toString(),
            mtrans = binding.actvMtrans.text.toString(),
            physical_activity = activityValue,
            water_intake = waterValue,
            ncp = binding.actvNcp.text.toString().toFloat(),
            tue = binding.actvTue.text.toString().toFloat(),
            faf = activityValue,
            ch2o = waterValue
        )
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.recommendationState.collect { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        binding.progressIndicator.show()
                        binding.btnGetRecommendation.isEnabled = false
                    }
                    is NetworkResult.Success -> {
                        binding.progressIndicator.hide()
                        binding.btnGetRecommendation.isEnabled = true
                        val parcelable = result.data.toParcelable()
                        viewModel.resetState()
                        val bundle = Bundle().apply {
                            putParcelable("recommendation", parcelable)
                        }
                        findNavController().navigate(R.id.action_profile_to_dashboard, bundle)
                    }
                    is NetworkResult.Error -> {
                        binding.progressIndicator.hide()
                        binding.btnGetRecommendation.isEnabled = true
                        binding.root.showErrorSnackbar(result.message)
                    }
                    null -> {
                        binding.progressIndicator.hide()
                        binding.btnGetRecommendation.isEnabled = true
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
