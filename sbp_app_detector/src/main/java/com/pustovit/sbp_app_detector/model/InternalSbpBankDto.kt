package com.pustovit.sbp_app_detector.model

import android.content.Intent
import android.net.Uri

internal data class InternalSbpBankDto(
    val bankName: String,
    val logoURL: String,
    val schema: String,
    val package_name: String
) {

    val intentForCheck: Intent
        get() = Intent(Intent.ACTION_VIEW).also {
            it.setDataAndNormalize(Uri.parse(schema + testUrl))
        }

    companion object {
        private const val testUrl = "://qr.nspk.ru/test"
    }
}