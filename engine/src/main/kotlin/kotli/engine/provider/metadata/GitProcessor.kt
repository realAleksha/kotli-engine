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
                
                .idea
                .gradle
                .vscode
                .project
                .settings
                .DS_Store
            """.trimIndent()
            )
        )
    }
}