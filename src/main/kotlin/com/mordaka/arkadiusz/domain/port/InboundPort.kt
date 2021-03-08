package com.mordaka.arkadiusz.domain.port

import com.mordaka.arkadiusz.adapters.rest.dto.AggregateTransactionInfoDto

internal interface IService {
    fun getCustomerTransactions(ids: String?): List<AggregateTransactionInfoDto>
}
