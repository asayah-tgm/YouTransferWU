package at.sayah.you_transfer.infrastructure.config

import at.sayah.you_transfer.extension.spring.solid.SolidSessionArgumentResolver
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect
import org.thymeleaf.extras.springsecurity6.dialect.SpringSecurityDialect
import org.thymeleaf.spring6.ISpringTemplateEngine
import org.thymeleaf.spring6.SpringTemplateEngine
import org.thymeleaf.templateresolver.ITemplateResolver

@Configuration
class WebMvcConfig : WebMvcConfigurer {
  override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
    resolvers.add(SolidSessionArgumentResolver())
  }

  @Bean
  fun templateEngine(templateResolver: ITemplateResolver): ISpringTemplateEngine {
    val engine = SpringTemplateEngine()

    engine.addDialect(Java8TimeDialect())
    engine.addDialect(SpringSecurityDialect())
    engine.setTemplateResolver(templateResolver)

    return engine
  }
}