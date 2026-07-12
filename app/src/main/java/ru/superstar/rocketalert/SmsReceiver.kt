package ru.superstar.rocketalert

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log

class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return

        try {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            if (messages.isNullOrEmpty()) return

            val messagesBySender = messages.groupBy { it.displayOriginatingAddress }

            for ((senderAddress, smsMessages) in messagesBySender) {
                if (senderAddress == null) continue

                val fullText = smsMessages.joinToString(separator = "") { it.displayMessageBody ?: "" }

                if (senderAddress.equals(SENDER, ignoreCase = true)) {
                    var matchedKeyword: String? = null
                    for (keyword in KEYWORDS) {
                        if (fullText.contains(keyword, ignoreCase = true)) {
                            matchedKeyword = keyword
                            break
                        }
                    }

                    if (matchedKeyword != null) {
                        Log.d(TAG, "Danger detected: $matchedKeyword")
                        RocketSoundPlayer.playSound(context)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error handling SMS: ${e.message}", e)
        }
    }

    companion object {
        private const val TAG = "SmsReceiver"
        const val SENDER = "RSCHS"
        val KEYWORDS = listOf(
            "РАКЕТНАЯ ОПАСНОСТЬ",
            "БЕСПИЛОТНАЯ ОПАСНОСТЬ"
        )
    }
}
