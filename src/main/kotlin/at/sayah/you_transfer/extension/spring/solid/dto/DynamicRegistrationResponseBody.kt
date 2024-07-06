package at.sayah.you_transfer.extension.spring.solid.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.Date

data class DynamicRegistrationResponseBody(
  @JsonProperty("client_id")
  val clientId: String,
  @JsonProperty("client_secret")
  val clientSecret: String,
  @JsonProperty("client_secret_expires_at")
  val clientSecretExpiresAt: Date,
  @JsonProperty("grant_types")
  val grantTypes: List<String>,

)
