package at.sayah.you_transfer.extension.spring.solid.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class DynamicRegistrationRequestBody(
  @JsonProperty("application_type") val applicationType: String,
  @JsonProperty("redirect_uris") val redirectUris: List<String>,
  @JsonProperty("client_name") val clientName: String,
  @JsonProperty("token_endpoint_auth_method") val tokenEndpointAuthMethod: String
)
