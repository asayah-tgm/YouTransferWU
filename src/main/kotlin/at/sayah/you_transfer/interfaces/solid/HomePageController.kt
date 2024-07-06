package at.sayah.you_transfer.interfaces.solid

import com.inrupt.client.auth.Session
import com.inrupt.client.solid.SolidSyncClient
import com.inrupt.client.webid.WebIdProfile
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import java.net.URI

@Controller
@RequestMapping("/")
class HomePageController {

  companion object {
    val storages = "storages"
  }

  @GetMapping
  fun homePage(model: Model, @AuthenticationPrincipal oAuth2User: OAuth2User, session: Session?): String {
    if (session != null) {
      val webId = oAuth2User.attributes["webid"] as String
      val webIdProfile = SolidSyncClient.getClient().session(session).read(URI.create(webId), WebIdProfile::class.java)

      model.addAttribute(storages, webIdProfile.storages.map { it.toString() })

      return "homePage"
    } else {
      return "redirect:/login"
    }
  }
}