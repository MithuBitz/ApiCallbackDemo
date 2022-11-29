package com.mibtech.myapplication

import android.app.Dialog
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        callAPILoginAsyncTask("Mithu", "12345").onExecuteCalled()

    }

    private inner class callAPILoginAsyncTask(val username: String, val password: String){

        private lateinit var customProgressDialog: Dialog

        fun onExecuteCalled(){
            showProgressDialog()

            CoroutineScope(Dispatchers.IO).launch {
                //Do in background
                kotlin.runCatching {
                    var result: String
                    var connection: HttpURLConnection? = null
                    try {
                        val url = URL("https://run.mocky.io/v3/6792b05f-c0a8-4f30-b472-0a974beb340d")
                        connection = url.openConnection() as HttpURLConnection
                        connection.doInput = true
                        connection.doOutput = true

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

                    Log.e("JSON RESULT: ", result)

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
}