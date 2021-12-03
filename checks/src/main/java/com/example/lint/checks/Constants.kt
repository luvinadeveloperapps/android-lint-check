package com.example.lint.checks

class Constants {
    companion object {
        const val ISSUE_PREFIX = "[***SKM***] "

        const val CLOSE_TAG_ID = "CloseTagID"
        const val CLOSE_TAG_BRIEF = "Need close tag"
        const val CLOSE_TAG_EXPLANATION = "XML elements without content need close tag"

        const val METHOD_NAME_ID = "MethodNameID"
        const val METHOD_NAME_BRIEF = "First character of method name need lowercase"
        const val METHOD_NAME_EXPLANATION = "First character of method name and variable name of method need lowercase"

        const val CLASS_NAME_ID = "ClassNameID"
        const val CLASS_NAME_BRIEF = "Need start with uppercase"
        const val CLASS_NAME_EXPLANATION = "Class name must be not null and start with uppercase"

        const val METHOD_LINE_ID = "MethodLineID"
        const val METHOD_LINE_BRIEF = "The number lines of code of the method <= 30"
        const val METHOD_LINE_EXPLANATION = "The number of lines of code of the method - exclude comments exceeds the limit (30)"

        const val EXPRESSION_ID = "ExpressionID"
        const val EXPRESSION_BRIEF = "Each line should contain one statement"
        const val EXPRESSION_EXPLANATION = "More than one statement in a line"

        const val IF_NET_ID = "IfNetID"
        const val IF_NET_BRIEF = "More than 3 if statements in a net"
        const val IF_NET_EXPLANATION = "If-net should maximum 3 if statements"

        const val METHOD_PARAMETER_ID = "MethodParameterID"
        const val METHOD_PARAMETER_BRIEF = "More than 5 input parameters in method"
        const val METHOD_PARAMETER_EXPLANATION = "Method should maximum 5 input parameters"
    }

}