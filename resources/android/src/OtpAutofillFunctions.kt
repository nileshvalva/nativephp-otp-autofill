package com.nativephp.plugins.otp_autofill

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.telephony.SmsMessage
import androidx.fragment.app.FragmentActivity
import com.nativephp.mobile.bridge.BridgeFunction
import com.nativephp.mobile.bridge.BridgeResponse
import com.nativephp.mobile.utils.NativeActionCoordinator
import org.json.JSONObject

object OtpAutofillFunctions {

    private var smsReceiver: BroadcastReceiver? = null
    private var isListening = false

    private fun extractOtp(message: String): String? {
        val pattern = Regex("\\b(\\d{4,8})\\b")
        return pattern.find(message)?.groupValues?.get(1)
    }

    class StartListening(private val activity: FragmentActivity) : BridgeFunction {
        override fun execute(parameters: Map<String, Any>): Map<String, Any> {
            if (isListening) {
                return BridgeResponse.success(mapOf("status" to "already_listening"))
            }

            val receiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    if (intent?.action != "android.provider.Telephony.SMS_RECEIVED") return

                    val bundle = intent.extras ?: return
                    val pdus = bundle["pdus"] as? Array<*> ?: return

                    for (pdu in pdus) {
                        val smsMessage = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            val format = bundle.getString("format") ?: "3gpp"
                            SmsMessage.createFromPdu(pdu as ByteArray, format)
                        } else {
                            @Suppress("DEPRECATION")
                            SmsMessage.createFromPdu(pdu as ByteArray)
                        }

                        val body = smsMessage?.messageBody ?: continue
                        val otp = extractOtp(body) ?: continue

                        val payload = JSONObject().apply {
                            put("otp", otp)
                            put("message", body)
                            put("sender", smsMessage.originatingAddress ?: "unknown")
                        }

                        // Must dispatch on main thread
                        Handler(Looper.getMainLooper()).post {
                            NativeActionCoordinator.dispatchEvent(
                                activity,
                                "Nativephp\\OtpAutofill\\Events\\OtpReceived",
                                payload.toString()
                            )
                        }
                    }
                }
            }

            val filter = IntentFilter("android.provider.Telephony.SMS_RECEIVED")
            filter.priority = IntentFilter.SYSTEM_HIGH_PRIORITY
            activity.registerReceiver(receiver, filter)
            smsReceiver = receiver
            isListening = true

            return BridgeResponse.success(mapOf("status" to "listening"))
        }
    }

    class StopListening(private val activity: FragmentActivity) : BridgeFunction {
        override fun execute(parameters: Map<String, Any>): Map<String, Any> {
            smsReceiver?.let {
                activity.unregisterReceiver(it)
                smsReceiver = null
                isListening = false
            }
            return BridgeResponse.success(mapOf("status" to "stopped"))
        }
    }

    class GetStatus(private val context: Context) : BridgeFunction {
        override fun execute(parameters: Map<String, Any>): Map<String, Any> {
            return BridgeResponse.success(mapOf(
                "status" to if (isListening) "listening" else "idle",
                "version" to "1.0.0"
            ))
        }
    }
}