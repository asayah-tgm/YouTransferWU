package at.sayah.you_transfer.extension.spring.solid

import com.inrupt.client.auth.Session
import com.inrupt.client.spring.SessionUtils
import org.springframework.core.MethodParameter
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

class SolidSessionArgumentResolver : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.parameterType == Session::class.java
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Any? {
        val authentication = SecurityContextHolder.getContext().authentication

        return if (authentication != null && authentication.principal is OAuth2User) {
            val session = SessionUtils.asSession(authentication.principal as OAuth2User)

            return if (session.isPresent) {
                session.get()
            } else {
                null
            }
        } else {
            null
        }
    }
}