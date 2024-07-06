package at.sayah.you_transfer.interfaces.solid.dto

import java.time.LocalDateTime

data class AccessRequestListDTO(
  val identifier: String,
  val creator: String,
  val resources: List<String>,
  val modes: List<String>,
  val issuedAt: LocalDateTime,
  val expiresAt: LocalDateTime,
  val isExpired: Boolean
)
