package com.example.mocklocation.presentation.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.mocklocation.core.helper.MyVSMHelper

class IntroActivity : ComponentActivity() {
    private val mListener: MyVSMHelper.InitializeListener = object :
        MyVSMHelper.InitializeListener {
        override fun onProgress(count: Int, total: Int) { }
        override fun onSuccess() { onVSMInitialized() }
        override fun onError(errorCode: Int, errorMsg: String) { }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            IntroScreen(
                onStartClick = {
                    if (MyVSMHelper.getInstance() == null) {
                        MyVSMHelper.createHelper(applicationContext)
                    }
                    MyVSMHelper.getInstance().setListener(mListener)
                    MyVSMHelper.getInstance().initialize()
                }
            )
        }
    }

    private fun onVSMInitialized() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        MyVSMHelper.getInstance().stopLoading()
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        MyVSMHelper.getInstance().setListener(null)
    }
}

@Composable
fun IntroScreen(onStartClick: () -> Unit) {
    Button(onClick = onStartClick) {
        Text(text = "시작")
    }
}