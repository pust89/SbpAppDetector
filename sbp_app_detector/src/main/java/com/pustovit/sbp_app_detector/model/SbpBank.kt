package com.pustovit.sbp_app_detector.model

import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable
import android.net.Uri

/**
 * Representing an installed bank.
 * @property appName Bank application name
 * @property packageName Bank package name
 * @property requiredSchema Required schema for this bank
 * @property activityIconDrawable Icon fetched from SbpActivity of this bank
 * @property bankLogoUrl  Icon url from [https://qr.nspk.ru/proxyapp/c2bmembers.json](https://qr.nspk.ru/proxyapp/c2bmembers.json)
 * @property activityResolveInfo SbpActivity @[ResolveInfo] of this bank
 */
class SbpBank(
    val appName: String,
    val packageName: String,
    val requiredSchema: String,
    val activityIconDrawable: Drawable?,
    val bankLogoUrl: String,
    val activityResolveInfo: ResolveInfo
) {
    /**
     * Start this bank's SbpActivity.
     * Throw IllegalArgumentException if param uri schema is not
     * [requiredSchema] from [SbpBank]
     * @throws IllegalArgumentException
     * @param uri uri
     * @param context @[Context]
     */
    @Throws(IllegalArgumentException::class)
    fun startSbpActivity(uri: String, context: Context) {
        if (!uri.startsWith(requiredSchema)) {
            throw IllegalArgumentException("Uri must start with schema $requiredSchema!")
        }
        startActivity(Uri.parse(uri), context)
    }

    /**
     * Start this bank's SbpActivity.
     * Throw IllegalArgumentException if param uri schema is not
     * [requiredSchema] from [SbpBank]
     * @throws IllegalArgumentException
     * @param uri uri
     * @param context @[Context]
     */
    @Throws(IllegalArgumentException::class)
    fun startSbpActivity(uri: Uri, context: Context) {
        if (uri.scheme != requiredSchema) {
            throw IllegalArgumentException("Uri must start with schema $requiredSchema!")
        }
        startActivity(uri, context)
    }

    private fun startActivity(uri: Uri, context: Context) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setPackage(packageName)
        intent.setDataAndNormalize(uri)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

}