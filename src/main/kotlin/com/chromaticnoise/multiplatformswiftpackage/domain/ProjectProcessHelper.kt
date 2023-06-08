package com.chromaticnoise.multiplatformswiftpackage.domain

import org.gradle.api.GradleException
import org.gradle.api.Project

internal object ProjectProcessHelper {

    internal fun Project.isRootDirectoryGitRepository(): Boolean {
        val results = procRunWarnLog("git", "rev-parse", "--is-inside-work-tree")
        return results.isNotEmpty() && results.first() == "true"
    }

    internal fun Project.findRepoRoot(): String {
        val results = procRunWarnLog("git", "rev-parse", "--show-toplevel")
        return if (results.isEmpty()) {
            "."
        } else {
            val repoFile = file(results.first())
            projectDir.toPath().relativize(repoFile.toPath()).toString()
        }
    }

    private fun Project.procRunWarnLog(vararg params: String): List<String> {
        val output = mutableListOf<String>()
        try {
            logger.info("Project.procRunFailLog: ${params.joinToString(" ")}")
            procRun(*params) { line, _ -> output.add(line) }
        } catch (e: Exception) {
            output.forEach { logger.warn("warn: $it") }
            return emptyList()
        }
        return output
    }

    private fun procRun(vararg params: String, processLines: (String, Int) -> Unit) {
        val process = ProcessBuilder(*params)
            .redirectErrorStream(true)
            .start()
        val streamReader = process.inputStream.reader()
        val bufferedReader = streamReader.buffered()
        var lineCount = 1
        bufferedReader.forEachLine { line ->
            processLines(line, lineCount)
            lineCount++
        }
        bufferedReader.close()
        val returnValue = process.waitFor()
        if (returnValue != 0)
            throw GradleException("Process failed: ${params.joinToString(" ")}")
    }

}