package com.example.lint.checks

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.*

class MethodNameDetector : Detector(), Detector.UastScanner{

    override fun getApplicableUastTypes()  = listOf<Class<out UElement>>(UMethod::class.java)


    override fun createUastHandler(context: JavaContext): UElementHandler? {
        return MethodHandler(context)
    }

    private inner class MethodHandler (private val context: JavaContext) : UElementHandler() {
        override fun visitMethod(node: UMethod) {
            val parameters = node.uastParameters
            for (parameter in parameters){
                val isLower = parameter.name[0].isLowerCase()
                if (isLower) {
                    continue
                }
                context.report(ISSUE, node, context.getLocation(node), Constants.ISSUE_PREFIX
                        + "First character of parameter '${parameter.name}' need lowercase")
            }
            if (node.isConstructor) {
                return
            }
            val isFirstCharacterLower = node.name[0].isLowerCase()
            if (isFirstCharacterLower){
                return
            }
            context.report(ISSUE, node, context.getLocation(node), Constants.ISSUE_PREFIX
                    + "First character of method '${node.name}' need lowercase")
        }
    }

    companion object {
        val ISSUE: Issue = Issue.create(
            id =  Constants.METHOD_NAME_ID,
            briefDescription =  Constants.METHOD_NAME_BRIEF,
            explanation =  Constants.METHOD_NAME_EXPLANATION,
            category = Category.CORRECTNESS,
            priority = 6,
            severity = Severity.WARNING,
            implementation = Implementation(
                MethodNameDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )
    }
}