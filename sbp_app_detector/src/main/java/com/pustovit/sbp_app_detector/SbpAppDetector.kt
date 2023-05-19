package com.pustovit.sbp_app_detector

import android.content.Context
import com.pustovit.sbp_app_detector.impl.SbpAppDetectorImpl
import com.pustovit.sbp_app_detector.model.SbpBank

interface SbpAppDetector {

    /**
     * Makes a request to [https://qr.nspk.ru/proxyapp/c2bmembers.json](https://qr.nspk.ru/proxyapp/c2bmembers.json)
     * and finds installed banks on this device.
     * @param context @[Context]
     * @param connectTimeout an int that specifies the connect timeout value in milliseconds
     * @param readTimeout an int that specifies the timeout value to be used in milliseconds
     */
    suspend fun execute(
        context: Context,
        connectTimeout: Int = 10000,
        readTimeout: Int = 10000
    )


    /**
     * Listens to the state of loading data
     *
     */
    interface Listener {
        /**
         * Called when data is loaded successfully
         *
         * @param installedSbpBanks a list of installed banks that support SBP
         */
        fun onSuccess(installedSbpBanks: List<SbpBank>)

        /**
         * Called when data is loading
         *
         * @param isLoading is loading now
         */
        fun onLoading(isLoading: Boolean)

        /**
         *Called when data is not loaded successfully.
         *
         * @param throwable throwable
         */
        fun onFailure(throwable: Throwable)
    }

    companion object {

        fun create(listener: Listener): SbpAppDetector {
            return SbpAppDetectorImpl(listener)
        }
    }
}