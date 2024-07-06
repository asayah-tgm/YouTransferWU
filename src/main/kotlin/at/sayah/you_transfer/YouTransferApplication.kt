package at.sayah.you_transfer

import at.sayah.you_transfer.infrastructure.properties.YouTransferProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(YouTransferProperties::class)
class YouTransferApplication

fun main(args: Array<String>) {
	runApplication<YouTransferApplication>(*args)
}
