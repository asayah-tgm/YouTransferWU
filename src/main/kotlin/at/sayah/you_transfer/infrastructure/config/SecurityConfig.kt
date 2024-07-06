package at.sayah.you_transfer.infrastructure.config

import at.sayah.you_transfer.extension.spring.solid.DynamicClientRegistrationRepository
import at.sayah.you_transfer.infrastructure.persistence.DynamicClientRegistrationRepositoryInMemoryImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.oauth2.client.oidc.authentication.OidcIdTokenDecoderFactory
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher

@Configuration
@EnableWebSecurity
class SecurityConfig {

  @Bean
  fun filterChain(http: HttpSecurity): SecurityFilterChain {
    http {
      authorizeHttpRequests {
        authorize(antMatcher("/error"), permitAll)
        authorize(antMatcher("/webjars/**"), permitAll)
        authorize(antMatcher("/login"), permitAll)
        authorize(antMatcher("/login/chooseProvider"), permitAll)
        authorize(antMatcher("/callback"), permitAll)
        authorize(anyRequest, authenticated)
      }
      oauth2Login {
        clientRegistrationRepository = clientRegistrationRepository()
        loginPage = "/login"
      }

    }
    return http.build()
  }

  @Bean
  fun clientRegistrationRepository(): DynamicClientRegistrationRepository {
    return DynamicClientRegistrationRepositoryInMemoryImpl()
  }

  @Bean
  fun idTokenDecoderFactory(): OidcIdTokenDecoderFactory {
    val idTokenDecoderFactory = OidcIdTokenDecoderFactory()
    idTokenDecoderFactory.setJwsAlgorithmResolver {
      SignatureAlgorithm.ES256
    }
    return idTokenDecoderFactory
  }
}