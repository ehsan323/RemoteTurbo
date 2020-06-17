package ir.turbo.turboremotecontrol.util

import java.io.Serializable

data class TotalCarCondition(
    var switchStatus: String? = null,
    var thiefCatcher: String? = null,
    var batteryStatus: String? = null,
    var signalstatus: String? = null,
    var netStatus: String? = null,
    var carDoor: String? = null,
    var batteryPercent: String? = null
) : Serializable {
}