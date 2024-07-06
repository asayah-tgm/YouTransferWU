package at.sayah.you_transfer.interfaces.login

import org.springframework.security.oauth2.client.registration.ClientRegistration

interface DynamicClientRegistrationRestServiceFacade {

  fun findClientRegistrationByOpenIdProviderUrl(openIdProviderUrl: String): ClientRegistration?

  fun register(openIdProviderUrl: String): ClientRegistration
}
