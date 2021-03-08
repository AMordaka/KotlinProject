package com.mordaka.arkadiusz.domain.port

import com.mordaka.arkadiusz.domain.FeeWagesCsv
import com.mordaka.arkadiusz.domain.TransactionCsv
import java.time.LocalDateTime

internal interface ITransactionDao {
    fun getCustomerTransactionsDao(): List<TransactionCsv>
    fun getTransactionsDaoByCustomerId(id: Long): List<TransactionCsv>
}

internal interface IFeeDao {
    fun getFeePercentages(): List<FeeWagesCsv>
}

internal interface IFeeInfoDao {
    fun saveInfo(customerId: Long, feeAmount: Double, dateOfFeeCalculation: LocalDateTime)
}