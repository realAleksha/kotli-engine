package kotli.engine

import kotli.engine.model.Feature
import org.slf4j.LoggerFactory
import java.util.Deque
import java.util.LinkedList

/**
 * Basic implementation of any processor created.
 *
 * This class provides a common base for implementing feature processors and includes methods
 * to apply and remove the processor's functionality within the context of a template.
 */
abstract class BaseFeatureProcessor : FeatureProcessor {

    protected val logger by lazy { LoggerFactory.getLogger(this::class.java) }

    /**
     * Applies the feature processor within the given template context.
     * It includes handling dependencies and invoking the specific implementation provided
     * by the subclass.
     *
     * @param context The template context in which the processor is applied.
     */
    final override fun apply(context: TemplateContext) {
        if (canApply(context)) {
            getDependencies(context, this)
                .minus(this)
                .onEach { dependency -> dependency.apply(context) }
            context.onApplyFeature(getId()) { feature ->
                doApply(context, feature)
            }
        } else {
            logger.warn("skip processor due to wrong state :: {}", getId())
        }
    }

    /**
     * Removes the applied feature processor within the given template context.
     * It includes invoking the specific removal implementation provided by the subclass.
     *
     * @param context The template context in which the processor is removed.
     */
    override fun remove(context: TemplateContext) {
        context.onRemoveFeature(getId()) { feature ->
            doRemove(context, feature)
        }
    }

    /**
     * Retrieves a list of feature processors including the dependencies of the given processor.
     *
     * @param state The template state containing the processor and other relevant information.
     * @param processor The feature processor for which dependencies are retrieved.
     * @return A list of feature processors, including dependencies.
     */
    protected fun getDependencies(
        state: TemplateState,
        processor: FeatureProcessor
    ): List<FeatureProcessor> {
        logger.debug("getDependencies :: proceed :: {}", getId())
        val processors = LinkedList<FeatureProcessor>()
        fillDependencies(state, processor, processors)
        logger.debug("getDependencies :: found :: {} -> {}", getId(), processors.size)
        return processors.toList()
    }

    /**
     * Performs the specific application of the feature processor within the given template state and feature.
     *
     * @param state The template state in which the processor is applied.
     * @param feature The feature to which the processor is applied.
     */
    protected open fun doApply(state: TemplateState, feature: Feature) = doApply(state)

    /**
     * Performs the specific removal of the feature processor within the given template state and feature.
     *
     * @param state The template state in which the processor is removed.
     * @param feature The feature from which the processor is removed.
     */
    protected open fun doRemove(state: TemplateState, feature: Feature) = doRemove(state)

    /**
     * Performs the specific application of the feature processor within the given template state.
     *
     * @param state The template state in which the processor is applied.
     */
    protected open fun doApply(state: TemplateState) = Unit

    /**
     * Performs the specific removal of the feature processor within the given template state.
     *
     * @param state The template state in which the processor is removed.
     */
    protected open fun doRemove(state: TemplateState) = Unit

    private fun fillDependencies(
        state: TemplateState,
        processor: FeatureProcessor,
        processors: Deque<FeatureProcessor>,
        front: Boolean = false
    ) {
        if (processors.contains(processor)) return
        if (front) {
            processors.addFirst(processor)
        } else {
            processors.addLast(processor)
        }
        logger.debug(
            "fillDependencies :: fill :: {} -> {}, all={}",
            getId(),
            processor,
            processors.size
        )
        val templateProcessor = state.processor
        templateProcessor.getFeatureProvider(processor::class)?.dependencies()
            ?.mapNotNull(templateProcessor::getFeatureProcessor)
            ?.minus(this)
            ?.onEach { fillDependencies(state, it, processors, true) }
        processor.dependencies()
            .mapNotNull(templateProcessor::getFeatureProcessor)
            .minus(this)
            .onEach { fillDependencies(state, it, processors, true) }
    }

}