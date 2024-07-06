package at.sayah.you_transfer.interfaces.solid.impl

import at.sayah.you_transfer.interfaces.solid.SolidMapper
import at.sayah.you_transfer.interfaces.solid.dto.ContainerDetailsDTO
import at.sayah.you_transfer.interfaces.solid.dto.ResourceDetailDTO
import com.inrupt.client.solid.SolidContainer
import com.inrupt.client.solid.SolidResource
import org.springframework.stereotype.Component

@Component
class SolidMapperImpl : SolidMapper {
    override fun toDetails(solidContainer: SolidContainer): ContainerDetailsDTO {
        solidContainer.resources.forEach { it.entity }
        return ContainerDetailsDTO(
            solidContainer.identifier.toString(),
            solidContainer.resources.map{toResourceDetail(it)}
        )
    }

    private fun toResourceDetail(solidResource: SolidResource): ResourceDetailDTO {
        return ResourceDetailDTO(
            solidResource.identifier.toString(),
        )
    }
}