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
    }

}