package ir.turbo.turboremotecontrol

import android.content.Context
import android.content.Intent
import androidx.multidex.MultiDex
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.viewpump.ViewPump
import ir.turbo.turboremotecontrol.di.DaggerAppComponent
import ir.turbo.turboremotecontrol.ui.main.MainActivity
import ir.turbo.turboremotecontrol.ui.startup.StartupActivity
import ir.turbo.turboremotecontrol.util.SharedPrefs
import javax.inject.Inject


class RemoteApplication : DaggerApplication() {

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }


    override fun onCreate() {
        super.onCreate()
        changeAppFont()

        val page = sharedPrefs.loadLauncherPage()

        if (page.equals("main")) {
            System.out.println("9Logger: main")
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        } else {
            System.out.println("9Logger: startup")
            val intent = Intent(this, StartupActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder().create(this)
    }

    private fun changeAppFont() {

        ViewPump.init(
            ViewPump.builder()
                .addInterceptor(
                    CalligraphyInterceptor(
                        CalligraphyConfig.Builder()
                            .setDefaultFontPath("fonts/yekan_mobile_bold.ttf")
                         //   .setFontAttrId(R.attr.fontPath)
                            .build()
                    )
                )
                .build()
        )


//        CalligraphyConfig.initDefault(
//            CalligraphyConfig.Builder()
//                .setDefaultFontPath("fonts/yekan_mobile_bold.ttf")
//                .setFontAttrId(R.attr.fontPath)
//                .build()
//        )
    }
}