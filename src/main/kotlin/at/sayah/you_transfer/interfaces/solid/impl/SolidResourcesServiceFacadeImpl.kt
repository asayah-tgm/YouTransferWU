package at.sayah.you_transfer.interfaces.solid.impl

import at.sayah.you_transfer.domain.model.SolidImage
import at.sayah.you_transfer.interfaces.solid.SolidMapper
import at.sayah.you_transfer.interfaces.solid.SolidResourcesServiceFacade
import at.sayah.you_transfer.interfaces.solid.dto.ContainerDetailsDTO
import at.sayah.you_transfer.interfaces.solid.impl.SolidAccessServiceFacadeImpl.Companion.PS_ACCESS_GRANT_URI
import com.inrupt.client.accessgrant.AccessGrant
import com.inrupt.client.accessgrant.AccessGrantClient
import com.inrupt.client.accessgrant.AccessGrantSession
import com.inrupt.client.auth.Session
import com.inrupt.client.solid.SolidClientException
import com.inrupt.client.solid.SolidContainer
import com.inrupt.client.solid.SolidSyncClient
import com.inrupt.client.util.URIBuilder
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.net.URI

@Service
class SolidResourcesServiceFacadeImpl(
  private val solidMapper: SolidMapper
) : SolidResourcesServiceFacade {

  val log = KotlinLogging.logger {}

  override fun containerDetails(identifier: String, session: Session): ContainerDetailsDTO {
    val client = SolidSyncClient.getClient()
      .session(session)

    val container = client.read(URI.create(identifier), SolidContainer::class.java)

    return solidMapper.toDetails(container)
  }

  override fun uploadPhotos(images: List<MultipartFile>, identifier: String, session: Session) {
    val client = createSyncClient(session)

    for (image in images) {
      val photographIdentifier = URIBuilder.newBuilder(URI.create(identifier)).path(image.originalFilename).build()

      val solidImage = SolidImage(photographIdentifier, image.contentType!!, image.inputStream)

      try {
        client.create(solidImage)
      } catch (e: SolidClientException) {
        log.error { "Couldn't upload image [${image.originalFilename}]" }
        log.error { e }
      }
    }
  }

  override fun uploadPhotos(images: List<MultipartFile>, accessGrantIdentifier: String, resourceIdentifier: String, session: Session) {
    val accessGrantSession = createAccessGrantSession(accessGrantIdentifier, session)

    uploadPhotos(images, resourceIdentifier, accessGrantSession)
  }

  private fun createSyncClient(session: Session): SolidSyncClient {
    return SolidSyncClient.getClient().session(session)
  }

  private fun createAccessGrantClient(session: Session): AccessGrantClient {
    return AccessGrantClient(PS_ACCESS_GRANT_URI).session(session)
  }

  private fun createAccessGrantSession(accessGrantIdentifier: String, session: Session): AccessGrantSession {
    val accessGrantClient = createAccessGrantClient(session)

    val accessGrant = accessGrantClient.fetch(URI.create(accessGrantIdentifier), AccessGrant::class.java).toCompletableFuture()
      .join()
    return AccessGrantSession.ofAccessGrant(session, accessGrant)
  }

  override fun delete(accessGrantIdentifier: String, resourceIdentifier: String, session: Session) {
    val accessGrantSession = createAccessGrantSession(accessGrantIdentifier, session)

    delete(resourceIdentifier, accessGrantSession)
  }

  override fun delete(identifier: String, session: Session) {
    val client = createSyncClient(session)

    try {
      client.delete(URI.create(identifier))

      log.info { "Resource [$identifier] deleted" }
    } catch (e: SolidClientException) {
      log.error(e) { "Couldn't delete resource [$identifier]" }
    }
  }
}