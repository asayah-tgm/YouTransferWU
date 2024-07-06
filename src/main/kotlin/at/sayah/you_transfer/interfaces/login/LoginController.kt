package at.sayah.you_transfer.interfaces.login

import at.sayah.you_transfer.interfaces.login.dto.LoginDTO
import com.inrupt.client.solid.SolidSyncClient
import com.inrupt.client.webid.WebIdProfile
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.validation.Valid
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.util.StringUtils
import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import java.net.MalformedURLException
import java.net.URI
import java.net.URISyntaxException
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Controller
@RequestMapping("/login")
class LoginController(
  val dynamicClientRegistrationRestServiceFacade: DynamicClientRegistrationRestServiceFacade
) {

  private val log = KotlinLogging.logger {}

  companion object {
    val availableOPs = listOf("https://login.inrupt.com")
  }

  @GetMapping
  fun login(model: Model, redirectAttributes: RedirectAttributes): String {

    if (!model.containsAttribute("availableOPs")) {
      model.addAttribute("availableOPs", availableOPs)
    }
    model.addAttribute("dto", LoginDTO())

    return "login"
  }

  @PostMapping
  fun login(@Valid @ModelAttribute("dto") dto: LoginDTO, model: Model, bindingResult: BindingResult, redirectAttributes: RedirectAttributes): String {
    if (!bindingResult.hasErrors()) {
      if (isOpenIdProviderRequest(dto)) {
        val openIdProviderUrl = dto.openIdProvider!!
        val existingClientRegistration: ClientRegistration? = dynamicClientRegistrationRestServiceFacade.findClientRegistrationByOpenIdProviderUrl(openIdProviderUrl)

        if (existingClientRegistration != null) {
          return "redirect:/oauth2/authorization/" + urlEncodeClientRegistrationId(existingClientRegistration.registrationId)
        } else {
          val clientRegistration: ClientRegistration = dynamicClientRegistrationRestServiceFacade.register(openIdProviderUrl)

          return "redirect:/oauth2/authorization/" + urlEncodeClientRegistrationId(clientRegistration.registrationId)
        }
      } else if (isWebIdRequest(dto)) {
        val webId = dto.webId!!

        if (!isValidUrl(webId)) {
          bindingResult.addError(FieldError("dto", "webId", "not a valid url"))

          return "login"
        }

        val profile = SolidSyncClient.getClient().read(URI.create(webId), WebIdProfile::class.java)

        redirectAttributes.addFlashAttribute("oidcIssuers", profile.oidcIssuers.map { it.toString() })

        return "redirect:/login/chooseProvider"
      }
    }

    model.addAttribute("availableOPs", availableOPs)
    model.addAttribute("dto", dto)

    return "login"
  }

  @GetMapping("/chooseProvider")
  fun selectOpenIdProvider(model: Model): String {
    model.addAttribute("dto", LoginDTO())

    return "chooseProvider"
  }

  private fun isValidUrl(webId: String): Boolean {
    try {
      URI.create(webId).toURL()

      return true
    } catch (e: MalformedURLException) {
      return false
    } catch (e: URISyntaxException) {
      return false
    } catch (e: IllegalArgumentException) {
      return false
    }
  }

  private fun urlEncodeClientRegistrationId(clientRegistrationId: String): String {
    return URLEncoder.encode(clientRegistrationId, StandardCharsets.UTF_8)
  }

  private fun isWebIdRequest(dto: LoginDTO): Boolean {
    return StringUtils.hasText(dto.webId)
  }

  private fun isOpenIdProviderRequest(dto: LoginDTO): Boolean {
    return StringUtils.hasText(dto.openIdProvider)
  }
}