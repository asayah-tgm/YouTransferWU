package at.sayah.you_transfer.interfaces.solid.dto

import jakarta.validation.constraints.NotEmpty
import org.hibernate.validator.constraints.URL

data class CreateAccessRequestDTO(
  @URL
  @NotEmpty
  val webId: String?,

  @URL
  @NotEmpty
  val resourceIdentifier: String?,

  @NotEmpty
  val scopes: List<String>
) {

}
