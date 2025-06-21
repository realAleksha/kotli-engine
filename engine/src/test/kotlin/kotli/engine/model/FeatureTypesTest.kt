package kotli.engine.model

import org.junit.jupiter.api.Assertions
import kotlin.test.Test

class FeatureTypesTest {

    @Test
    fun `feature types have icon and title`() {
        FeatureTypes.entries.forEach { type ->
            Assertions.assertNotNull(type.getIcon(), type.getId())
            Assertions.assertNotNull(type.getTitle(), type.getId())
        }
    }
}