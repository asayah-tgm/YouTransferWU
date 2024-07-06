package at.sayah.you_transfer.domain.model

import com.inrupt.client.NonRDFSource
import java.io.InputStream
import java.net.URI

class SolidImage(
    identifier: URI,
    contentType: String,
    inputStream: InputStream
) : NonRDFSource(identifier, contentType, inputStream)