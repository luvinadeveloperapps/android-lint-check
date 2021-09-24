package com.example.lint.checks

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.kotlin.psi.psiUtil.startOffset
import org.jetbrains.uast.*

class SKMMethodLineDetector : Detector(), Detector.UastScanner {
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
            val lineMethod = location.end!!.line - location.start!!.line
            var startBlockCommentLine = 0
            var commentNumber = 0
            for (index in location.start!!.line until location.end!!.line) {
                val content = methodContent[index].trim()
                if (content.startsWith("//")) {
                    if (startBlockCommentLine == 0) { //prevent comment in comment
                        commentNumber += 1
                        continue
                    }
                }
                if (content.startsWith("/*")) {
                    if (startBlockCommentLine == 0) {
                        startBlockCommentLine = index
                        continue
                    }
                }
                if (content.endsWith("*/")) {
                    commentNumber += index - startBlockCommentLine + 1
                    startBlockCommentLine = 0
                    continue
                }
            }
            val methodLineWithoutComment = lineMethod - commentNumber
            if (methodLineWithoutComment > 30) {
                context.report(ISSUE, node, context.getLocation(node), Constants.ISSUE_PREFIX
                        + "method=$lineMethod, comment = $commentNumber."
                        + "The maximum number of lines for a method is 30 lines."
                )
            }
        }
    }

    companion object {
        val ISSUE: Issue = Issue.create(
            id =  Constants.METHOD_LINE_ID,
            briefDescription =  Constants.METHOD_LINE_BRIEF,
            explanation =  Constants.METHOD_LINE_EXPLANATION,
            category = Category.CORRECTNESS,
            priority = 6,
            severity = Severity.WARNING,
            implementation = Implementation(
                SKMMethodLineDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )
    }
}