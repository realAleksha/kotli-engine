package kotli.engine

import kotlin.reflect.KClass

/**
 * Provides dependencies on some type of objects.
 *
 * @param T The type of objects.
 */
interface DependencyProvider<T : Any> {

    /**
     * Returns all dependencies of the given object.
     *
     * @return A list of dependencies.
     */
    fun dependencies(): List<KClass<out T>> = emptyList()

}