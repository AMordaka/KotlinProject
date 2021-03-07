package com.mordaka.arkadiusz.adapters.db.mongodb

import com.mordaka.arkadiusz.domain.port.IFeeInfoDao
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Component
import java.time.LocalDateTime


internal interface FeeInfoRepository : MongoRepository<FeeInfo, String>

@Component
internal class FeeInfoImpl(
    private val repository: FeeInfoRepository
) : IFeeInfoDao {

    override fun saveInfo(customerId: Long, feeAmount: Double, dateOfFeeCalculation: LocalDateTime) {
        repository.save(FeeInfo(ObjectId(), customerId, feeAmount, dateOfFeeCalculation))
    }
}

@Document
internal data class FeeInfo(
    @Id
    val id: ObjectId = ObjectId.get(),
    val customerId: Long,
    val feeAmount: Double,
    val dateOfFeeCalculation: LocalDateTime = LocalDateTime.now(),
)
