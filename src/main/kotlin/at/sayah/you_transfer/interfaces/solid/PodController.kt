package at.sayah.you_transfer.interfaces.solid

import at.sayah.you_transfer.interfaces.solid.dto.CreateAccessRequestDTO
import at.sayah.you_transfer.interfaces.solid.exception.InvalidScopeValueException
import com.inrupt.client.auth.Session
import com.inrupt.client.solid.SolidContainer
import com.inrupt.client.solid.SolidSyncClient
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.validation.Valid
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import java.net.URI

@Validated
@Controller
@RequestMapping("/pod")
class PodController(
  private val solidAccessServiceFacade: SolidAccessServiceFacade
) {

  val log = KotlinLogging.logger {}

  companion object {
    const val ATTRIBUTE_ACCESS_REQUEST_DTO = "accessRequestDto"
    const val ATTRIBUTE_AVAILABLE_SCOPES = "availableScopes"
    const val ATTRIBUTE_IDENTIFIER = "identifier"
    const val ATTRIBUTE_RESOURCES = "resources"
    const val ATTRIBUTE_ACCESS_REQUESTS = "accessRequests"
    const val ATTRIBUTE_ACCESS_GRANTS = "accessGrants"
  }

  @GetMapping("/detail")
  fun detail(
    @RequestParam identifier: String,
    session: Session?,
    model: Model
  ): String {
    if (session != null) {
      val client = SolidSyncClient.getClient().session(session)

      val solidContainer = client.read(URI.create(identifier), SolidContainer::class.java)

      model.addAttribute(ATTRIBUTE_IDENTIFIER, identifier)
      model.addAttribute(ATTRIBUTE_RESOURCES, solidContainer.resources.map { it.identifier.toString() })

      model.addAttribute(ATTRIBUTE_ACCESS_REQUESTS, solidAccessServiceFacade.listAccessRequests(session).sortedBy { it.issuedAt }.reversed())
      model.addAttribute(ATTRIBUTE_ACCESS_GRANTS, solidAccessServiceFacade.listAccessGrants(session).sortedBy { it.issuedAt }.reversed())

      return "pod/resources/list"
    }

    return "redirect:/login"
  }

  @GetMapping("/accessRequest")
  fun accessRequest(
    @RequestParam identifier: String,
    session: Session?,
    @AuthenticationPrincipal oAuth2User: OAuth2User,
    model: Model
  ): String {
    if (session != null) {
      model.addAttribute(ATTRIBUTE_ACCESS_REQUEST_DTO, CreateAccessRequestDTO(null, null, listOf()))
      model.addAttribute(ATTRIBUTE_AVAILABLE_SCOPES, solidAccessServiceFacade.getAvailableScopes())
      model.addAttribute(ATTRIBUTE_IDENTIFIER, identifier)

      return "accessRequest/create"
    } else {
      return "redirect:/login"
    }
  }

  @PostMapping("/accessRequest")
  fun accessRequest(
    @RequestParam identifier: String,
    @Valid @ModelAttribute(ATTRIBUTE_ACCESS_REQUEST_DTO) accessRequestDTO: CreateAccessRequestDTO,
    bindingResult: BindingResult,
    session: Session?,
    @AuthenticationPrincipal oAuth2User: OAuth2User,
    model: Model,
    redirectAttributes: RedirectAttributes
  ): String {
    if (session != null) {
      if (!bindingResult.hasErrors()) {
        try {
          solidAccessServiceFacade.createAccessRequest(
            accessRequestDTO,
            oAuth2User.attributes["webid"] as String,
            session
          )
        } catch (e: InvalidScopeValueException) {
          model.addAttribute("error", e.message)
        }
      }

      model.addAttribute(ATTRIBUTE_AVAILABLE_SCOPES, solidAccessServiceFacade.getAvailableScopes())
      redirectAttributes.addAttribute(ATTRIBUTE_IDENTIFIER, identifier)

      return "redirect:/pod/detail"
    }

    return "redirect:/login"
  }

  @PostMapping("/accessRequest/grant")
  fun grantAccessRequest(
    @RequestParam identifier: String,
    @RequestParam accessRequestIdentifier: String,
    session: Session?,
    model: Model,
    redirectAttributes: RedirectAttributes
  ): String {
    if (session != null) {
      try {
        solidAccessServiceFacade.grantAccessRequest(accessRequestIdentifier, session)
      } catch (e: Exception) {
        log.error(e) { "Could not create access request" }
      }

      redirectAttributes.addAttribute(ATTRIBUTE_IDENTIFIER, identifier)

      return "redirect:/pod/detail"
    }

    return "redirect:/login"
  }

  @PostMapping("/accessRequest/deny")
  fun denyAccessRequest(
    @RequestParam identifier: String,
    @RequestParam accessRequestIdentifier: String,
    session: Session?,
    model: Model,
    redirectAttributes: RedirectAttributes
  ): String {
    if (session != null) {
      try {
        solidAccessServiceFacade.denyAccessRequest(accessRequestIdentifier, session)
      } catch (e: Exception) {
        log.error(e) { "Could not create access request" }
      }

      redirectAttributes.addAttribute(ATTRIBUTE_IDENTIFIER, identifier)

      return "redirect:/pod/detail"
    }

    return "redirect:/login"
  }

  @PostMapping("/accessGrant/revoke")
  fun revokeAccessGrant(
    @RequestParam identifier: String,
    @RequestParam accessGrantIdentifier: String,
    session: Session?,
    model: Model,
    redirectAttributes: RedirectAttributes
  ): String {
    if (session != null) {
      solidAccessServiceFacade.revokeAccessGrant(accessGrantIdentifier, session)

      redirectAttributes.addAttribute(ATTRIBUTE_IDENTIFIER, identifier)

      return "redirect:/pod/detail"
    }

    return "redirect:/login"
  }
}