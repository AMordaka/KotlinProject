package com.mordaka.arkadiusz.domain

import com.mordaka.arkadiusz.adapters.rest.dto.AggregateTransactionInfoDto
import com.mordaka.arkadiusz.domain.port.IFeeDao
import com.mordaka.arkadiusz.domain.port.IService
import com.mordaka.arkadiusz.domain.port.ITransactionDao
import org.springframework.stereotype.Component

@Component
internal class TransactionService(
    private val transactionDao: ITransactionDao,
    private val feeDao: IFeeDao
) : IService {

    override fun getCustomerTransactions(): List<AggregateTransactionInfoDto> {
        return transactionDao.getCustomerTransactionsDao()
            .groupBy { it.customer_id }
            .map { (_, value) -> value.toAggregateTransactionInfoDto() }
    }

    override fun getTransactionsByCustomerId(id: Long): AggregateTransactionInfoDto {
        return transactionDao.getTransactionsDaoByCustomerId(id).toAggregateTransactionInfoDto()
    }

    private fun List<TransactionCsv>.toAggregateTransactionInfoDto(): AggregateTransactionInfoDto =
        AggregateTransactionInfoDto(
            firstName = first().customer_first_name,
            lastName = first().customer_last_name,
            customerId = first().customer_id,
            numberOfTransactions = count().toLong(),
            totalValueOfTransactions = sumOf { it.transaction_amount },
            transactionsFeeValue = sumOf { it.transaction_amount }.calculateFee().round(),
            lastTransactionDate = maxByOrNull { it.transaction_date }!!.transaction_date
        )

    private fun Double.calculateFee(): Double {
        val wages = feeDao.getFeePercentages().sortedBy { it.transaction_value_less_than }
        wages.forEach {
            if (this < it.transaction_value_less_than) {
                return this * (it.fee_percentage_of_transaction_value / 100)
            }
        }
        return this * (wages.last().fee_percentage_of_transaction_value / 100)
    }

    private fun Double.round(decimals: Int = 2): Double = "%.${decimals}f".format(this).toDouble()
}





