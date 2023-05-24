package com.pustovit.sbp_app_detector.model


/**
 * Representing dto model of bank.
 * @property bankName Bank application name
 * @property logoURL  Icon url from [https://qr.nspk.ru/proxyapp/c2bmembers.json](https://qr.nspk.ru/proxyapp/c2bmembers.json)
 * @property schema Required schema for this bank
 * @property package_name Bank package name
 */
data class SbpBankDto(
    val bankName: String? = null,
    val logoURL: String? = null,
    val schema: String? = null,
    val package_name: String? = null
)