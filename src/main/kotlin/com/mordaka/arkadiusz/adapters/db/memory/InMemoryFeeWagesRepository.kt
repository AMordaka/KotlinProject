package com.mordaka.arkadiusz.adapters.db.memory

import com.mordaka.arkadiusz.domain.FeeWagesCsv
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
internal class InMemoryFeeWagesRepository {

    private val map = ConcurrentHashMap<Long, FeeWagesCsv>()

    fun save(transactionCsv: FeeWagesCsv): FeeWagesCsv {
        map[transactionCsv.transaction_value_less_than] = transactionCsv
        return transactionCsv
    }

    fun getFees(): List<FeeWagesCsv> {
        return map.values.toList()
    }
}
