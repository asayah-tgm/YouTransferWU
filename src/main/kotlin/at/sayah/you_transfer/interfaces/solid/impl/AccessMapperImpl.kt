package at.sayah.you_transfer.interfaces.solid.impl

import at.sayah.you_transfer.interfaces.solid.AccessMapper
import at.sayah.you_transfer.interfaces.solid.dto.AccessDenialDetailDTO
import at.sayah.you_transfer.interfaces.solid.dto.AccessGrantDetailDTO
import at.sayah.you_transfer.interfaces.solid.dto.AccessGrantListDTO
import at.sayah.you_transfer.interfaces.solid.dto.AccessRequestListDTO
import com.inrupt.client.accessgrant.AccessDenial
import com.inrupt.client.accessgrant.AccessGrant
import com.inrupt.client.accessgrant.AccessRequest
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@Component
class AccessMapperImpl : AccessMapper {
  override fun toAccessGrantDTO(accessGrant: AccessGrant): AccessGrantDetailDTO {
    return AccessGrantDetailDTO(identifier = accessGrant.identifier.toString(), isUploadAllowed = isUploadAllowed(accessGrant))
  }

  private fun isUploadAllowed(accessGrant: AccessGrant): Boolean {
    return accessGrant.modes.contains("Append") || accessGrant.modes.contains("Write")
  }

  override fun toAccessRequestListDTO(accessRequests: List<AccessRequest>): List<AccessRequestListDTO> {
    return accessRequests.map {
      AccessRequestListDTO(
        identifier = it.identifier.toString(),
        creator = it.creator.toString(),
        resources = it.resources.map { resource -> resource.toString() },
        modes = it.modes.toList(),
        issuedAt = toLocalDateTime(it.issuedAt),
        expiresAt = toLocalDateTime(it.expiration),
        isExpired = isExpired(it)
      )
    }
  }

  private fun isExpired(accessRequest: AccessRequest): Boolean {
    return Instant.now().isAfter(accessRequest.expiration)
  }

  override fun toAccessDenialDTO(accessDenial: AccessDenial): AccessDenialDetailDTO {
    return AccessDenialDetailDTO(accessDenial.identifier.toString())
  }

  override fun toAccessGrantListDTO(accessGrants: List<AccessGrant>): List<AccessGrantListDTO> {
    return accessGrants.map {
      AccessGrantListDTO(
        identifier = it.identifier.toString(),
        creator = it.creator.toString(),
        resources = it.resources.map { resource -> resource.toString() },
        modes = it.modes.toList(),
        issuedAt = toLocalDateTime(it.issuedAt),
        expiresAt = toLocalDateTime(it.expiration),
        isExpired = isExpired(it)
      )
    }
  }

  fun isExpired(accessGrant: AccessGrant): Boolean {
    return Instant.now().isAfter(accessGrant.expiration)
  }

  fun toLocalDateTime(instant: Instant): LocalDateTime {
    return LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
  }
}