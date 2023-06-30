package com.example.lint.checks

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.*

class SKMLogFormat : Detector(), Detector.UastScanner {
    override fun getApplicableUastTypes()  = listOf<Class<out UElement>>(UMethod::class.java)


    override fun createUastHandler(context: JavaContext): UElementHandler? {
        return MethodHandler(context)
    }

    private inner class MethodHandler (private val context: JavaContext) : UElementHandler() {
        val methodContent = context.client.readFile(context.file).toString().split("\n")
        override fun visitMethod(node: UMethod) {
            if (node.isConstructor) {
                return
            }
            val location = context.getLocation(node)
            for (index in location.start!!.line until location.end!!.line) {
                val content = methodContent[index].trim()
                if (content.contains("Log.")) {
                    context.report(ISSUE, node, context.getLocation(node), Constants.ISSUE_PREFIX + "Sai format ghi log"
                    )
                }
            }
        }
    }

    companion object {
        val ISSUE: Issue = Issue.create(
            id =  Constants.LOG_ID,
            briefDescription =  Constants.LOG_FORMAT,
            explanation =  Constants.LOG_FORMAT_EXPLANATION,
            category = Category.CORRECTNESS,
            priority = 6,
            severity = Severity.WARNING,
            implementation = Implementation(
                SKMLogFormat::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )
    }
}