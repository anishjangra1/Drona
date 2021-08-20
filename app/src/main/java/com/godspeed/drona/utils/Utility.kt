package com.godspeed.drona.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.app.ActivityCompat


class Utility {
    companion object {

        fun getUniqueIMEIId(context: Context): String? {
            try {
                val telephonyManager =
                    context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.READ_PHONE_STATE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return ""
                }
                val imei = telephonyManager.deviceId
                Log.e("imei", "=$imei")
                return if (imei != null && !imei.isEmpty()) {
                    imei
                } else {
                    Build.SERIAL
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return "not_found"
        }

    }
}