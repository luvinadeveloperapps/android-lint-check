package com.example.lint.checks

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.*

class SKMMethod5Parameters : Detector(), Detector.UastScanner {
    override fun getApplicableUastTypes() = listOf<Class<out UElement>>(
        UMethod::class.java
        )


    override fun createUastHandler(context: JavaContext): UElementHandler? {
        return MethodHandler(context)
    }

    private inner class MethodHandler(private val context: JavaContext) : UElementHandler() {
        override fun visitMethod(node: UMethod) {
            val parameters = node.uastParameters

            if (parameters.size > 5) {
                context.report(
                    SKMIfNestDetector.ISSUE,
                    node,
                    context.getLocation(node),
                    Constants.ISSUE_PREFIX + "More than 5 input parameters in method"
                )
            }


        }
    }
    companion object {
        val ISSUE: Issue = Issue.create(
            id = Constants.METHOD_PARAMETER_ID,
            briefDescription = Constants.METHOD_PARAMETER_BRIEF,
            explanation = Constants.METHOD_PARAMETER_EXPLANATION,
            category = Category.CORRECTNESS,
            priority = 6,
            severity = Severity.WARNING,
            implementation = Implementation(
                SKMMethod5Parameters::class.java, Scope.JAVA_FILE_SCOPE
            )
        )
    }
}