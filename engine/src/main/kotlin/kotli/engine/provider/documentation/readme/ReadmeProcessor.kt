package kotli.engine.provider.documentation.readme

import kotli.engine.BaseFeatureProcessor
import kotli.engine.FeatureProcessor
import kotli.engine.TemplateProcessor
import kotli.engine.TemplateState
import kotli.engine.template.rule.WriteText
import java.net.URLEncoder

object ReadmeProcessor : BaseFeatureProcessor() {

    override fun getId(): String = "documentation.readme"

    override fun doApply(state: TemplateState) {
        val readmeBuilder = StringBuilder()

        // about
        state.processor.getWebUrl()?.let {
            readmeBuilder.appendLine("# About")
            readmeBuilder.appendLine()
            readmeBuilder.appendLine("Layer template: $it")
            state.getRoot()
                .takeIf { root -> root.processor === TemplateProcessor.App }
                ?.layer?.id?.takeIf { id -> id.isNotEmpty() }
                ?.let {
                    readmeBuilder.appendLine()
                    readmeBuilder.appendLine("Project architecture: https://kotli.xyz/project/$it")
                }
        }

        // features
        val featuresBuilder = StringBuilder()
        val features = state.layer.features
        features
            .plus(state.processor.getMissedFeatures(features, { it.id }, { it }))
            .asSequence()
            .map { it.id }
            .mapNotNull(state.processor::getFeatureProcessor)
            .sortedBy { state.processor.getFeatureProcessorOrder(it.getId()) }
            .map { getDependencies(state, it) }
            .flatten()
            .distinct()
            .filter { !it.isInternal() || it.getConfiguration(state) != null }
            .filter { it.canApply(state) }
            .sortedBy { state.processor.getFeatureProvider(it::class)?.getType()?.getOrder() }
            .toList()
            .onEach { processor -> proceedFeature(featuresBuilder, state, processor) }
        if (featuresBuilder.isNotBlank()) {
            readmeBuilder.appendLine()
            readmeBuilder.appendLine("# Features")
            readmeBuilder.appendLine()
            readmeBuilder.appendLine("| Group | Feature | Overview | Configuration | Usage |")
            readmeBuilder.appendLine("|-------|---------|----------|---------------|-------|")
            readmeBuilder.append(featuresBuilder)
        }

        // description
        state.layer.description?.let { description ->
            readmeBuilder.appendLine()
            readmeBuilder.appendLine("# Description")
            readmeBuilder.appendLine()
            readmeBuilder.appendLine(description)
        }

        readmeBuilder
            .trim()
            .takeIf { it.isNotEmpty() }
            ?.let { state.onApplyRules("README.md", WriteText(it.toString())) }
    }

    private fun proceedFeature(
        textBuilder: StringBuilder,
        state: TemplateState,
        processor: FeatureProcessor
    ) {
        logger.debug("proceedFeature: {}", processor.getId())
        val provider = state.processor.getFeatureProvider(processor::class) ?: return
        val groupTitle = provider.getType().getTitle() ?: return
        val title = processor.getTitle(state) ?: return
        val usage = createUsage(state, groupTitle, title, processor)
        val overview = createOverview(state, groupTitle, title, processor)
        val configuration = createConfiguration(state, groupTitle, title, processor)
        textBuilder.appendLine("| $groupTitle | $title | ${overview.asLink()} | ${configuration.asLink()} | ${usage.asLink()} |")
    }

    private fun createOverview(
        state: TemplateState,
        group: String,
        title: String,
        processor: FeatureProcessor
    ): String {
        val docBuilder = StringBuilder()
        // about
        processor.getDescription(state)?.let {
            docBuilder.appendLine("# About")
            docBuilder.appendLine()
            docBuilder.appendLine(it)
        }
        // links
        StringBuilder()
            .also { links ->
                processor.getWebUrl(state)?.let { links.appendLine("- [Official Site]($it)") }
                processor.getIntegrationUrl(state)
                    ?.let { links.appendLine("- [Integration Guide]($it)") }
            }
            .trim()
            .takeIf { it.isNotBlank() }
            ?.let {
                docBuilder.appendLine()
                docBuilder.appendLine("# Links")
                docBuilder.appendLine()
                docBuilder.appendLine(it)
            }
        state.onApplyRules(
            "docs/${group}/${title}/overview.md",
            WriteText(docBuilder.trim().toString())
        )
        return "docs/${group.encoded()}/${title.encoded()}/overview.md"
    }

    private fun createConfiguration(
        state: TemplateState,
        group: String,
        title: String,
        processor: FeatureProcessor
    ): String? {
        val docBuilder = StringBuilder()
        processor.getConfiguration(state)?.let {
            docBuilder.appendLine("# Configuration")
            docBuilder.appendLine()
            docBuilder.appendLine(it)
        } ?: return null
        state.onApplyRules(
            "docs/${group}/${title}/configuration.md",
            WriteText(docBuilder.trim().toString())
        )
        return "docs/${group.encoded()}/${title.encoded()}/configuration.md"
    }

    private fun createUsage(
        state: TemplateState,
        group: String,
        title: String,
        processor: FeatureProcessor
    ): String? {
        val docBuilder = StringBuilder()
        processor.getUsage(state)?.let {
            docBuilder.appendLine("# Usage")
            docBuilder.appendLine()
            docBuilder.appendLine(it)
        } ?: return null
        state.onApplyRules(
            "docs/${group}/${title}/usage.md",
            WriteText(docBuilder.trim().toString())
        )
        return "docs/${group.encoded()}/${title.encoded()}/usage.md"
    }

    private fun String?.asLink() = this?.let { "[Link]($it)" } ?: "-"
    private fun String.encoded() = URLEncoder.encode(this, "UTF-8").replace("+", "%20")

}