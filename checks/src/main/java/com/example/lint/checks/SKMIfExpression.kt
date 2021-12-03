package com.example.lint.checks

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.*

class SKMIfExpression : Detector(), Detector.UastScanner {

    private lateinit var fileContent : List<String>
    override fun getApplicableUastTypes() = listOf<Class<out UElement>>(
        UIfExpression::class.java
    )


    override fun createUastHandler(context: JavaContext): UElementHandler? {
        fileContent = context.client.readFile(context.file).toString().split("\n")
        return IfExpressionHandler(context)
    }

    private inner class IfExpressionHandler(private val context: JavaContext) : UElementHandler() {
        override fun visitIfExpression(node: UIfExpression) {
            if (node.isTernary) {
                return
            }
            val startLine = context.getLocation(node.condition).start!!.line
            val endLine = context.getLocation(node.condition).end!!.line
            for (index in startLine..endLine) {
                val conditionStr = fileContent[index].toLowerCase().replace(" ","")
                if (conditionStr.contains("==true") || conditionStr.contains("==false") ||
                    conditionStr.contains( "boolean.true") || conditionStr.contains( "boolean.false")
                ) {
                    context.report(
                        ISSUE,
                        node,
                        context.getLocation(node),
                        Constants.ISSUE_PREFIX + "DUNT"
                    )
                }
            }
        }
    }

    companion object {
        val ISSUE: Issue = Issue.create(
            id = Constants.IF_NET_ID,
            briefDescription = Constants.IF_NET_BRIEF,
            explanation = Constants.IF_NET_EXPLANATION,
            category = Category.CORRECTNESS,
            priority = 6,
            severity = Severity.WARNING,
            implementation = Implementation(
                SKMIfExpression::class.java, Scope.JAVA_FILE_SCOPE
            )
        )
    }
}