package com.pustovit.sbp_app_detector_sample_app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.pustovit.sbp_app_detector.SbpAppDetector
import com.pustovit.sbp_app_detector_sample_app.databinding.FragmentSbpBanksBinding
import kotlinx.coroutines.launch

class SbpBanksFragment : Fragment() {

    private var binding: FragmentSbpBanksBinding? = null

    private val sbpBankAdapter by lazy {
        SbpBankAdapter {
            it.startSbpActivity("${it.requiredSchema}://qr.nspk.ru/test", requireContext())
        }
    }

    private val sbpAppDetectorImplListener = object : SbpAppDetector.Listener {
        override fun onSuccess(installedSbpBanks: List<com.pustovit.sbp_app_detector.model.SbpBank>) {
            sbpBankAdapter.submitList(installedSbpBanks)
        }

        override fun onLoading(isLoading: Boolean) {
            binding?.progressBar?.isVisible = isLoading
        }

        override fun onFailure(throwable: Throwable) {
            Toast.makeText(requireContext(), throwable.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun initViews(binding: FragmentSbpBanksBinding) = with(binding) {
        installedSbpBanksRecyclerView.adapter = sbpBankAdapter
        binding.buttonFind.setOnClickListener {
            val sbpAppDetector = SbpAppDetector.create(sbpAppDetectorImplListener)
            lifecycleScope.launch {
                sbpAppDetector.execute(requireContext())
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentSbpBanksBinding.inflate(inflater, container, false).let {
            binding = it
            initViews(it)
            it.root
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
