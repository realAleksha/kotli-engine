package kotli.engine.model

import kotli.engine.FeatureType
import kotli.engine.utils.ResourceUtils
import java.net.URL

/**
 * Represents a predefined set of feature types.
 *
 * @param id The unique identifier of the feature type.
 * @param order The order in which the feature type should be processed.
 */
enum class FeatureTypes(
    private val id: String,
    private val order: Int
) : FeatureType {

    Foundation("foundation", Int.MIN_VALUE),
    Platform("platform", 0),
    DevTools("dev", 1),
    DataLayer("data", 2),
    UiLayer("ui", 3),
    Feature("feature", 4),
    Unspecified("unspecified", Int.MAX_VALUE),
    ;

    override fun getId(): String = id
    override fun getOrder(): Int = order
    override fun getIcon(): URL? = ResourceUtils.get(this, "feature_type_${id}.svg")
    override fun getTitle(): String? = ResourceUtils.getAsString(this, "feature_type_${id}_title.md")
    override fun getDescription(): String? = ResourceUtils.getAsString(this, "feature_type_${id}_description.md", true)
}