package kotli.engine

import kotli.engine.model.LayerTypes
import kotlin.reflect.KClass

/**
 * The template processor is responsible for fulfilling the template context with data required
 * to obtain the output structure. It does not produce the output structure itself; instead,
 * it only prepares the data required for further usage by the [kotli.engine.TemplateGenerator].
 */
interface TemplateProcessor : TemplateDescriptor {

    /**
     * Processes the given context with all metadata required for further generation of output structure.
     *
     * @param context The template context to be prepared.
     */
    suspend fun process(context: TemplateContext)

    companion object {
        val App = object : BaseTemplateProcessor() {
            override fun getId(): String = "app"
            override fun getType(): LayerType = LayerTypes.App
            override fun createProviders(): List<FeatureProvider> = emptyList()
            override fun dependencies(): List<KClass<FeatureProcessor>> = emptyList()
        }
    }

}