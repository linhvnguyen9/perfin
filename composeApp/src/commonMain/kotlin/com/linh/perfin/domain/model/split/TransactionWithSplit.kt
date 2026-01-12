package com.linh.perfin.domain.model.split

import com.linh.perfin.domain.model.transaction.Transaction

data class TransactionWithSplit(
    val transaction: Transaction,
    val split: SplitTransaction?
) {
    fun hasSplit(): Boolean = split != null
}
