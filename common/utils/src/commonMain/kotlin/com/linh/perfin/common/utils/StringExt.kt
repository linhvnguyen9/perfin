package com.linh.perfin.common.utils

fun String.removeVietnameseAccent(): String {
    var str = this
    str = str.replace("[àáạảãâầấậẩẫăằắặẳẵ]".toRegex(), "a")
    str = str.replace("[èéẹẻẽêềếệểễ]".toRegex(), "e")
    str = str.replace("[ìíịỉĩ]".toRegex(), "i")
    str = str.replace("[òóọỏõôồốộổỗơờớợởỡ]".toRegex(), "o")
    str = str.replace("[ó]".toRegex(), "o")
    str = str.replace("[ùúụủũưừứựửữ]".toRegex(), "u")
    str = str.replace("[ỳýỵỷỹ]".toRegex(), "y")
    str = str.replace("đ".toRegex(), "d")
    str = str.replace("[ÀÁẠẢÃÂẦẤẬẨẪĂẰẮẶẲẴ]".toRegex(), "A")
    str = str.replace("[ÈÉẸẺẼÊỀẾỆỂỄ]".toRegex(), "E")
    str = str.replace("[ÌÍỊỈĨ]".toRegex(), "I")
    str = str.replace("[ÒÓỌỎÕÔỒỐỘỔỖƠỜỚỢỞỠ]".toRegex(), "O")
    str = str.replace("[ÙÚỤỦŨƯỪỨỰỬỮ]".toRegex(), "U")
    str = str.replace("[ỲÝỴỶỸ]".toRegex(), "Y")
    str = str.replace("Đ".toRegex(), "D")
    return str
}