package at.sayah.you_transfer.interfaces.solid

import at.sayah.you_transfer.domain.model.SolidImage
import at.sayah.you_transfer.interfaces.solid.dto.*
import com.inrupt.client.auth.Session
import com.inrupt.client.solid.*
import com.inrupt.client.accessgrant.*

/**
 * Facade for interacting with the [AccessGrantClient]
 */
interface SolidAccessServiceFacade {

    /**
     * Returns a list of all [AccessRequest]s for the [session]
     */
    fun listAccessRequests(session: Session): List<AccessRequestListDTO>

    /**
     * Returns a list of all [AccessGrant]s for the [session]
     */
    fun listAccessGrants(session: Session): List<AccessGrantListDTO>

    /**
     * Grants an [AccessRequest] with the given [identifier]
     */
    fun grantAccessRequest(identifier: String, session: Session): AccessGrantDetailDTO

    /**
     * Denies an [AccessRequest] with the given [identifier]
     */
    fun denyAccessRequest(identifier: String, session: Session): AccessDenialDetailDTO

    /**
     * Revokes an [AccessGrant]
     *
     * @param identifier of an [AccessGrant]
     */
    fun revokeAccessGrant(identifier: String, session: Session)

    /**
     * Creates an access request for the given [creatorWebId]
     */
    fun createAccessRequest(createAccessRequestDTO: CreateAccessRequestDTO, creatorWebId: String, session: Session)

    /**
     * Returns the available scopes for creating an [AccessRequest]
     */
    fun getAvailableScopes(): List<String>

    /**
     * Returns details about a [SolidResource] for an [AccessGrant]
     *
     * @param accessGrantIdentifier identifier of the [AccessGrant]
     * @param resourceIdentifier identifier of the [SolidResource]
     */
    fun accessGrantContainerDetails(accessGrantIdentifier: String, resourceIdentifier: String, session: Session): ContainerDetailsDTO


    fun accessGrantImage(accessGrantIdentifier: String, resourceIdentifier: String, session: Session): SolidImage

    /**
     * Returns details about an [AccessGrant]
     *
     * @param accessGrantIdentifier identifier of the [AccessGrant]
     */
    fun accessGrantDetails(accessGrantIdentifier: String, session: Session): AccessGrantDetailDTO
}