package com.mordaka.arkadiusz

import com.mordaka.arkadiusz.domain.FeeWagesCsv
import com.mordaka.arkadiusz.domain.TransactionCsv
import com.mordaka.arkadiusz.domain.TransactionService
import com.mordaka.arkadiusz.domain.port.IFeeDao
import com.mordaka.arkadiusz.domain.port.IFeeInfoDao
import com.mordaka.arkadiusz.domain.port.ITransactionDao
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.Mockito
import java.time.LocalDateTime


internal class TransactionServiceTest {

    private val transactionDao = Mockito.mock(ITransactionDao::class.java)
    private val feeDao = Mockito.mock(IFeeDao::class.java)
    private val feeInfoDao = Mockito.mock(IFeeInfoDao::class.java)

    private val instance = TransactionService(transactionDao, feeDao, feeInfoDao)

    @Test
    fun `transaction service should be initialized`() {
        //expect
        Assertions.assertNotNull(instance)
    }

    @Test
    fun `should return correct data`() {
        //given
        Mockito.`when`(transactionDao.getCustomerTransactionsDao()).thenReturn(testTransactionData(0.0))
        Mockito.`when`(feeDao.getFeePercentages()).thenReturn(feeWages)

        //when
        val aggregatedData = instance.getCustomerTransactions()

        //then
        Assertions.assertEquals(1, aggregatedData.size)
        Assertions.assertEquals(1L, aggregatedData[0].customerId)
        Assertions.assertEquals("Name", aggregatedData[0].firstName)
        Assertions.assertEquals("LastName", aggregatedData[0].lastName)
        Assertions.assertEquals(getDate, aggregatedData[0].lastTransactionDate)
        Assertions.assertEquals(1, aggregatedData[0].numberOfTransactions)
    }

    @ParameterizedTest
    @MethodSource("parametersFee")
    fun `should return correct fee value`(amount: Double, expected: Double) {
        //given
        Mockito.`when`(transactionDao.getCustomerTransactionsDao()).thenReturn(testTransactionData(amount))
        Mockito.`when`(feeDao.getFeePercentages()).thenReturn(feeWages)

        //when
        val aggregatedData = instance.getCustomerTransactions()

        //then
        Assertions.assertEquals(amount, aggregatedData[0].totalValueOfTransactions)
        Assertions.assertEquals(expected, aggregatedData[0].transactionsFeeValue)
    }

    private fun testTransactionData(transactionAmount: Double) = listOf(
        TransactionCsv(1L, transactionAmount, "Name", 1L, "LastName", getDate)
    )

    private val getDate = LocalDateTime.of(2021, 3, 7, 12, 12, 12)

    private val feeWages = listOf(
        FeeWagesCsv(1000, 3.5),
        FeeWagesCsv(2500, 2.5),
        FeeWagesCsv(5000, 1.1),
        FeeWagesCsv(10000, 0.1)
    )

    companion object {
        @JvmStatic
        fun parametersFee(): List<Arguments> {
            return listOf(
                Arguments.of(0.00, 0.0),
                Arguments.of(900.00, 31.50),
                Arguments.of(1000.00, 25.00),
                Arguments.of(2400.00, 60.00),
                Arguments.of(2500.00, 27.50),
                Arguments.of(4900.00, 53.90),
                Arguments.of(9900.00, 9.90),
                Arguments.of(10000.00, 10.00)
            )
        }
    }
}
