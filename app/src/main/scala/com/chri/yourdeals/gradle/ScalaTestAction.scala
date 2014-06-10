package com.chri.yourdeals.gradle

import org.gradle.api.tasks.testing.Test
import org.gradle.process.internal.{JavaExecAction, DefaultJavaExecAction}
import org.gradle.api.{GradleException, Action}
import org.gradle.api.reporting.DirectoryReport
import org.gradle.logging.ConsoleRenderer
import org.gradle.api.internal.file.FileResolver
import scala.collection.parallel.mutable
import scala.reflect.io.File

/**
 * <p>Designed to replace the normal Test Action with a new JavaExecAction
 * launching the scalatest Runner.</p>
 * <p>Classpath, JVM Args and System Properties are propagated.</p>
 * <p>Tests are launched against the testClassesDir.</p>
 */
class ScalaTestAction extends Action[Test] {

  override def execute(t: Test) {
    def result = makeAction(t).execute()
    if (result.getExitValue != 0) {
      handleTestFailures(t)
    }
  }

  private def handleTestFailures(t: Test) {
    var message = "There were failing tests"
    val htmlReport = t.getReports.getHtml
    if (htmlReport.isEnabled) {
      message = message.concat(". See the report at: ").concat(url(htmlReport))
    } else {
      val junitXmlReport = t.getReports.getJunitXml
      if (junitXmlReport.isEnabled) {
        message = message.concat(". See the results at: ").concat(url(junitXmlReport))
      }
    }
    if (t.getIgnoreFailures) {
      t.getLogger.warn(message)
    }
    else {
      throw new GradleException(message)
    }
  }

  private def url(report: DirectoryReport): String = {
    new ConsoleRenderer().asClickableFileUrl(report.getEntryPoint)
  }


  def makeAction(t: Test): JavaExecAction = {
    val fileResolver = t.getEnvironment.get(classOf[FileResolver])
    JavaExecAction javaExecHandleBuilder = new DefaultJavaExecAction(fileResolver)
    javaExecHandleBuilder.setMain("org.scalatest.tools.Runner")
    javaExecHandleBuilder.setClasspath(t.getClasspath())
    javaExecHandleBuilder.setJvmArgs(t.getAllJvmArgs())
    javaExecHandleBuilder.setArgs(getArgs(t))
    javaExecHandleBuilder.setIgnoreExitValue(true)
    javaExecHandleBuilder
  }

  def getArgs(t: Test): Iterable[String] = {
    val args = new mutable.ArraySeq[String]()
    // this represents similar behaviour to the existing JUnit test action
    args.add("- oID ")

    if (t.getMaxParallelForks == 0) {
      args.add("- P ")
    } else {
      args.add("-P${t.maxParallelForks}")
    }

    args.add("- R ")
    args.add(t.getTestClassesDir.getAbsolutePath)

    if (t.getReports.getJunitXml.isEnabled) {
      args.add("- u ")
      args.add(t.getReports.getJunitXml.getEntryPoint.getAbsolutePath)
    }

    if (t.getReports.getHtml.isEnabled) {
      args.add("- h ")
      def dest = t.getReports.getHtml.getDestination
      dest.mkdirs()
      args.add(dest.getAbsolutePath)
    }

    args
  }
}