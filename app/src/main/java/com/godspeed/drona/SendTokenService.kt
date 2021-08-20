package com.godspeed.drona

import android.app.IntentService
import android.content.Intent
import android.content.SharedPreferences
import com.godspeed.drona.models.Login
import com.godspeed.drona.utils.SharedPrefManager
import com.godspeed.drona.utils.SharedPrefManager.read
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SendTokenService(name :String ="SentTokenIntentService") : IntentService(name) {
    private lateinit var apiInterface: APIInterface
    override fun onHandleIntent(intent: Intent?) {

        val token = intent!!.getStringExtra("token")
        if (token != null) {
            sendToServer(token)
        }
    }

    private fun sendToServer(token: String) {
        apiInterface = APIClient.getClient().create(APIInterface::class.java)
        val data: MutableMap<String, String> = HashMap()
        val imei: String = SharedPrefManager.read(SharedPrefManager.IMEI, null)
        val token: String = SharedPrefManager.read(SharedPrefManager.TOKEN, "")
        val user_id: String = SharedPrefManager.read(SharedPrefManager.USER_ID, "")

        data["imei"] = imei
        data["token"] = token
        data["user_id"] = user_id

        val call1: Call<Login> = apiInterface.saveFirebaseToken(data)
        call1.enqueue(object : Callback<Login> {
            override fun onResponse(call: Call<Login>, response: Response<Login>) {
                val user1: Login = response.body()
//                        Toast.makeText(
//                            applicationContext,
//                            user1.token.toString() ,
//                            Toast.LENGTH_SHORT
//                        ).show()
            }

            override fun onFailure(call: Call<Login>, t: Throwable) {
                call.cancel()
            }
        })
    }

}