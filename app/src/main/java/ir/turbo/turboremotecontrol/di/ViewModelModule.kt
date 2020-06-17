package ir.turbo.turboremotecontrol.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ir.turbo.turboremotecontrol.helper.ViewModelFactory
import ir.turbo.turboremotecontrol.ui.admin.AdminViewModel
import ir.turbo.turboremotecontrol.ui.login.LoginViewModel
import ir.turbo.turboremotecontrol.ui.main.MainViewModel
import ir.turbo.turboremotecontrol.ui.map.MapViewModel
import ir.turbo.turboremotecontrol.ui.password.PasswordViewModel
import ir.turbo.turboremotecontrol.ui.remote.RemoteViewModel
import ir.turbo.turboremotecontrol.ui.setting.SettingViewModel
import ir.turbo.turboremotecontrol.ui.startup.StartupViewModel

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    internal abstract fun bindsMainViewModel(mainViewModel: MainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MapViewModel::class)
    internal abstract fun bindMapViewModel(mainViewModel: MapViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(RemoteViewModel::class)
    internal abstract fun bindRemoteViewModel(remoteViewModel: RemoteViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AdminViewModel::class)
    internal abstract fun bindsAdminViewModel(adminViewModel: AdminViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    internal abstract fun bindsLoginViewModel(loginViewModel: LoginViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PasswordViewModel::class)
    internal abstract fun bindsPasswordViewModel(passwordViewModel: PasswordViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(StartupViewModel::class)
    internal abstract fun bindsStartupViewModel(startupViewModel: StartupViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SettingViewModel::class)
    internal abstract fun bindsFavoritesViewModel(settingViewModel: SettingViewModel): ViewModel

    @Binds
     abstract fun bindsViewModelFactory(viewModelFactory: ViewModelFactory): ViewModelProvider.Factory
}


