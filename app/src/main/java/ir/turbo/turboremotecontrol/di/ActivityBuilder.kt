package ir.turbo.turboremotecontrol.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ir.turbo.turboremotecontrol.ui.admin.AdminActivity
import ir.turbo.turboremotecontrol.ui.launcher.LauncherActivity
import ir.turbo.turboremotecontrol.ui.login.LoginActivity
import ir.turbo.turboremotecontrol.ui.main.MainActivity
import ir.turbo.turboremotecontrol.ui.password.PasswordActivity
import ir.turbo.turboremotecontrol.ui.startup.StartupActivity

@Module
 abstract class ActivityBuilder {

    @ContributesAndroidInjector (modules = [FragmentBuilder::class])
     abstract fun bindMainActivity(): MainActivity

    @ContributesAndroidInjector
    abstract fun bindAdminActivity(): AdminActivity

    @ContributesAndroidInjector
    abstract fun bindStartupActivity(): StartupActivity

    @ContributesAndroidInjector
    abstract fun bindPasswordActivity(): PasswordActivity

    @ContributesAndroidInjector
    abstract fun bindLoginActivity(): LoginActivity

   @ContributesAndroidInjector
   abstract fun bindLauncherActivity(): LauncherActivity
}
