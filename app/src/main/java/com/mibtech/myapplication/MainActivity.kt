package com.mibtech.myapplication

import android.app.Dialog

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.gson.Gson
import com.mibtech.myapplication.databinding.ActivityMainBinding

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        CallAPILoginAsyncTask("Mithu", "12345").onExecuteCalled()

    }

    private inner class CallAPILoginAsyncTask(var username: String, var password: String){

        private lateinit var customProgressDialog: Dialog

        fun onExecuteCalled(){
            showProgressDialog()

            CoroutineScope(Dispatchers.IO).launch {
                //Do in background
                kotlin.runCatching {
                    var result: String
                    var connection: HttpURLConnection? = null
                    try {
                        val url = URL("https://run.mocky.io/v3/4353a3fc-fdf2-45c3-9c16-4dada972a28f")
                        connection = url.openConnection() as HttpURLConnection
                        connection.doInput = true
                        connection.doOutput = true

                        connection.instanceFollowRedirects = false

                        connection.requestMethod = "POST"
                        connection.setRequestProperty("Content-Type", "application/json")
                        connection.setRequestProperty("charset", "utf-8")
                        connection.setRequestProperty("Accept", "application/json")

                        connection.useCaches = false

                        //Write data on connection
                        val writeDataOutputStream = DataOutputStream(connection.outputStream)
                        val jsonRequest = JSONObject()
                        jsonRequest.put("username", username)
                        jsonRequest.put("password", password)

                        writeDataOutputStream.writeBytes(jsonRequest.toString())
                        writeDataOutputStream.flush()
                        writeDataOutputStream.close()

                        val httpResult: Int = connection.responseCode

                        if (httpResult == HttpURLConnection.HTTP_OK){
                            val inputStream = connection.inputStream
                            val reader = BufferedReader(InputStreamReader(inputStream))

                            val stringBuilder = StringBuilder()
                            var line: String?

                            try {
                                while (reader.readLine().also { line = it } != null){
                                    stringBuilder.append(line + "\n")
                                }
                            } catch (e: IOException){
                                e.printStackTrace()
                            } finally {
                                try {
                                    inputStream.close()
                                } catch (e: IOException){
                                    e.printStackTrace()
                                }
                            }
                            result = stringBuilder.toString()
                        } else {
                            result = connection.responseMessage
                        }
                    } catch (e: SocketTimeoutException){
                        result = "Connection Timeout"
                    } catch (e: Exception){
                        result = "Error: " + e.message
                    } finally {
                        connection?.disconnect()
                    }

                    withContext(Dispatchers.Main){}
                    cancelProgressDialog()

                    Log.i("JSON RESULT: ", result)

                    //Gson implementation useing fromJson with the result json string and map it through the Response data data class
                    val responseData = Gson().fromJson(result, ResponseData::class.java)
                    //Access the json data
                    Log.i("Message: ", responseData.message)
                    Log.i("Name: ", responseData.name)
                    Log.i("Email: ", responseData.email)
                    Log.i("Mobile-No: ", "${responseData.mobile}")

                    Log.i("Profile Detail: ", "${responseData.profile_details.is_profile_completed}")
                    Log.i("Rating: ", "${responseData.profile_details.rating}")

                    //Access data from a list
                    for (item in responseData.data_list.indices){
                        Log.i("Value $item", "${responseData.data_list[item]}")

                        Log.i("ID: ", "${responseData.data_list[item].id}")
                        Log.i("VALUE: ", "${responseData.data_list[item].value}")
                    }


                }
            }


        }

        private fun showProgressDialog() {
            customProgressDialog = Dialog(this@MainActivity)
            customProgressDialog.setContentView(R.layout.dialog_custom_progress)
            customProgressDialog.show()
        }

        private fun cancelProgressDialog() {
            customProgressDialog.dismiss()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}