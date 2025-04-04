package kotli.engine.provider.documentation

import kotli.engine.BaseFeatureProvider
import kotli.engine.FeatureProcessor
import kotli.engine.FeatureType
import kotli.engine.model.FeatureTypes
import kotli.engine.provider.documentation.readme.ReadmeProcessor

object DocumentationProvider : BaseFeatureProvider() {

    override fun getId(): String = "documentation"
    override fun getType(): FeatureType = FeatureTypes.Guides

    override fun createProcessors(): List<FeatureProcessor> = listOf(
        ReadmeProcessor
    )
}