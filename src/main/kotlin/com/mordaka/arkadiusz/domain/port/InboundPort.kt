package com.mordaka.arkadiusz.domain.port

import com.mordaka.arkadiusz.adapters.rest.dto.AggregateTransactionInfoDto

internal interface IService {
    fun getCustomerTransactions(): List<AggregateTransactionInfoDto>
    fun getTransactionsByCustomerId(id: Long): AggregateTransactionInfoDto
}
