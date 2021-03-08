package com.mordaka.arkadiusz.adapters.csv

import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.mordaka.arkadiusz.adapters.db.memory.InMemoryFeeWagesRepository
import com.mordaka.arkadiusz.adapters.db.memory.InMemoryTransactionRepository
import com.mordaka.arkadiusz.domain.FeeWagesCsv
import com.mordaka.arkadiusz.domain.TransactionCsv
import com.mordaka.arkadiusz.domain.port.IFeeDao
import com.mordaka.arkadiusz.domain.port.ITransactionDao
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component
import java.io.FileReader
import javax.annotation.PostConstruct

@Component
internal class CsvLoader(
    val transactionRepository: InMemoryTransactionRepository,
    val feeWagesRepository: InMemoryFeeWagesRepository
) : ITransactionDao, IFeeDao {

    private val csvMapper = CsvMapper().apply {
        registerModule(KotlinModule())
        registerModule(JavaTimeModule())
    }

    @PostConstruct
    fun init() {
        loadTransactions()
        loadFeeWages()
    }

    override fun getCustomerTransactionsDao(): List<TransactionCsv> {
        return transactionRepository.getAllTransactions()
    }

    override fun getTransactionsDaoByCustomerId(id: Long): List<TransactionCsv> {
        return transactionRepository.findTransactionsByCustomerId(id)
    }

    override fun getFeePercentages(): List<FeeWagesCsv> {
        return feeWagesRepository.getFees()
    }

    private fun loadTransactions() {
        return readCsvFile<TransactionCsv>(ClassPathResource("/static/transactions.csv").file.absolutePath)
            .parallelStream().forEach { run { transactionRepository.save(it) } }
    }

    private fun loadFeeWages() {
        readCsvFile<FeeWagesCsv>(ClassPathResource("/static/fee_wages.csv").file.absolutePath)
            .forEach { run { feeWagesRepository.save(it) } }
    }

    private inline fun <reified T> readCsvFile(fileName: String): List<T> {
        FileReader(fileName).use { reader ->
            return csvMapper
                .readerFor(T::class.java)
                .with(CsvSchema.emptySchema().withHeader())
                .readValues<T>(reader)
                .readAll()
                .toList()
        }
    }
}
