package at.sayah.you_transfer.extension.spring.solid

import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import java.net.URI

interface DynamicClientRegistrationRepository : ClientRegistrationRepository {

  fun store(providerUrl: URI, dynamicClientRegistration: DynamicClientRegistration)

  fun findByOpenIdProviderUrl(providerUrl: URI): DynamicClientRegistration?

}