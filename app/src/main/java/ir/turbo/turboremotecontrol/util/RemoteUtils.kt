package ir.turbo.turboremotecontrol.util

object RemoteUtils {

    fun checkPhoneNumberValidation(phone: String?): Boolean {
        return phone?.length == 11 && phone.startsWith("09")
    }

    fun translateRemoteResponse(code: String): String {
        return when (code) {
            "tracker is activated" -> "ردیاب با موفقیت فعال شد"
            "Tracker is activated" -> "ردیاب با موفقیت فعال شد"
            "tracker is deactivated" -> "ردیاب با موفقیت غیر فعال شد"
            "Tracker is deactivated" -> "ردیاب با موفقیت غیر فعال شد"
            "Stop engine Succeed" -> "خودرو با موفقیت خاموش شد"
            "Resume engine Succeed" -> "برق خودرو وصل شد"
            "monitor OK!" -> "شنود با موفقیت فعال شد"
            "monitor ok!" -> "شنود با موفقیت فعال شد"
            "tracker OK!" -> "شنود با موفقیت غیرفعال شد"
            "help OK!" -> "درخواست پیام کمک لغو شد"
            "help ok" -> "درخواست پیام کمک لغو شد"
            "speed ok" -> "کنترل سرعت با موفقیت فعال شد"
            "speed ok!" -> "کنترل سرعت با موفقیت فعال شد"
            "nospeed OK!" -> "کنترل سرعت با موفقیت غیرفعال شد"
            "sensitivity ok" -> "حساسیت با موفقیت تنظیم شد"
            "admin OK!" -> "ادمین ثبت شد"
            "admin ok!" -> "ادمین ثبت شد"
            "noadmin OK!" -> "ادمین حذف شد"
            "noadmin ok" -> "ادمین حذف شد"
            "password OK!" -> "تغییر رمز با موفقیت ثبت شد"
            "Door alarm!" -> "درب خودرو باز شده است"
            "door alarm!" -> "درب خودرو باز شده است"
            "ACC alarm!" -> "خودرو روشن شده است"
            "acc alarm!" -> "خودرو روشن شده است"
            "sensor alarm!" -> "خودرو ضربه خورده است"
            "Power alarm!" -> "برق خودرو قطع شده است"
            "power alarm!" -> "برق خودرو قطع شده است"
            "help me!" -> "درخواست کمک"
            "This number has been authorized!" -> "ادمین ثبت شد"
            "That authorized phone number is not setup in the tracker." -> "شماره تلفن ریموت حذف شد. با این شرایط امکان ارتباط با دستگاه وجود ندارد. لطفا شماره موبایل دستگاه را ثبت کنید."
            "pwd fail" -> "رمز اشتباه است"
            "set up fail! Pls turn off ACC" -> "دزد گیر به علت روشن بودن خودرو فعال نشد"
            "set up fail! Pls close the door" -> "دزد گیر به علت باز بودن درب خودرو فعال نشد"
            "only support 6 digits, password fail!" -> "حداکثر طول پسورد 6 رقم، تغییر پسورد انجام نشد."
            else -> code
        }
    }


    fun parseLocationResponse(message: String): LocationResponse {
        val messageArray = message.split("\n")
        val firstValuesArr = messageArray[0]
        val locationArray = firstValuesArr.split(" ")
        System.out.println("96363arr: " + locationArray)
        val location = LocationResponse()
        for (item in locationArray) {
            val key = item.split(":")
            if (key[0] == "lat") {
                location.lat = key[1]
                System.out.println("96363lat: " + key[1])
            } else if (key[0] == "lon") {
                location.lon = key[1]
                System.out.println("96363lon: " + key[1])
            }
        }
        return location
    }

    fun parseWarning(message: String): String {
        val messageArray = message.split("\n")
        val firstValuesArr = messageArray[0]
        return translateRemoteResponse(firstValuesArr)
    }


    fun parseCheckResponse(message: String): TotalCarCondition {
        System.out.println(message)

        // [Power: ON, Bat: 100%, GPRS: ON, GPS: ON, ACC: OFF, Door: OFF, GSM: 15, Oil: 0.00%, APN: cmnet,,;, IP: 67.229.77.202:9000, Arm: OFF, ]
        val atrArray = message.split("\n")
        val totalCarCondition = TotalCarCondition()
        for (item in atrArray) {
            val key = item.split(":")
            if (key[0] == "Power") {
                if (key[1].trim() == "ON") {
                    totalCarCondition.batteryStatus = "وصل"
                } else {
                    totalCarCondition.batteryStatus = "قطع"
                }
            } else if (key[0] == "Bat") {
                totalCarCondition.batteryPercent = key[1]
            } else if (key[0] == "GPRS") {
                if (key[1].trim() == "ON") {
                    totalCarCondition.netStatus = "فعال"
                } else {
                    totalCarCondition.netStatus = "غیر فعال"
                }
            } else if (key[0] == "GPS") {
                if (key[1].trim() == "ON") {
                    totalCarCondition.signalstatus = "وصل"
                } else {
                    totalCarCondition.signalstatus = "قطع"
                }
            } else if (key[0] == "ACC") {
                if (key[1].trim() == "ON") {
                    totalCarCondition.switchStatus = "باز"
                } else {
                    totalCarCondition.switchStatus = "بسته"
                }
            } else if (key[0] == "Arm") {
                if (key[1].trim() == "ON") {
                    totalCarCondition.thiefCatcher = "فعال"
                } else {
                    totalCarCondition.thiefCatcher = "غیر فعال"
                }
            } else if (key[0] == "Door") {
                if (key[1] == " ON") {
                    totalCarCondition.carDoor = "باز"
                } else {
                    totalCarCondition.carDoor = " بسته"
                }
            } else if (key[0] == "Oil") {
//                if (key[1] == "ON") {
//                    totalCarCondition.switchStatus = "فعال"
//                } else {
//                    totalCarCondition.switchStatus = "غیر فعال"
//                }
            }
        }

        return totalCarCondition
    }
}