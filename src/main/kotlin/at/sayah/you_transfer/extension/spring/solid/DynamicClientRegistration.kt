package at.sayah.you_transfer.extension.spring.solid

import org.springframework.security.oauth2.client.registration.ClientRegistration

data class DynamicClientRegistration(
  val clientRegistration: ClientRegistration,
  val clientSecretExpiresAt: kotlinx.datetime.LocalDateTime
)
