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
import com.pustovit.sbp_app_detector.model.SbpBank
import com.pustovit.sbp_app_detector.model.SbpBankDto
import com.pustovit.sbp_app_detector_sample_app.databinding.FragmentSbpBanksBinding
import kotlinx.coroutines.launch

class SbpBanksFragment : Fragment() {

    private var binding: FragmentSbpBanksBinding? = null

    private val sbpBankAdapter by lazy {
        SbpBankAdapter { sbpBank ->
            sbpBank.startSbpActivity(
                "${sbpBank.requiredSchema}://qr.nspk.ru/test",
                requireContext()
            )
        }
    }

    // Create an SbpAppDetector using the create method
    private val sbpAppDetector by lazy {
        SbpAppDetector.create(
            contextProvider = {
                requireContext()
            },
            listener = object : SbpAppDetector.Listener {

                override fun onSuccess(installedSbpBanks: List<SbpBank>) {
                    sbpBankAdapter.submitList(installedSbpBanks)
                }

                override fun onLoading(isLoading: Boolean) {
                    binding?.progressBar?.isVisible = isLoading
                }

                override fun onFailure(throwable: Throwable) {
                    Toast.makeText(requireContext(), throwable.message, Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun findSbpBanks() {
        // You can call the 'execute' method that
        // makes a request to https://qr.nspk.ru/proxyapp/c2bmembers.json  and finds all installed
        // banks on this device.
        lifecycleScope.launch {
            sbpAppDetector.execute()
        }

        // Or you can call the 'execute' method with your own implementation of RemoteDataSource.

        /*
        lifecycleScope.launch {
                sbpAppDetector.execute(
                    remoteDataSource = object : SbpAppDetector.RemoteDataSource {
                        override suspend fun getSbpBanks(): List<SbpBankDto> {
                            val requestedBanks = listOf<SbpBankDto>(
                                SbpBankDto(
                                    bankName = "Сбербанк",
                                    logoURL = "https://qr.nspk.ru/proxyapp/logo/bank100000000111.png",
                                    schema =  "bank100000000111",
                                    package_name = "ru.sberbankmobile"
                                ),
                                SbpBankDto(
                                    bankName = "Тинькофф Банк",
                                    logoURL = "https://qr.nspk.ru/proxyapp/logo/bank100000000004.png",
                                    schema =  "bank100000000004",
                                    package_name = "com.idamob.tinkoff.android"
                                )
                            )
                            return requestedBanks
                        }
                    }
                )

            }

         */
    }

    private fun initViews(binding: FragmentSbpBanksBinding) {
        binding.installedSbpBanksRecyclerView.adapter = sbpBankAdapter
        findSbpBanks()
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
