package com.example.lint.checks

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.kotlin.js.translate.utils.finalElement
import org.jetbrains.uast.*

class SKMClassNameDetector : Detector(), Detector.UastScanner{

    override fun getApplicableUastTypes()  = listOf<Class<out UClass>>(UClass::class.java)


    override fun createUastHandler(context: JavaContext): UElementHandler? {
        return ClassHandler(context)
    }

    private inner class ClassHandler (private val context: JavaContext) : UElementHandler() {
        override fun visitClass(node: UClass) {
            val className = node.name
            if (className.isNullOrEmpty()) {
                context.report(
                    ISSUE, node, context.getLocation(node.toUElement()!!),
                    Constants.ISSUE_PREFIX + "Class must be not null"
                )
            } else {
                val isFirstCharacterUpper = className[0].isUpperCase()
                if (isFirstCharacterUpper){
                    return
                }
                context.report(ISSUE, node, context.getLocation(node.toUElement()!!),
                    Constants.ISSUE_PREFIX + "First character of class '${node.name}' need uppercase")
            }

        }
    }

    companion object {
        val ISSUE: Issue = Issue.create(
            id =  Constants.CLASS_NAME_ID,
            briefDescription =  Constants.CLASS_NAME_BRIEF,
            explanation =  Constants.CLASS_NAME_EXPLANATION,
            category = Category.CORRECTNESS,
            priority = 10,
            severity = Severity.WARNING,
            implementation = Implementation(
                SKMClassNameDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )
    }

}