package com.pustovit.sbp_app_detector.impl

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Build
import com.pustovit.sbp_app_detector.SbpAppDetector
import com.pustovit.sbp_app_detector.model.SbpBank
import com.pustovit.sbp_app_detector.model.InternalSbpBankDto
import com.pustovit.sbp_app_detector.network.SbpConnection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.text.Collator
import java.util.*
import kotlin.Comparator

internal class SbpAppDetectorImpl(
    private val contextProvider: () -> Context?,
    private val listener: SbpAppDetector.Listener
) : SbpAppDetector {

    override suspend fun execute(
        remoteDataSource: SbpAppDetector.RemoteDataSource
    ) {
        try {
            listener.onLoading(true)

            withContext(Dispatchers.IO) {
                remoteDataSource.getSbpBanks().map {
                    InternalSbpBankDto(
                        bankName = it.bankName ?: "",
                        logoURL = it.logoURL ?: "",
                        schema = it.schema ?: "",
                        package_name = it.package_name ?: ""
                    )
                }.filter { it.schema.isNotEmpty() }
            }.let { list ->
                contextProvider()?.let { context ->
                    val installedBanks =
                        withContext(Dispatchers.IO) {
                            getInstalledBanks(context, list)
                        }
                    withContext(Dispatchers.Main) {
                        listener.onLoading(false)
                        listener.onSuccess(installedBanks)
                    }
                } ?: run {
                    withContext(Dispatchers.Main) {
                        listener.onLoading(false)
                        listener.onFailure(RuntimeException("Context is null!"))
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                listener.onLoading(false)
                listener.onFailure(e)
            }
        }
    }

    override suspend fun execute(connectTimeout: Int, readTimeout: Int) {
        val sbpConnection = SbpConnection(
            connectTimeout = connectTimeout,
            readTimeout = readTimeout,
        )
        execute(RemoteDataSourceImpl(sbpConnection))
    }

    private fun getInstalledBanks(
        context: Context,
        internalSbpBanksDto: List<InternalSbpBankDto>
    ): List<SbpBank> {

        val packageManager = context.packageManager
        val russianComparator =
            Comparator<SbpBank> { o1, o2 ->
                Collator.getInstance(Locale("ru", "RU")).compare(o1.appName, o2.appName)
            }

        return internalSbpBanksDto.mapNotNull { sbpBank ->
            getActivityResolveInfoCompat(sbpBank.intentForCheck, packageManager).firstOrNull()
                ?.let { resolveInfo ->
                    val appName =
                        packageManager.getApplicationLabel(resolveInfo.activityInfo.applicationInfo)
                            .toString()
                    val packageName = resolveInfo.activityInfo.packageName
                    val activityIconDrawable = resolveInfo.loadIcon(packageManager)
                    SbpBank(
                        appName = appName,
                        bankName = sbpBank.bankName,
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