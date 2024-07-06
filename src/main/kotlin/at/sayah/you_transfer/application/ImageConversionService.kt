package at.sayah.you_transfer.application

import java.io.InputStream

interface ImageConversionService {
  fun createThumbnail(inputStream: InputStream): InputStream
}