package at.sayah.you_transfer.interfaces.solid

import at.sayah.you_transfer.application.ImageConversionService
import at.sayah.you_transfer.domain.model.SolidImage
import com.inrupt.client.auth.Session
import com.inrupt.client.solid.SolidSyncClient
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.core.io.InputStreamResource
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import org.springframework.web.servlet.view.RedirectView
import java.net.URI

@Controller
@RequestMapping("/resources")
class ResourceController(
  private val solidAccessServiceFacade: SolidAccessServiceFacade,
  private val solidResourcesServiceFacade: SolidResourcesServiceFacade,
  private val imageConversionService: ImageConversionService
) {

  val log = KotlinLogging.logger {}

  companion object {
    const val ATTRIBUTE_CONTAINER = "container"
    const val ATTRIBUTE_ACCESS_GRANT_IDENTIFIER = "accessGrantIdentifier"
    const val ATTRIBUTE_ACCESS_GRANT = "accessGrant"
    const val ATTRIBUTE_RESOURCE_IDENTIFIER = "resourceIdentifier"
    const val ATTRIBUTE_IDENTIFIER = "identifier"
    const val ATTRIBUTE_PARENT_IDENTIFIER = "parentIdentifier"
  }

  @GetMapping("/detail")
  fun detail(
    @RequestParam parentIdentifier: String,
    @RequestParam(required = false) identifier: String?,
    @RequestParam(required = false) accessGrantIdentifier: String?,
    @RequestParam(required = false) resourceIdentifier: String?,
    session: Session?,
    model: Model
  ): String {
    if (session != null) {
      if (isShowAccessGrantRequest(accessGrantIdentifier, resourceIdentifier)) {
        val containerDetails = solidAccessServiceFacade.accessGrantContainerDetails(accessGrantIdentifier!!, resourceIdentifier!!, session)
        val accessGrantDetails = solidAccessServiceFacade.accessGrantDetails(accessGrantIdentifier, session)

        model.addAttribute(ATTRIBUTE_CONTAINER, containerDetails)
        model.addAttribute(ATTRIBUTE_ACCESS_GRANT_IDENTIFIER, accessGrantIdentifier)
        model.addAttribute(ATTRIBUTE_RESOURCE_IDENTIFIER, resourceIdentifier)
        model.addAttribute(ATTRIBUTE_ACCESS_GRANT, accessGrantDetails)
      } else {
        val containerDetails = solidResourcesServiceFacade.containerDetails(identifier!!, session)

        model.addAttribute(ATTRIBUTE_CONTAINER, containerDetails)
      }

      model.addAttribute(ATTRIBUTE_PARENT_IDENTIFIER, parentIdentifier)

      return "pod/resources/detail"
    }

    return "redirect:/login"
  }

  private fun isShowAccessGrantRequest(accessGrantIdentifier: String?, resourceIdentifier: String?): Boolean {
    return StringUtils.hasText(accessGrantIdentifier) && StringUtils.hasText(resourceIdentifier)
  }

  @GetMapping("/image")
  @ResponseBody
  fun image(
    @RequestParam(required = false) identifier: String?,
    @RequestParam(required = false) accessGrantIdentifier: String?,
    @RequestParam(required = false) resourceIdentifier: String?,
    model: Model,
    session: Session?
  ): ResponseEntity<InputStreamResource> {
    if (session != null) {
      val image: SolidImage;

      if (isShowAccessGrantRequest(accessGrantIdentifier, resourceIdentifier)) {
        image = solidAccessServiceFacade.accessGrantImage(accessGrantIdentifier!!, resourceIdentifier!!, session)
      } else if (identifier != null) {
        val client = SolidSyncClient.getClient().session(session)

        image = client.read(URI.create(identifier), SolidImage::class.java)
      } else {
        throw IllegalStateException("Request has neither required accessGrant- nor regular image view parameters")
      }

      val imageThumbnail = imageConversionService.createThumbnail(image.entity)

      return ResponseEntity.ok().contentType(MediaType.parseMediaType(image.contentType))
        .body(InputStreamResource(imageThumbnail))
    }

    return ResponseEntity.status(HttpStatus.PERMANENT_REDIRECT).location(URI.create("/login")).build()
  }

  @GetMapping("/upload")
  fun uploadPhotos(@RequestParam identifier: String, session: Session?, model: Model): String {
    if (session != null) {
      model.addAttribute("identifier", identifier)

      return "photos/upload"
    }
    return "redirect:/login"
  }

  @PostMapping("/upload")
  fun uploadPhotos(
    model: Model,
    @RequestParam("images") images: List<MultipartFile>,
    @RequestParam parentIdentifier: String,
    @RequestParam(required = false) identifier: String?,
    @RequestParam(required = false) accessGrantIdentifier: String?,
    @RequestParam(required = false) resourceIdentifier: String?,
    session: Session?,
    redirectAttributes: RedirectAttributes
  ): RedirectView {
    if (session != null) {
      if (isShowAccessGrantRequest(accessGrantIdentifier, resourceIdentifier)) {
        solidResourcesServiceFacade.uploadPhotos(images, accessGrantIdentifier!!, resourceIdentifier!!, session)

        redirectAttributes.addAttribute(ATTRIBUTE_ACCESS_GRANT_IDENTIFIER, accessGrantIdentifier)
        redirectAttributes.addAttribute(ATTRIBUTE_RESOURCE_IDENTIFIER, resourceIdentifier)
      } else if (isRegularRequest(identifier)) {
        solidResourcesServiceFacade.uploadPhotos(images, identifier!!, session)
      } else {
        throw IllegalStateException("Request has neither required accessGrant- nor regular image view parameters")
      }

      redirectAttributes.addAttribute(ATTRIBUTE_IDENTIFIER, identifier)
      redirectAttributes.addAttribute(ATTRIBUTE_PARENT_IDENTIFIER, parentIdentifier)

      return RedirectView("/resources/detail")
    }

    return RedirectView("redirect:/login")
  }

  @PostMapping("/delete")
  fun delete(
    @RequestParam parentIdentifier: String,
    @RequestParam(required = false) identifier: String?,
    @RequestParam(required = false) accessGrantIdentifier: String?,
    @RequestParam(required = false) resourceIdentifier: String?,
    session: Session?,
    redirectAttributes: RedirectAttributes
  ): String {
    if (session != null) {
      if (isShowAccessGrantRequest(accessGrantIdentifier, resourceIdentifier)) {
        solidResourcesServiceFacade.delete(accessGrantIdentifier!!, resourceIdentifier!!, session)
      } else if (isRegularRequest(identifier)) {
        solidResourcesServiceFacade.delete(identifier!!, session)
      }

      redirectAttributes.addAttribute(ATTRIBUTE_IDENTIFIER, parentIdentifier)

      return "redirect:/pod/detail"
    }

    return "redirect:/login"
  }

  private fun isRegularRequest(identifier: String?): Boolean {
    return StringUtils.hasText(identifier)
  }
}