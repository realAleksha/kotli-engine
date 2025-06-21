package kotli.engine

import kotli.engine.model.FeatureTypes
import kotli.engine.model.Layer
import kotli.engine.model.LayerTypes
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import java.util.*
import kotlin.test.Test

class BaseTemplateProcessorTest {

    private val templateProcessor by lazy {
        val provider1 = TestFeatureProvider(
            "1", true, listOf(
                TestFeatureProcessor("1"),
                TestFeatureProcessor("2"),
            )
        )
        val provider2 = TestFeatureProvider("2", true, emptyList())
        val provider3 = TestFeatureProvider(
            "3", false, listOf(
                TestFeatureProcessor("3"),
                TestFeatureProcessor("4"),
            )
        )
        TestTemplateProcessor(
            listOf(
                provider1,
                provider2,
                provider3
            )
        )
    }

    @Test
    fun `getPresets is filled with pre-defined preset`() {
        val presets = templateProcessor.getPresets()
        Assertions.assertEquals(1, presets.size)
        Assertions.assertEquals(1, presets.first().features.size)
        Assertions.assertEquals("1", presets.first().features.first().id)
    }

    @Test
    fun `process with missed features included`() {
        val context = DefaultTemplateContext(
            layer = Layer(
                id = UUID.randomUUID().toString(),
                processorId = templateProcessor.getId(),
                name = "",
                namespace = ""
            ),
            contextPath = "",
            registry = DefaultTemplateRegistry(templateProcessor),
            parent = null
        )
        runBlocking { templateProcessor.process(context) }
        Assertions.assertEquals(3, context.getAppliedFeatures().size)
        Assertions.assertTrue(context.getAppliedFeatures().any { it.id == "1" })
        Assertions.assertSame(context.getRoot(), context)
    }

    class TestTemplateProcessor(private val providers: List<FeatureProvider>) :
        BaseTemplateProcessor() {
        override fun createProviders(): List<FeatureProvider> = providers
        override fun getType(): LayerType = LayerTypes.App
        override fun getId(): String = "test"
    }

    class TestFeatureProvider(
        private val id: String,
        private val required: Boolean,
        private val processors: List<FeatureProcessor>
    ) : BaseFeatureProvider() {
        override fun getId(): String = id
        override fun isRequired(): Boolean = required
        override fun getType(): FeatureType = FeatureTypes.Foundation
        override fun createProcessors(): List<FeatureProcessor> = processors
    }

    class TestFeatureProcessor(
        private val id: String
    ) : BaseFeatureProcessor() {
        override fun getId(): String = id
    }

}