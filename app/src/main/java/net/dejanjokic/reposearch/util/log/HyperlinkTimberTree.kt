package net.dejanjokic.reposearch.util.log

import timber.log.Timber

class HyperlinkTimberTree : Timber.DebugTree() {

    override fun createStackElementTag(element: StackTraceElement): String? =
        "(${element.fileName}:${element.lineNumber})#${element.methodName}"
}