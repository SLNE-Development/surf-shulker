package dev.slne.surf.shulker.spring

import dev.slne.surf.surfapi.core.api.util.requiredService
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.context.ConfigurableApplicationContext
import kotlin.reflect.KClass

private val shulkerInstance = requiredService<ShulkerInstance>()

interface ShulkerInstance {
    fun startSpringApplication(
        applicationClass: Class<*>,
        classLoader: ClassLoader = applicationClass.classLoader,
        vararg parentClassLoader: ClassLoader,
        customizer: SpringApplicationBuilder.() -> Unit = {}
    ): ConfigurableApplicationContext

    fun <B : Any> getBean(beanClass: Class<B>): B

    companion object : ShulkerInstance by shulkerInstance {
        val INSTANCE get() = shulkerInstance
    }
}

fun ShulkerInstance.startSpringApplication(
    applicationClass: KClass<*>,
    classLoader: ClassLoader = applicationClass.java.classLoader,
    vararg parentClassLoader: ClassLoader,
    customizer: SpringApplicationBuilder.() -> Unit = {}
) = startSpringApplication(
    applicationClass.java,
    classLoader,
    *parentClassLoader,
    customizer = customizer
)

inline fun <reified B : Any> ShulkerInstance.getBean(): B = getBean(B::class.java)

inline fun <reified A : KClass<*>> startSpringApplication(
    classLoader: ClassLoader = A::class.java.classLoader,
    vararg parentClassLoader: ClassLoader,
    noinline customizer: SpringApplicationBuilder.() -> Unit = {}
): ConfigurableApplicationContext = ShulkerInstance.INSTANCE.startSpringApplication(
    A::class,
    classLoader,
    *parentClassLoader,
    customizer = customizer
)