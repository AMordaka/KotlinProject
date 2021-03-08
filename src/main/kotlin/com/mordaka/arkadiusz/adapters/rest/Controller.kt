package com.mordaka.arkadiusz.adapters.rest


import com.mordaka.arkadiusz.adapters.rest.dto.AggregateTransactionInfoDto
import com.mordaka.arkadiusz.domain.port.IService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
internal class Controller(
    private val IService: IService
) {
    @GetMapping("/api")
    fun getAggregatedTransactions(@RequestParam(required = false) customer_id: String?): ResponseEntity<List<AggregateTransactionInfoDto>> {
        if (paramIsEmptyOrAll(customer_id)) {
            return ResponseEntity.ok(IService.getCustomerTransactions())
        }
        return ResponseEntity.ok(getCustomerTransactions(customer_id!!))
    }

    private fun getCustomerTransactions(ids: String): List<AggregateTransactionInfoDto> {
        return ids.split(",")
            .map { it.trim().toLong() }
            .map { IService.getTransactionsByCustomerId(it) }
    }

    private fun paramIsEmptyOrAll(customerId: String?): Boolean {
        if (customerId.isNullOrBlank()) {
            return true
        }
        return customerId == "ALL"
    }
}
