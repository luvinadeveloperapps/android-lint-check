package com.example.lint.checks

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.*

class SKMIfNestDetector : Detector(), Detector.UastScanner {
    override fun getApplicableUastTypes() = listOf<Class<out UElement>>(
        UIfExpression::class.java, UDoWhileExpression::class.java, UForExpression::class.java,
        UForEachExpression::class.java, USwitchExpression::class.java, UWhileExpression::class.java,

        )


    override fun createUastHandler(context: JavaContext): UElementHandler? {
        return IfNestHandler(context)
    }

    private inner class IfNestHandler(private val context: JavaContext) : UElementHandler() {
        var listNet = ArrayList<Net>()
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


        private fun detectNet(node: UElement) {
            val location = context.getLocation(node)
            val curStartIndex = location.start!!.line + 1
            val curEndIndex = location.end!!.line + 1

            val currentNet = Net(curStartIndex, curEndIndex)
            if (listNet.size == 0) {
                listNet.add(currentNet)
                return
            }

            val netBefore = listNet[listNet.size - 1]
            val netFirst = listNet[0]
            if (currentNet.startNet > netFirst.endNet) {
                listNet.clear()
                listNet.add(currentNet)
                return
            }

            if (currentNet.endNet < netBefore.endNet) {
                listNet.add(currentNet)

                if (listNet.size == 4) {
                    val strListNetStart = StringBuilder()
                    for (net in listNet) {
                        strListNetStart.append(net.startNet.toString() + " ")
                    }
                    context.report(
                        ISSUE,
                        node,
                        context.getLocation(node),
                        Constants.ISSUE_PREFIX + "More than 3 if statement in net, net start $strListNetStart"
                    )
                }
                return
            }

            if (currentNet.startNet > netBefore.endNet) {
                val listNetNew = ArrayList<Net>()
                for (net in listNet) {
                    if (currentNet.startNet < net.endNet) {
                        listNetNew.add(net)
                    }
                }
                listNet = listNetNew
                listNet.add(currentNet)
                if (listNet.size == 4) {
                    val strListNetStart = StringBuilder()
                    for (net in listNet) {
                        strListNetStart.append(net.startNet.toString() + " ")
                    }
                    context.report(
                        ISSUE,
                        node,
                        context.getLocation(node),
                        Constants.ISSUE_PREFIX + "More than 3 if statement in net, net start $strListNetStart"
                    )
                }
                return
            }
        }
    }

    data class Net(val startNet: Int, val endNet: Int)

    companion object {
        val ISSUE: Issue = Issue.create(
            id = Constants.IF_NET_ID,
            briefDescription = Constants.IF_NET_BRIEF,
            explanation = Constants.IF_NET_EXPLANATION,
            category = Category.CORRECTNESS,
            priority = 6,
            severity = Severity.WARNING,
            implementation = Implementation(
                SKMIfNestDetector::class.java, Scope.JAVA_FILE_SCOPE
            )
        )
    }
}