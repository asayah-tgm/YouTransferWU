package at.sayah.you_transfer.interfaces.solid

import at.sayah.you_transfer.interfaces.solid.dto.ContainerDetailsDTO
import com.inrupt.client.solid.SolidContainer

interface SolidMapper {
    fun toDetails(solidContainer: SolidContainer): ContainerDetailsDTO
}