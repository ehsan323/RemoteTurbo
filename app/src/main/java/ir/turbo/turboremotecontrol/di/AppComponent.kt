package ir.turbo.turboremotecontrol.di

import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import ir.turbo.turboremotecontrol.RemoteApplication

import javax.inject.Singleton


//   https://github.com/iammert/AndroidArchitecture/tree/master/app/src/main/java/iammert/com/androidarchitecture/di

@Singleton
@Component(modules = arrayOf(AndroidSupportInjectionModule::class, AppModule::class, ActivityBuilder::class))
interface AppComponent : AndroidInjector<RemoteApplication> {

    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<RemoteApplication>()
}
