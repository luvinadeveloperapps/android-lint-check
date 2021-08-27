/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.lint.checks

import com.android.resources.ResourceFolderType
import com.android.tools.lint.detector.api.*
import org.w3c.dom.Element


@Suppress("UnstableApiUsage")
class SKMShortEndTagDetector : ResourceXmlDetector(), XmlScanner {

    override fun visitElement(context: XmlContext, element: Element) {
        val nodeList = element.childNodes
        if(nodeList.length > 1) {
            return
        }
        val ctxContent = context.client.readFile(context.file).toString().split("\n")
        val index = context.getLocation(element).end!!.line
        val content = ctxContent[index]
        if (content.contains("/>")) {
            return
        }
        context.report(ISSUE, element, context.getLocation(element), Constants.ISSUE_PREFIX + "Need short end tag")
    }


    override fun getApplicableElements(): Collection<String>? {
        // Return the set of elements we want to analyze. The `visitElement` method
        // below will be called each time lint sees one of these elements in a
        // layout XML resource file.
        return XmlScannerConstants.ALL
    }

    override fun appliesTo(folderType: ResourceFolderType): Boolean {
        // Return true if we want to analyze resource files in the specified resource
        // folder type. In this case we only need to analyze layout resource files.
        return folderType == ResourceFolderType.LAYOUT
    }


    companion object {
        /**
         * Issue describing the problem and pointing to the detector
         * implementation.
         */
        val ISSUE: Issue = Issue.create(
            // ID: used in @SuppressLint warnings etc
            id =  Constants.CLOSE_TAG_ID,
            // Title -- shown in the IDE's preference dialog, as category headers in the
            // Analysis results window, etc
            briefDescription =  Constants.CLOSE_TAG_BRIEF,
            // Full explanation of the issue; you can use some markdown markup such as
            // `monospace`, *italic*, and **bold**.
            explanation =  Constants.CLOSE_TAG_EXPLANATION,
            category = Category.CORRECTNESS,
            priority = 6,
            severity = Severity.WARNING,
            implementation = Implementation(
                SKMShortEndTagDetector::class.java,
                Scope.RESOURCE_FILE_SCOPE
            )
        )
    }
}
