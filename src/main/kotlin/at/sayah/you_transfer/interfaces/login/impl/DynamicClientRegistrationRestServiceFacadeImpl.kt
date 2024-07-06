package at.sayah.you_transfer.interfaces.login.impl

import at.sayah.you_transfer.extension.spring.solid.DynamicClientRegistration
import at.sayah.you_transfer.extension.spring.solid.DynamicClientRegistrationRepository
import at.sayah.you_transfer.extension.spring.solid.dto.DynamicRegistrationRequestBody
import at.sayah.you_transfer.extension.spring.solid.dto.DynamicRegistrationResponseBody
import at.sayah.you_transfer.infrastructure.properties.YouTransferProperties
import at.sayah.you_transfer.interfaces.login.DynamicClientRegistrationRestServiceFacade
import com.inrupt.client.auth.DPoP
import com.inrupt.client.openid.OpenIdProvider
import kotlinx.datetime.toLocalDateTime
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import java.net.URI

@Service
class DynamicClientRegistrationRestServiceFacadeImpl(
  private val dynamicClientRegistrationRepository: DynamicClientRegistrationRepository,
  private val youTransferProperties: YouTransferProperties,
  @Value("\${spring.application.name}") private val applicationName: String
) : DynamicClientRegistrationRestServiceFacade {

  override fun findClientRegistrationByOpenIdProviderUrl(openIdProviderUrl: String): ClientRegistration? {
    return dynamicClientRegistrationRepository.findByOpenIdProviderUrl(URI.create(openIdProviderUrl))?.clientRegistration
  }

  override fun register(openIdProviderUrl: String): ClientRegistration {
    val providerUrl = URI.create(openIdProviderUrl)
    val openIdProvider = OpenIdProvider(URI.create(openIdProviderUrl), DPoP.of())
    val openIdConfig = openIdProvider.metadata().toCompletableFuture().join()

    val restClient = RestClient.builder().baseUrl(openIdConfig.registrationEndpoint.toString()).build()

    val response = restClient.post()
      .body(DynamicRegistrationRequestBody("web", listOf(composeRedirectUri(providerUrl)), applicationName, "client_secret_basic"))
      .retrieve()
      .toEntity(DynamicRegistrationResponseBody::class.java)

    if (response.statusCode.is2xxSuccessful) {
      val body = response.body!!

      val clientRegistration = ClientRegistration.withRegistrationId(providerUrl.host)
        .clientId(body.clientId)
        .clientSecret(body.clientSecret)
        .authorizationGrantType(toAuthorizationGrantType(body))
        .authorizationUri(openIdConfig.authorizationEndpoint.toString())
        .tokenUri(openIdConfig.tokenEndpoint.toString())
        .jwkSetUri(openIdConfig.jwksUri.toString())
        .redirectUri(composeRedirectUri(providerUrl))
        .issuerUri(openIdConfig.issuer.toString())
        .scope(listOf("openid", "webid", "offline_access"))
        .build()

      val clientSecretExpiresOn = kotlinx.datetime.Instant
        .fromEpochMilliseconds(body.clientSecretExpiresAt.time)
        .toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
      val dynamicClientRegistration = DynamicClientRegistration(clientRegistration, clientSecretExpiresOn)

      dynamicClientRegistrationRepository.store(providerUrl, dynamicClientRegistration)

      return dynamicClientRegistration.clientRegistration
    } else {
      throw IllegalStateException("Illegal state")
    }
  }

  private fun toAuthorizationGrantType(body: DynamicRegistrationResponseBody): AuthorizationGrantType {
    return body.grantTypes
      .map { AuthorizationGrantType(it) }
      .first()
  }

  private fun composeRedirectUri(openIdProviderUrl: URI): String {
    return "${youTransferProperties.publicUrl}/login/oauth2/code/${openIdProviderUrl.host}"
  }
}