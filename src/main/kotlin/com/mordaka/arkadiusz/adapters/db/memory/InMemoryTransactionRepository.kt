package com.mordaka.arkadiusz.adapters.db.memory

import com.mordaka.arkadiusz.domain.TransactionCsv
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
internal class InMemoryTransactionRepository {

    private val map = ConcurrentHashMap<Long, TransactionCsv>()

    fun save(transactionCsv: TransactionCsv): TransactionCsv {
        map[transactionCsv.transaction_id] = transactionCsv
        return transactionCsv
    }

    fun getAllTransactions(): List<TransactionCsv> {
        return map.values.toList()
    }

    fun findTransactionsByCustomerId(customerId: Long): List<TransactionCsv> {
        return map.filter { (_, value) -> value.customer_id == customerId }.values.toList()
    }
}
