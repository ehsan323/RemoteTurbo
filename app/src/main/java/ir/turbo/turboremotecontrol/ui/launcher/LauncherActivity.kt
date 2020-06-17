package ir.turbo.turboremotecontrol.ui.launcher

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ir.turbo.turboremotecontrol.R
import ir.turbo.turboremotecontrol.di.AppModule
import ir.turbo.turboremotecontrol.ui.main.MainActivity
import ir.turbo.turboremotecontrol.ui.startup.StartupActivity
import ir.turbo.turboremotecontrol.util.SharedPrefs
import javax.inject.Inject

class LauncherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      //  setContentView(R.layout.activity_launcher)

        val page =application.getSharedPreferences(AppModule.SHARED_PREF_FILE, Context.MODE_PRIVATE)?.getString(
            SharedPrefs.LAUNCHER_PAGE, "")

        if (page.equals("main")) {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        } else {
            val intent = Intent(this, StartupActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}
