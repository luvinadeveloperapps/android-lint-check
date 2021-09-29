package com.example.lint.checks

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.kotlin.utils.addToStdlib.cast
import org.jetbrains.uast.*
import java.util.*

class SKMIfNestDetector : Detector(), Detector.UastScanner {
    override fun getApplicableUastTypes()  = listOf<Class<out UElement>>(
        UIfExpression::class.java, UDoWhileExpression::class.java, UForExpression::class.java,
        UForEachExpression::class.java, USwitchExpression::class.java, UWhileExpression::class.java,

    )


    override fun createUastHandler(context: JavaContext): UElementHandler? {
        return IfNestHandler(context)
    }

    private inner class IfNestHandler (private val context: JavaContext) : UElementHandler() {
        var endIndex = -1
        var netCount = 0
        override fun visitForEachExpression(node: UForEachExpression) {
           detectNet(node)
        }

        override fun visitWhileExpression(node: UWhileExpression) {
           detectNet(node)
        }

        override fun visitSwitchExpression(node: USwitchExpression) {
            detectNet(node)
        }
        override fun visitIfExpression(node: UIfExpression) {
            if (node.isTernary) {
                return
            }
            detectNet(node)
        }

        override fun visitDoWhileExpression(node: UDoWhileExpression) {
            detectNet(node)
        }

        override fun visitForExpression(node: UForExpression) {
            detectNet(node)
        }


        private fun detectNet(node : UElement) {
            val location = context.getLocation(node)
            val curStartIndex = location.start!!.line
            val curEndIndex = location.end!!.line

            if (endIndex != -1 && curStartIndex > endIndex) {
                netCount = 0
                endIndex = -1
                return
            }
            netCount++
            endIndex = curEndIndex
            if (netCount > 3) {
                context.report(
                    ISSUE, node, context.getLocation(node), Constants.ISSUE_PREFIX
                            + "More than 3 if statement in net, count=$netCount"
                )
                endIndex = -1
                netCount = 0
            }
        }
    }

    companion object {
        val ISSUE: Issue = Issue.create(
            id =  Constants.IF_NET_ID,
            briefDescription =  Constants.IF_NET_BRIEF,
            explanation =  Constants.IF_NET_EXPLANATION,
            category = Category.CORRECTNESS,
            priority = 6,
            severity = Severity.WARNING,
            implementation = Implementation(
                SKMIfNestDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )
    }
}