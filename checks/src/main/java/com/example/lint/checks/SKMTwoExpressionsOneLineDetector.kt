package com.example.lint.checks

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.*

class SKMTwoExpressionsOneLineDetector : Detector(), Detector.UastScanner {

    override fun getApplicableUastTypes()  = listOf(UImportStatement::class.java, UMethod::class.java)

    override fun createUastHandler(context: JavaContext): UElementHandler? {
        return ExpressionHandler(context)
    }

    private inner class ExpressionHandler (private val context: JavaContext) : UElementHandler() {
        val methodContent = context.client.readFile(context.file).toString().split("\n")
        var beforeImportLine = -1
        override fun visitImportStatement(node: UImportStatement) {
            var currentImportLine = context.getLocation(node).end!!.line
            if (beforeImportLine == currentImportLine) {
                context.report(
                    ISSUE, node, context.getLocation(node), Constants.ISSUE_PREFIX
                            + "Two import in one line"
                )
            }
            beforeImportLine = currentImportLine
        }

        override fun visitMethod(node: UMethod) {
            val location = context.getLocation(node)
            for (index in location.start!!.line until location.end!!.line) {
                val lineContent = methodContent[index].trim()
                if(!lineContent.contains(";")) {
                    continue
                }
                if (lineContent.startsWith("for")) {
                    continue
                }
                var splitArr =  lineContent.trim().split(";")
                if(checkComment(splitArr[1])) {
                    context.report(
                        ISSUE, node, context.getLocation(node), Constants.ISSUE_PREFIX
                                + "Two statement at line=${index + 1}"
                    )
                    continue
                }

            }
        }

        private fun checkComment(inputStr: String) : Boolean {
            if (inputStr.isNullOrEmpty()) {
                return false
            }
            var input =  inputStr
            while (true) {
                if (input.startsWith("//")) {
                    return false
                }
                if (input.startsWith("/*")) {
                    val splitComment = input.split("*/")
                    input = splitComment[1]
                    continue
                }
                return true
            }
        }
    }

    companion object {
        val ISSUE: Issue = Issue.create(
            id =  Constants.EXPRESSION_ID,

            briefDescription =  Constants.EXPRESSION_BRIEF,
            explanation =  Constants.EXPRESSION_EXPLANATION,
            category = Category.CORRECTNESS,
            priority = 6,
            severity = Severity.WARNING,
            implementation = Implementation(
                SKMTwoExpressionsOneLineDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )
    }
}