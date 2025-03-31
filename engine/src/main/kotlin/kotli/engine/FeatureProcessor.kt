package kotli.engine

/**
 * The Feature processor is responsible for the inclusion or exclusion of the feature it implements
 * in the generated template.
 *
 * A feature is any atomic integration, technical solution, or business flow
 * that can be added to a layer during its configuration in Kotli.
 *
 * Each feature should be self-descriptive, allowing it to be presented to the user with an icon, title, description, links,
 * and any other metadata required to understand its value and purpose.
 *
 * The primary advantage of a feature is to provide a ready-to-use solution with minimal configuration required
 * (zero configuration is the goal).
 *
 * By default, each blueprint template should be pre-configured to include all features.
 */
interface FeatureProcessor : DependencyProvider<FeatureProcessor>, FeatureDescriptor {

    /**
     * Applies the given processor to the generated template.
     *
     * @param context The current runtime template context with user-defined parameters.
     */
    fun apply(context: TemplateContext) = Unit

    /**
     * Removes the given processor from the generated template.
     *
     * @param context The current runtime template context with user-defined parameters.
     */
    fun remove(context: TemplateContext) = Unit

    /**
     * Checks if the given processor can be applied based on the state provided.
     *
     * If not, the processor will be skipped and removed as a result.
     *
     * By default, any added processor is applied.
     */
    fun canApply(state: TemplateState): Boolean = true
}