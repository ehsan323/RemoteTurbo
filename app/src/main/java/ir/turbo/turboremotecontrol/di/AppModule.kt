package ir.turbo.turboremotecontrol.di

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import ir.turbo.turboremotecontrol.RemoteApplication
import ir.turbo.turboremotecontrol.util.SharedPrefs
import javax.inject.Singleton

@Module(includes = arrayOf(ViewModelModule::class))
class AppModule() {

    companion object{
        const val SHARED_PREF_FILE = "shared_pref_file"
    }

    @Provides
    @Singleton
    fun provideContext(application: RemoteApplication): Context {
        return application.applicationContext
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(SHARED_PREF_FILE, Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideSharedPrefs(sharedPreferences: SharedPreferences): SharedPrefs {
        return SharedPrefs(sharedPreferences)
    }
}
