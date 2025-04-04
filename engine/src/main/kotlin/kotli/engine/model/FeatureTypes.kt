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

    Metadata("metadata", -1),
    Essentials("essentials", -1),
    DevOps("devops", 1),
    DataFlow("dataflow", 2),
    UI("ui", 3),
    UserFlow("userflow", 4),
    Quality("quality", 5),
    Testing("testing", 6),
    Workflow("workflow", 7),
    Guides("guides", 8),
    Examples("examples", 9),
    Unspecified("unspecified", Int.MIN_VALUE)

    ;

    override fun getId(): String = id
    override fun getOrder(): Int = order
    override fun getIcon(): URL? = ResourceUtils.get(this, "feature_type_${id}.svg")
    override fun getTitle(): String? =
        ResourceUtils.getAsString(this, "feature_type_${id}_title.md")

    override fun getDescription(): String? =
        ResourceUtils.getAsString(this, "feature_type_${id}_description.md", true)

}