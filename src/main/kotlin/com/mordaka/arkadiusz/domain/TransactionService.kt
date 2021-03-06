package com.mordaka.arkadiusz.domain

import com.mordaka.arkadiusz.adapters.rest.dto.AggregateTransactionInfoDto
import com.mordaka.arkadiusz.domain.port.IFeeDao
import com.mordaka.arkadiusz.domain.port.IFeeInfoDao
import com.mordaka.arkadiusz.domain.port.IService
import com.mordaka.arkadiusz.domain.port.ITransactionDao
import org.springframework.stereotype.Component
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.time.LocalDateTime
import java.util.*


@Component
internal class TransactionService(
    private val transactionDao: ITransactionDao,
    private val feeDao: IFeeDao,
    private val feeInfoDao: IFeeInfoDao
) : IService {

    override fun getCustomerTransactions(ids: String?): List<AggregateTransactionInfoDto> {
        if (paramIsEmptyOrAll(ids)) {
            return transactionDao.getCustomerTransactionsDao()
                .groupBy { it.customer_id }
                .map { (_, value) -> value.toAggregateTransactionInfoDto() }
        }
        return ids!!.split(",")
            .map { it.trim().toLong() }
            .map { transactionDao.getTransactionsDaoByCustomerId(it) }
            .filter { it.isNotEmpty() }
            .map { it.toAggregateTransactionInfoDto() }
    }

    private fun paramIsEmptyOrAll(customerId: String?): Boolean {
        if (customerId.isNullOrBlank()) {
            return true
        }
        return customerId == "ALL"
    }

    private fun List<TransactionCsv>.toAggregateTransactionInfoDto(): AggregateTransactionInfoDto {
        val aggregatedTransaction = AggregateTransactionInfoDto(
            firstName = first().customer_first_name,
            lastName = first().customer_last_name,
            customerId = first().customer_id,
            numberOfTransactions = count().toLong(),
            totalValueOfTransactions = sumOf { it.transaction_amount },
            transactionsFeeValue = sumOf { it.transaction_amount }.calculateFee().round(),
            lastTransactionDate = maxByOrNull { it.transaction_date }!!.transaction_date
        )
        feeInfoDao.saveInfo(
            aggregatedTransaction.customerId,
            aggregatedTransaction.transactionsFeeValue,
            LocalDateTime.now()
        )
        return aggregatedTransaction
    }

    private fun Double.calculateFee(): Double {
        val wage = feeDao.getFeePercentages().firstOrNull { it.transaction_value_less_than > this }
        return if (wage != null) {
            this * (wage.fee_percentage_of_transaction_value / 100)
        } else this * (0.1 / 100)
    }

    private fun Double.round(): Double =
        DecimalFormat("#.##", DecimalFormatSymbols.getInstance(Locale.ENGLISH)).format(this).toDouble()

}





