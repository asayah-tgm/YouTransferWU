package at.sayah.you_transfer.interfaces.solid

import at.sayah.you_transfer.interfaces.solid.dto.AccessDenialDetailDTO
import at.sayah.you_transfer.interfaces.solid.dto.AccessGrantDetailDTO
import at.sayah.you_transfer.interfaces.solid.dto.AccessGrantListDTO
import at.sayah.you_transfer.interfaces.solid.dto.AccessRequestListDTO
import com.inrupt.client.accessgrant.AccessDenial
import com.inrupt.client.accessgrant.AccessGrant
import com.inrupt.client.accessgrant.AccessRequest

interface AccessMapper {
  fun toAccessRequestListDTO(accessRequests: List<AccessRequest>): List<AccessRequestListDTO>

  fun toAccessGrantListDTO(accessGrants: List<AccessGrant>): List<AccessGrantListDTO>

  fun toAccessGrantDTO(accessGrant: AccessGrant): AccessGrantDetailDTO

  fun toAccessDenialDTO(accessDenial: AccessDenial): AccessDenialDetailDTO
}