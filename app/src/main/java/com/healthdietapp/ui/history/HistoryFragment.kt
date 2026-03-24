package com.healthdietapp.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.healthdietapp.databinding.FragmentHistoryBinding
import com.healthdietapp.utils.NetworkResult
import com.healthdietapp.utils.hide
import com.healthdietapp.utils.show
import com.healthdietapp.viewmodel.HistoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HistoryViewModel by viewModels()
    private lateinit var historyAdapter: HistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeState()
        viewModel.loadHistory()
    }

    private fun setupRecyclerView() {
        historyAdapter = HistoryAdapter()
        binding.rvHistory.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = historyAdapter
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.historyState.collect { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        binding.progressIndicator.show()
                        binding.tvError.hide()
                        binding.tvEmptyHistory.hide()
                        binding.rvHistory.hide()
                    }
                    is NetworkResult.Success -> {
                        binding.progressIndicator.hide()
                        binding.tvError.hide()
                        val items = result.data.items
                        binding.tvTotalAnalyses.text = "${result.data.total} analyses"
                        if (items.isEmpty()) {
                            binding.tvEmptyHistory.show()
                            binding.rvHistory.hide()
                        } else {
                            binding.tvEmptyHistory.hide()
                            binding.rvHistory.show()
                            historyAdapter.submitList(items)
                        }
                    }
                    is NetworkResult.Error -> {
                        binding.progressIndicator.hide()
                        binding.rvHistory.hide()
                        binding.tvEmptyHistory.hide()
                        binding.tvError.text = result.message
                        binding.tvError.show()
                    }
                    null -> {
                        binding.progressIndicator.hide()
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
