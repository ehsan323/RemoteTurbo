package ir.turbo.turboremotecontrol.ui.main

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Telephony
import android.telephony.SmsMessage
import androidx.core.app.NotificationCompat
import ir.turbo.turboremotecontrol.R
import ir.turbo.turboremotecontrol.util.RemoteUtils
import ir.turbo.turboremotecontrol.util.SharedPrefs
import javax.inject.Inject


class SmsReceiver : BroadcastReceiver() {

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    override fun onReceive(context: Context?, intent: Intent?) {

        val bundle = intent?.getExtras()
        if (bundle != null && bundle.containsKey("pdus")) {
            val smsMessage: SmsMessage

            if (Build.VERSION.SDK_INT >= 19) { //KITKAT
                val msgs = Telephony.Sms.Intents.getMessagesFromIntent(intent)
                smsMessage = msgs[0]
            } else {
                val pdus = bundle.get("pdus") as Array<*>
                smsMessage = SmsMessage.createFromPdu(pdus[0] as ByteArray)
            }

            //  val message = smsMessage.messageBody

            if (Build.VERSION.SDK_INT >= 29) {

                //Initialise ContentIntent
                val mainActivity = Intent(context, MainActivity::class.java)
                mainActivity.flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
                mainActivity.putExtra("turbo_sms", smsMessage.displayMessageBody)

                val pi = PendingIntent.getActivity(
                    context,
                    0,
                    mainActivity,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )

                val builder = NotificationCompat.Builder(context!!, "")
                    .setSmallIcon(R.drawable.main_logo)
                    .setContentTitle("هشدار")
                    .setContentText(RemoteUtils.translateRemoteResponse(smsMessage.displayMessageBody))
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pi)
                //    .setFullScreenIntent(pi, true)

                val mgr = context.getSystemService(NotificationManager::class.java)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                    && mgr.getNotificationChannel("") == null
                ) {
                    mgr.createNotificationChannel(
                        NotificationChannel(
                            "",
                            "Whatever",
                            NotificationManager.IMPORTANCE_HIGH
                        )
                    )
                }

                mgr.notify(1, builder.build())
            } else {
                if (smsMessage.displayOriginatingAddress.contains("+989107030881")) {
                    val intentSms = Intent(context, MainActivity::class.java)
                    intent.setClassName(
                        "ir.turbo.turboremotecontrol.ui.main",
                        "ir.turbo.turboremotecontrol.ui.main.MainActivity"
                    )
                    intent.putExtra("turbo_sms", smsMessage.displayMessageBody)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    context?.startActivity(intentSms)
                }
            }
        }
    }
}