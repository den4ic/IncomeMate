package com.genesiseternity.incomemate.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.telephony.SmsMessage
import android.widget.Toast

class ReceiverSms : BroadcastReceiver()
{
    private val ACTION: String = "android.provider.Telephony.SMS_RECEIVED"
    private val PDU: String = "pdus"

    override fun onReceive(context: Context?, intent: Intent?)
    {
        if (intent?.action.equals(ACTION))
        {
            val bundle: Bundle = intent?.extras!!
            val smsMessage: Array<SmsMessage?>
            var fromMessage: String

            try {
                val pdus: Array<*>? = bundle.get(PDU) as Array<*>?
                smsMessage = arrayOfNulls(pdus!!.size)
                for (i in smsMessage.indices)
                {
                    smsMessage[i] = SmsMessage.createFromPdu(pdus[i] as ByteArray?)
                    fromMessage = smsMessage[i]?.originatingAddress.toString()
                    val messageBody = smsMessage[i]?.messageBody

                    Toast.makeText(context, "From: " + fromMessage + " Body: " + messageBody, Toast.LENGTH_SHORT).show()
                }
            }
            catch (e: Exception)
            {
                e.printStackTrace()
            }
        }
    }
}