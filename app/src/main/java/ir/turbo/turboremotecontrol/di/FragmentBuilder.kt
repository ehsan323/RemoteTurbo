package ir.turbo.turboremotecontrol.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ir.turbo.turboremotecontrol.ui.map.MapFragment
import ir.turbo.turboremotecontrol.ui.remote.RemoteFragment
import ir.turbo.turboremotecontrol.ui.setting.SettingFragment

@Module
abstract class FragmentBuilder {

    @ContributesAndroidInjector
    internal abstract fun contributeRemoteFragment(): RemoteFragment

    @ContributesAndroidInjector
    internal abstract fun contributeMapFragment(): MapFragment

    @ContributesAndroidInjector
    internal abstract fun contributeSettingFragment(): SettingFragment
}
