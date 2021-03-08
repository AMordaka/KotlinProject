package com.mordaka.arkadiusz.domain

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

internal data class TransactionCsv(
    val transaction_id: Long,
    @JsonDeserialize(using = DoubleDeserializer::class)
    val transaction_amount: Double,
    val customer_first_name: String,
    val customer_id: Long,
    val customer_last_name: String,
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    val transaction_date: LocalDateTime
)

internal data class FeeWagesCsv(
    val transaction_value_less_than: Long,
    @JsonDeserialize(using = DoubleDeserializer::class)
    val fee_percentage_of_transaction_value: Double
)

internal class DoubleDeserializer : JsonDeserializer<Double>() {
    override fun deserialize(parser: JsonParser, context: DeserializationContext): Double {
        return parser.text.replace(",", ".").toDouble()
    }
}

internal class LocalDateTimeDeserializer : JsonDeserializer<LocalDateTime>() {
    override fun deserialize(parser: JsonParser, context: DeserializationContext): LocalDateTime {
        return LocalDateTime.parse(parser.text, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))
    }
}
