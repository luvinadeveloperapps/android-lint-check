package com.example.lint.checks

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UFile
import org.jetbrains.uast.UImportStatement
import org.jetbrains.uast.toUElement
import java.util.regex.Matcher
import java.util.regex.Pattern

class SKMTwoExpressionsOneLineDetector : Detector(), Detector.UastScanner {

    override fun getApplicableUastTypes() = listOf(UImportStatement::class.java, UFile::class.java)

    override fun createUastHandler(context: JavaContext): UElementHandler? {
        return ExpressionHandler(context)
    }

    private inner class ExpressionHandler(private val context: JavaContext) : UElementHandler() {
        val fileContent = context.client.readFile(context.file).toString()
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

        override fun visitFile(node: UFile) {
            val fileContentNoComment = removeComment(fileContent)
            val fileLines = fileContentNoComment.split("\n")
            for ((index, line) in fileLines.withIndex()) {
                if (line.startsWith("import")) {
                    continue
                }
                if (!line.contains(";")) {
                    continue
                }
                if (line.startsWith("for")) {
                    continue
                }

                var splitArr = line.trim().split(";")
                if (splitArr[1].isNullOrEmpty()) {
                    continue
                }

                val position = context.getNameLocation(node).start!!.line
                val location = Location.create(context.file, line, index, index + 1)
                context.report(
                    ISSUE, node, location, Constants.ISSUE_PREFIX
                            + "Two statement at line=${index + 1}"
                )
            }
        }

        private fun removeComment(inputStr: String): String {
            val regex = "(/\\*([^*]|[\\r\\n]|(\\*+([^*/]|[\\r\\n])))*\\*+/)|(//.*)"
            val p = Pattern.compile(regex)
            val m: Matcher = p.matcher(inputStr)
            return m.replaceAll("")
        }

    }


    companion object {
        val ISSUE: Issue = Issue.create(
            id = Constants.EXPRESSION_ID,

            briefDescription = Constants.EXPRESSION_BRIEF,
            explanation = Constants.EXPRESSION_EXPLANATION,
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