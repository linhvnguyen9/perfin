package com.linh.perfin.domain.model.split

enum class SplitMode(val displayName: String) {
    EQUAL("Split Equally"),
    PERCENTAGE("By Percentage"),
    AMOUNT("By Amount"),
    SHARES("By Shares");

    companion object {
        fun fromString(value: String): SplitMode {
            return entries.find { it.name == value } ?: EQUAL
        }
    }
}
