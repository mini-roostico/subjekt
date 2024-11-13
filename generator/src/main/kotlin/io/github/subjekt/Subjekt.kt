package io.github.subjekt

import io.github.subjekt.files.Reader.suiteFromResource
import io.github.subjekt.files.Reader.suiteFromYaml
import io.github.subjekt.resolved.ResolvedSubject
import java.io.File
import java.io.FileNotFoundException

object Subjekt {

  fun List<Set<ResolvedSubject>>.asCode(): List<Set<String>> =
    map { resolvedSubjects -> resolvedSubjects.map { it.code }.toSet() }

  fun file(path: File): SubjektSuite = SubjektSuite(suiteFromYaml(path) ?: throw FileNotFoundException(path.name))
  fun text(yaml: String): SubjektSuite =
    SubjektSuite(suiteFromYaml(yaml) ?: throw IllegalArgumentException("Parsing failed with $yaml"))

  fun resource(path: String): SubjektSuite =
    SubjektSuite(suiteFromResource(path) ?: throw FileNotFoundException(path))
}
