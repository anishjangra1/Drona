package com.godspeed.drona

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.godspeed.drona.models.Login
import com.godspeed.drona.utils.SharedPrefManager
import com.godspeed.drona.utils.SharedPrefManager.*
import com.godspeed.drona.utils.Utility
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : AppCompatActivity() {
    lateinit var etphone: EditText
    lateinit var etPassword: EditText
    lateinit var tvSignUp: TextView
    lateinit var tvReset: TextView
    lateinit var btnLogin: Button
    lateinit var progressBar: ProgressBar
    lateinit var checkBox: CheckBox
    var imei = "";
    private val PHONE_STATE_CODE = 1
    var gotImei = false
    private lateinit var apiInterface: APIInterface

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("StringFormatInvalid")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        SharedPrefManager.init(getApplicationContext());
        progressBar =  findViewById(R.id.progressBar)

        apiInterface = APIClient.getClient().create(APIInterface::class.java)
        checkImei();

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            SharedPrefManager.write(SharedPrefManager.TOKEN, token);

            // Log and toast
            val msg = getString(R.string.msg_token_fmt, token)
            Log.d(TAG, msg)
//            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
        })

        btnLogin = findViewById(R.id.btnLogin)
        etphone = findViewById(R.id.etphone)
        etPassword = findViewById(R.id.etPassword)
        tvSignUp = findViewById(R.id.tvSignUp)
        tvReset = findViewById(R.id.tvReset)
        checkBox = findViewById(R.id.saveCredentials)

        var isLogined=SharedPrefManager.read(IS_LOGIN,false)

        if(isLogined==true){
            etphone.setText(SharedPrefManager.read(LOCAL_USER_ID,""))
            etPassword.setText(SharedPrefManager.read(LOCAL_USER_PASS,""))
            checkBox.isChecked=true
        }

        btnLogin.setOnClickListener(View.OnClickListener {
            if(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    !Utility.isOnline(this)
                } else {
                    TODO("VERSION.SDK_INT < LOLLIPOP")
                }
            )
            {
                Toast.makeText(
                    this, "Please check internet connection!",
                    Toast.LENGTH_LONG
                ).show();
            }
           else if (gotImei == false) {
                checkImei()
                Utility.getUniqueIMEIId(this)
                Toast.makeText(
                    this, "Please grant permission!",
                    Toast.LENGTH_LONG
                ).show();
            } else if (etphone.text.trim().length < 10) {
                Toast.makeText(
                    this, "Please enter valid phone number!",
                    Toast.LENGTH_LONG
                ).show();
            } else if (etPassword.text.trim().length < 6) {
                Toast.makeText(
                    this, "Please enter valid password!",
                    Toast.LENGTH_LONG
                ).show();
            } else {
                progressBar.visibility = View.VISIBLE
                val data: MutableMap<String, String> = HashMap()
//                data["user_id"] = "7834828645"
//                data["user_pass"] = "7834828645"

                data["user_id"] = etphone.text.trim().toString()
                data["user_pass"] = etPassword.text.trim().toString()

                val call1: Call<Login> = apiInterface.getMediaList(data)
                call1.enqueue(object : Callback<Login> {
                    override fun onResponse(call: Call<Login>, response: Response<Login>) {
                        progressBar.visibility = View.GONE
                        if (response.body() != null) {

                            val user1: Login = response.body()

                            SharedPrefManager.write(SharedPrefManager.LOCAL_USER_ID, etphone.text.trim().toString());
                            SharedPrefManager.write(SharedPrefManager.LOCAL_USER_PASS, etPassword.text.trim().toString());
                            SharedPrefManager.write(SharedPrefManager.USER_ID, user1.user_id);
                            SharedPrefManager.write(SharedPrefManager.USER_PASS, user1.user_pass);
                            SharedPrefManager.write(SharedPrefManager.WEB_TOKEN, user1.token);
                            SharedPrefManager.write(SharedPrefManager.WEB_TOKEN_ID, user1.token_id);

                            if(checkBox.isChecked){
                                SharedPrefManager.write(SharedPrefManager.IS_LOGIN, true);
                            }else{
                                SharedPrefManager.write(SharedPrefManager.IS_LOGIN, false);
                            }


                            sendRegistrationToServer(user1.token.toString())

                            nextScreen(SharedPrefManager.LOGIN_SCREEN)


                        } else {
                            Toast.makeText(
                                applicationContext,
                                "Your userid or password is incorrect!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<Login>, t: Throwable) {
                        call.cancel()
                        progressBar.visibility = View.GONE
                    }
                })

            }
        })

        tvSignUp.setOnClickListener(View.OnClickListener {
            nextScreen(SharedPrefManager.SIGN_UP_SCREEN)
        })
        tvReset.setOnClickListener(View.OnClickListener {
            nextScreen(SharedPrefManager.FORGOT_SCREEN)
        })
    }

    private fun sendRegistrationToServer(token: String) {
        val cbIntent = Intent()
        cbIntent.setClass(this, SendTokenService::class.java)
        cbIntent.putExtra("token", token)
        startService(cbIntent)
    }

    private fun checkImei() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_PHONE_STATE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getImei()
//            Toast.makeText(this, "you have already granted", Toast.LENGTH_SHORT).show()
        } else {
            reqPhonePermission()
        }


    }

    @JvmName("getImei1")
    private fun getImei(): String? {

        val myversion = Integer.valueOf(Build.VERSION.SDK)
        if (myversion < 23) {
            val manager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
            val info = manager.connectionInfo
            imei = info.macAddress
            if (imei == null) {
                val mngr = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_PHONE_STATE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return ""
                }
                imei = mngr.deviceId
            }
        } else if (myversion > 23 && myversion < 29) {
            val mngr = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_PHONE_STATE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return ""
            }
            imei = mngr.deviceId
        } else {
            val androidId: String =
                Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
            imei = androidId
        }
        if (imei!!.isNotEmpty()) {
            gotImei = true
        }
        SharedPrefManager.write(SharedPrefManager.IMEI, imei);

        return imei
    }

    private fun reqPhonePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.READ_PHONE_STATE
            )
        ) {
            AlertDialog.Builder(this).setTitle("permission needed")
                .setMessage("this permission needed")
                .setPositiveButton("ok",
                    DialogInterface.OnClickListener { dialogInterface, i ->
                        ActivityCompat.requestPermissions(
                            this, arrayOf(
                                Manifest.permission.READ_PHONE_STATE
                            ), PHONE_STATE_CODE
                        )
                    }).setNegativeButton("cancel",
                    DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                .create().show()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_PHONE_STATE),
                PHONE_STATE_CODE
            )
        }
    }

    fun onRequestPermissionResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        if (requestCode == PHONE_STATE_CODE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
                getImei()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun nextScreen(screenName :String){
        val i = Intent(applicationContext, HomeActivity::class.java)
        i.putExtra(SCREEN_NAME,screenName)
        startActivity(i)
        if(screenName.contains(SharedPrefManager.LOGIN_SCREEN)){
            finish()
        }

    }

}



