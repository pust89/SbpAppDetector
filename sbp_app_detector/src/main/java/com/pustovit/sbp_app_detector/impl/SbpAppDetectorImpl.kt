package com.pustovit.sbp_app_detector.impl

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Build
import com.pustovit.sbp_app_detector.SbpAppDetector
import com.pustovit.sbp_app_detector.model.SbpBank
import com.pustovit.sbp_app_detector.network.SbpBankDto
import com.pustovit.sbp_app_detector.network.SbpConnection
import java.text.Collator
import java.util.*
import kotlin.Comparator

internal class SbpAppDetectorImpl(private val listener: SbpAppDetector.Listener) : SbpAppDetector {


    private val sbpConnection = SbpConnection()

    private val onSuccessLoading: ((Context, List<SbpBankDto>) -> Unit) =
        { context, sbpBanksDto ->
            sbpBanksDto.filter { it.schema.isNotEmpty() }.let {
                val installedBanks = getInstalledBanks(context, it)
                listener.onLoading(false)
                listener.onSuccess(installedBanks)
            }
        }

    private val onFailureLoading: ((Throwable) -> Unit) = {
        listener.onFailure(it)
    }

    private val onLoading: ((Boolean) -> Unit) = {
        listener.onLoading(it)
    }

    override suspend fun execute(
        context: Context,
        connectTimeout: Int,
        readTimeout: Int
    ) {
        sbpConnection.getSbpBanks(
            context = context,
            onSuccessLoading = onSuccessLoading,
            onLoading = onLoading,
            onFailureLoading = onFailureLoading,
            connectTimeout = connectTimeout,
            readTimeout = readTimeout,
        )
    }


    private fun getInstalledBanks(
        context: Context,
        sbpBanksDto: List<SbpBankDto>
    ): List<SbpBank> {

        val packageManager = context.packageManager
        val russianComparator =
            Comparator<SbpBank> { o1, o2 ->
                Collator.getInstance(Locale("ru", "RU")).compare(o1.appName, o2.appName)
            }

        return sbpBanksDto.mapNotNull { sbpBank ->
            getActivityResolveInfoCompat(sbpBank.intentForCheck, packageManager).firstOrNull()
                ?.let { resolveInfo ->
                    val appName =
                        packageManager.getApplicationLabel(resolveInfo.activityInfo.applicationInfo)
                            .toString()
                    val packageName = resolveInfo.activityInfo.packageName
                    val activityIconDrawable = resolveInfo.loadIcon(packageManager)
                    SbpBank(
                        appName = appName,
                        packageName = packageName,
                        requiredSchema = sbpBank.schema,
                        activityIconDrawable = activityIconDrawable,
                        bankLogoUrl = sbpBank.logoURL,
                        activityResolveInfo = resolveInfo
                    )
                }
        }.sortedWith(russianComparator)
    }


    /**
     * Retrieve all activities that can be performed for the given intent.
     */
    private fun getActivityResolveInfoCompat(
        intent: Intent,
        packageManager: PackageManager
    ): List<ResolveInfo> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.queryIntentActivities(
                intent,
                PackageManager.ResolveInfoFlags.of(PackageManager.GET_ACTIVITIES.toLong())
            )
        } else {
            @Suppress("DEPRECATION")
            packageManager.queryIntentActivities(intent, PackageManager.GET_ACTIVITIES)
        }
    }
}