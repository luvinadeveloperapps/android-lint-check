package com.example.lint.checks

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.kotlin.psi.psiUtil.endOffset
import org.jetbrains.kotlin.psi.psiUtil.getTextWithLocation
import org.jetbrains.kotlin.psi.psiUtil.startOffset
import org.jetbrains.uast.*

class SKMMethodLineDetector : Detector(), Detector.UastScanner {
    override fun getApplicableUastTypes()  = listOf<Class<out UElement>>(UMethod::class.java)


    override fun createUastHandler(context: JavaContext): UElementHandler? {
        return MethodHandler(context)
    }

    private inner class MethodHandler (private val context: JavaContext) : UElementHandler() {
        override fun visitMethod(node: UMethod) {
            if (node.isConstructor) {
                return
            }
            val location = context.getLocation(node)
            val lineMethod = location.end!!.line - location.start!!.line
            val lineComment = node.comments
            val javaPsi = node.javaPsi.body?.context?.startOffset
            var a: String? = null
            if (lineComment != null) {
//                for (i in lineComment.parent.getTextWithLocation()) {
//                    val b = i.javaPsi?.context.
//                }
                a = lineComment.parent.getTextWithLocation()
            }

            val line = lineMethod
            if (lineMethod >= 30) {
                context.report(ISSUE, node, context.getLocation(node), Constants.ISSUE_PREFIX
                        + "${a} , ${lineComment}  Method has $lineMethod lines. The maximum number of lines for a " +
                        "method" +
                        " is 30 " +
                        "lines.")
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