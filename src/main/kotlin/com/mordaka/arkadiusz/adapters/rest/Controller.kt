package com.mordaka.arkadiusz.adapters.rest


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
    fun getAggregatedTransactions(@RequestParam(required = false) customer_id: String?) =
        ResponseEntity.ok(IService.getCustomerTransactions(customer_id))
}
