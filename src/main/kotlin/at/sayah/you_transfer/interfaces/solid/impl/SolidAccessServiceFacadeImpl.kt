package at.sayah.you_transfer.interfaces.solid.impl

import at.sayah.you_transfer.domain.model.SolidImage
import at.sayah.you_transfer.interfaces.solid.AccessMapper
import at.sayah.you_transfer.interfaces.solid.SolidAccessServiceFacade
import at.sayah.you_transfer.interfaces.solid.SolidMapper
import at.sayah.you_transfer.interfaces.solid.exception.InvalidScopeValueException
import at.sayah.you_transfer.interfaces.solid.dto.*
import com.inrupt.client.accessgrant.AccessCredentialQuery
import com.inrupt.client.accessgrant.AccessGrant
import com.inrupt.client.accessgrant.AccessGrantClient
import com.inrupt.client.accessgrant.AccessGrantSession
import com.inrupt.client.accessgrant.AccessRequest
import com.inrupt.client.auth.Session
import com.inrupt.client.solid.SolidClientException
import com.inrupt.client.solid.SolidContainer
import com.inrupt.client.solid.SolidSyncClient
import com.inrupt.client.util.URIBuilder
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.net.URI
import java.time.Duration
import java.time.Instant

@Service
class SolidAccessServiceFacadeImpl(
  private val accessMapper: AccessMapper,
  private val solidMapper: SolidMapper
) : SolidAccessServiceFacade {

  val log = KotlinLogging.logger {}

  companion object {
    val PS_ACCESS_GRANT_URI: URI = URI.create("https://vc.inrupt.com")
    val AVAILABLE_SCOPES = listOf("Read", "Write", "Append")
  }

  override fun listAccessRequests(session: Session): List<AccessRequestListDTO> {
    val client = createAccessGrantClient(session)

    val accessCredentialQuery = AccessCredentialQuery.newBuilder().build(AccessRequest::class.java)
    val accessRequests = client.query(accessCredentialQuery).toCompletableFuture().join()

    return accessMapper.toAccessRequestListDTO(accessRequests)
  }

  override fun accessGrantDetails(accessGrantIdentifier: String, session: Session): AccessGrantDetailDTO {
    val accessGrantClient = createAccessGrantClient(session)

    val accessGrant = accessGrantClient.fetch(URI.create(accessGrantIdentifier), AccessGrant::class.java).toCompletableFuture().join()

    return accessMapper.toAccessGrantDTO(accessGrant)
  }

  override fun listAccessGrants(session: Session): List<AccessGrantListDTO> {
    val client = createAccessGrantClient(session)

    val accessGrantQuery = AccessCredentialQuery.newBuilder().build(AccessGrant::class.java)
    val accessGrants = client.query(accessGrantQuery).toCompletableFuture().join()

    return accessMapper.toAccessGrantListDTO(accessGrants)
  }

  override fun accessGrantContainerDetails(accessGrantIdentifier: String, resourceIdentifier: String, session: Session): ContainerDetailsDTO {
    val accessGrantSession = createAccessGrantSession(accessGrantIdentifier, session)

    val solidClient = SolidSyncClient.getClient().session(accessGrantSession)

    val container = solidClient.read(URI.create(resourceIdentifier), SolidContainer::class.java)

    return solidMapper.toDetails(container)
  }

  override fun accessGrantImage(accessGrantIdentifier: String, resourceIdentifier: String, session: Session): SolidImage {
    val accessGrantSession = createAccessGrantSession(accessGrantIdentifier, session)

    val solidClient = SolidSyncClient.getClient().session(accessGrantSession)

    return solidClient.read(URI.create(resourceIdentifier), SolidImage::class.java)
  }

  override fun grantAccessRequest(identifier: String, session: Session): AccessGrantDetailDTO {
    val client = createAccessGrantClient(session)

    val accessRequest = client.fetch(URI.create(identifier), AccessRequest::class.java)
      .toCompletableFuture()
      .join()

    val accessGrant = client.grantAccess(accessRequest).toCompletableFuture().join()

    return accessMapper.toAccessGrantDTO(accessGrant)
  }

  override fun denyAccessRequest(identifier: String, session: Session): AccessDenialDetailDTO {
    val client = createAccessGrantClient(session)

    val accessRequest = client.fetch(URI.create(identifier), AccessRequest::class.java)
      .toCompletableFuture()
      .join()

    val accessDenial = client.denyAccess(accessRequest).toCompletableFuture().join()

    log.info { "AccessRequest [${accessRequest.identifier}] denied" }

    return accessMapper.toAccessDenialDTO(accessDenial)
  }

  override fun revokeAccessGrant(identifier: String, session: Session) {
    val client = createAccessGrantClient(session)

    val accessGrant = client.fetch(URI.create(identifier), AccessGrant::class.java).toCompletableFuture().join()

    client.revoke(accessGrant)

    log.info { "AccessGrant [${accessGrant.identifier}] revoked" }
  }

  override fun createAccessRequest(
    createAccessRequestDTO: CreateAccessRequestDTO,
    creatorWebId: String,
    session: Session
  ) {
    validateScopesValid(createAccessRequestDTO)

    val accessGrantClient = createAccessGrantClient(session)

    val accessRequest = AccessRequest.RequestParameters.newBuilder()
      .recipient(URI.create(createAccessRequestDTO.webId!!))
      .modes(createAccessRequestDTO.scopes)
      .expiration(Instant.now().plus(Duration.ofMinutes(15)))
      .resources(listOf(URI.create(createAccessRequestDTO.resourceIdentifier!!)))
      .build()

    val completedAccessRequest = accessGrantClient.requestAccess(accessRequest).toCompletableFuture().join()

    log.info { "AccessRequest [${completedAccessRequest.identifier}] created" }
  }

  override fun getAvailableScopes(): List<String> {
    return AVAILABLE_SCOPES
  }

  private fun createAccessGrantClient(session: Session): AccessGrantClient {
    return AccessGrantClient(PS_ACCESS_GRANT_URI).session(session)
  }

  private fun validateScopesValid(accessRequestDTO: CreateAccessRequestDTO) {
    for (scope in accessRequestDTO.scopes) {
      if (!AVAILABLE_SCOPES.contains(scope)) {
        throw InvalidScopeValueException("Invalid scope value $scope")
      }
    }
  }

  private fun createAccessGrantSession(accessGrantIdentifier: String, session: Session): AccessGrantSession {
    val accessGrantClient = createAccessGrantClient(session)

    val accessGrant = accessGrantClient.fetch(URI.create(accessGrantIdentifier), AccessGrant::class.java).toCompletableFuture()
      .join()
    return AccessGrantSession.ofAccessGrant(session, accessGrant)
  }
}