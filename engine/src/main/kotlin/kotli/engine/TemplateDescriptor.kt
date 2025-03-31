package kotli.engine

import kotli.engine.model.Feature
import kotli.engine.model.Layer
import kotlin.reflect.KClass

/**
 * Provides all the required metadata to describe this processor.
 */
interface TemplateDescriptor : Dictionary, DependencyProvider<FeatureProcessor> {

    /**
     * Type of the layer this processor is responsible for.
     */
    fun getType(): LayerType

    /**
     * Returns the path to the template directory.
     *
     * Default implementation is configured to take the template structure relative
     * to the resources directory of the jar artifact storing the processor logic.
     */
    fun getTemplatePath(): String = "kotli/templates/${getId()}"

    /**
     * Returns a URL on a public repository with source codes or the official site of the given template.
     */
    fun getWebUrl(): String? = null

    /**
     * Returns the current version of the processor.
     * This version is updated automatically each time the new artifact is released.
     */
    fun getVersion(): String = javaClass.`package`.implementationVersion ?: "0.0.0"

    /**
     * Finds a processor by its id.
     */
    fun getFeatureProcessor(id: String): FeatureProcessor?

    /**
     * Finds a processor order by its id.
     */
    fun getFeatureProcessorOrder(id: String): Int

    /**
     * Finds a processor by its type.
     */
    fun getFeatureProcessor(type: KClass<out FeatureProcessor>): FeatureProcessor?

    /**
     * Finds a provider by its type.
     *
     * @throws IllegalStateException if a provider with the given type is not found.
     */
    fun getFeatureProvider(type: KClass<out FeatureProcessor>): FeatureProvider?

    /**
     * Returns all registered providers.
     */
    fun getFeatureProviders(): List<FeatureProvider>

    /**
     * Gets presets for the given template. Presets contain features that are predefined or suggested for
     * the template. These presets can help users quickly configure their project based on common use cases
     * or recommended configurations.
     *
     * @return A list of features representing the presets for the template context.
     */
    fun getPresets(): List<Layer> = emptyList()

    /**
     * Finds missed features that are required but not provided.
     *
     * @param features List of features to check against.
     * @param id Function to extract the id of each feature.
     * @param mapper Function to map a missed feature to the desired type.
     * @return List of missed features.
     */
    fun <T> getMissedFeatures(features: List<T>, id: (feature: T) -> String, mapper: (from: Feature) -> T): List<T> {
        val requiredProviders = getFeatureProviders()
            .filter { provider -> provider.isRequired() }
            .filter { provider -> provider.getProcessors().any { !it.isInternal() } }
        val layerProviders = features
            .mapNotNull { getFeatureProcessor(id(it)) }
            .mapNotNull { getFeatureProvider(it::class) }
        return requiredProviders
            .minus(layerProviders.toSet())
            .mapNotNull { provider -> provider.getProcessors().firstOrNull { !it.isInternal() } }
            .map { Feature(it.getId()) }
            .map(mapper)
    }

}