package at.sayah.you_transfer.infrastructure.persistence

import at.sayah.you_transfer.extension.spring.solid.DynamicClientRegistration
import at.sayah.you_transfer.extension.spring.solid.DynamicClientRegistrationRepository
import org.springframework.security.oauth2.client.registration.ClientRegistration
import java.net.URI
import java.net.URLEncoder
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

class DynamicClientRegistrationRepositoryInMemoryImpl : DynamicClientRegistrationRepository {

  private val storage: MutableMap<URI, DynamicClientRegistration> = mutableMapOf()

  override fun findByRegistrationId(registrationId: String?): ClientRegistration {
    return storage.values
      .map { it.clientRegistration }
      .first { it.registrationId.equals(registrationId) }
  }

  override fun store(providerUrl: URI, dynamicClientRegistration: DynamicClientRegistration) {
    if (storage.containsKey(providerUrl)) {
      throw IllegalArgumentException("ProviderUrl [$providerUrl] already exists")
    }

    storage+=Pair(providerUrl, dynamicClientRegistration)
  }

  override fun findByOpenIdProviderUrl(providerUrl: URI): DynamicClientRegistration? {
    return storage.filter { it.key == providerUrl }
      .firstNotNullOfOrNull { it.value }
  }
}