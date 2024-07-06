package at.sayah.you_transfer.infrastructure.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("you-transfer")
data class YouTransferProperties(
  /**
   * Public URL under which this application is accessible from. Used for OAuth2 authorization code flow redirects
   */
  val publicUrl: String = ""
)
