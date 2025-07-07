package kotli.engine.provider.metadata

import kotli.engine.TemplateState
import kotli.engine.template.rule.WriteText

object GitProcessor : MetadataProcessor() {

    override fun getId(): String = "metadata.git"

    override fun doApply(state: TemplateState) {
        state.onApplyRules(
            ".gitignore",
            WriteText(
                """
                local.properties
                
                out/
                build/
                dist/
                node_modules/
                
                .idea
                .gradle
                .kotlin
                .vscode
                .project
                .settings
                .DS_Store
            """.trimIndent()
            )
        )
    }
}