package at.sayah.you_transfer.application.impl

import at.sayah.you_transfer.application.ImageConversionService
import net.coobird.thumbnailator.Thumbnails
import org.springframework.stereotype.Service
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream

@Service
class ImageConversionServiceImpl : ImageConversionService {
  override fun createThumbnail(inputStream: InputStream): InputStream {
    val outputStream = ByteArrayOutputStream()

    Thumbnails.of(inputStream).size(800, 600).outputFormat("PNG").outputQuality(1f).toOutputStream(outputStream)

    return ByteArrayInputStream(outputStream.toByteArray())
  }
}