package com.mordaka.arkadiusz.adapters.rest.dto

import java.time.LocalDateTime

data class AggregateTransactionInfoDto(
    val firstName: String,
    val lastName: String,
    val customerId: Long,
    val numberOfTransactions: Long,
    val totalValueOfTransactions: Double,
    val transactionsFeeValue: Double,
    val lastTransactionDate: LocalDateTime
)
