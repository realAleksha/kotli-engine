package kotli.engine.provider.metadata

import kotli.engine.BaseFeatureProvider
import kotli.engine.FeatureProcessor
import kotli.engine.FeatureType
import kotli.engine.model.FeatureTypes
import kotli.engine.provider.metadata.ReadmeProcessor
import kotli.engine.provider.metadata.GitProcessor

object MetadataProvider : BaseFeatureProvider() {

    override fun getId(): String = "metadata"

    override fun getType(): FeatureType = FeatureTypes.Foundation

    override fun createProcessors(): List<FeatureProcessor> = listOf(
        ReadmeProcessor,
        GitProcessor
    )
}