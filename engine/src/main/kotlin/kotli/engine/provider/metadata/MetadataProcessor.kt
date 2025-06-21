package kotli.engine.provider.metadata

import kotli.engine.BaseFeatureProcessor

abstract class MetadataProcessor : BaseFeatureProcessor() {

    final override fun isInternal(): Boolean = true
}