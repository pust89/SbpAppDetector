package com.pustovit.sbp_app_detector.network

import com.pustovit.sbp_app_detector.model.SbpBankDto
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.stream.Collectors


internal class SbpConnection(
    private val connectTimeout: Int,
    private val readTimeout: Int
) {

    internal fun getSbpBanks(): List<SbpBankDto> {
        var connection: HttpURLConnection? = null

        return try {
            connection = URL(membersUrl).openConnection() as HttpURLConnection

            connection.connectTimeout = connectTimeout
            connection.readTimeout = readTimeout

            connection.requestMethod = "GET"
            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val bufferedReader =
                    BufferedReader(InputStreamReader(connection.inputStream))

                bufferedReader.use {
                    val jsonResponse = JSONObject(it.lines().collect(Collectors.joining()))
                    extractFromJSONResponse(jsonResponse)
                }

            } else {
                throw RuntimeException("ResponseCode ${connection.responseCode}\n${connection.responseMessage}")
            }
        } catch (e: Exception) {
            throw e
        } finally {
            connection?.disconnect()
        }
    }

    private fun extractFromJSONResponse(response: JSONObject): List<SbpBankDto> {
        return try {
            val jsonArray = response.getJSONArray("dictionary")
            val result = mutableListOf<SbpBankDto>()
            for (i in 0 until jsonArray.length()) {
                val jsonObj = jsonArray.getJSONObject(i)
                val data = SbpBankDto(
                    bankName = jsonObj.extractStringSafety("bankName"),
                    logoURL = jsonObj.extractStringSafety("logoURL"),
                    schema = jsonObj.extractStringSafety("schema"),
                    package_name = jsonObj.extractStringSafety("package_name"),
                )
                result.add(data)
            }
            result
        } catch (e: JSONException) {
            e.printStackTrace()
            throw e
        }
    }

    private fun JSONObject.extractStringSafety(key: String): String? {
        return try {
            getString(key)
        } catch (e: Exception) {
            return null
        }
    }

    companion object {
        private const val membersUrl = "https://qr.nspk.ru/proxyapp/c2bmembers.json"
    }
}