package at.sayah.you_transfer.interfaces.solid

import at.sayah.you_transfer.domain.model.*
import at.sayah.you_transfer.interfaces.solid.dto.ContainerDetailsDTO
import com.inrupt.client.auth.Session
import org.springframework.web.multipart.MultipartFile
import com.inrupt.client.accessgrant.*
import com.inrupt.client.solid.*

interface SolidResourcesServiceFacade {

  /**
   * Returns details about a [com.inrupt.client.solid.SolidContainer] identified by the given [identifier]
   */
  fun containerDetails(identifier: String, session: Session): ContainerDetailsDTO

  /**
   * Uploads images as [SolidImage]s
   */
  fun uploadPhotos(images: List<MultipartFile>, identifier: String, session: Session)

  /**
   * Uploads images as [SolidImage]s using an [AccessGrant]
   *
   * @param images images to upload
   * @param accessGrantIdentifier identifier of the [AccessGrant]. Used to build an [AccessGrantSession]
   * @param resourceIdentifier identifier of the [SolidResource] to upload photos to
   */
  fun uploadPhotos(images: List<MultipartFile>, accessGrantIdentifier: String, resourceIdentifier: String, session: Session)

  /**
   * Deletes a [SolidResource] using an [AccessGrant]
   *
   * @param accessGrantIdentifier identifier of the [AccessGrant]
   * @param resourceIdentifier identifier of the [SolidResource]
   */
  fun delete(accessGrantIdentifier: String, resourceIdentifier: String, session: Session)

  /**
   * Deletes a [SolidResource]
   *
   * @param identifier of the [SolidResource]
   */
  fun delete(identifier: String, session: Session)
}
