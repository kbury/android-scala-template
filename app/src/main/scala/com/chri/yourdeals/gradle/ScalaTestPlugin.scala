package com.chri.yourdeals.gradle

import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.scala.ScalaPlugin
import org.gradle.api.tasks.testing.Test
import org.gradle.api.{Project, Plugin, Action}
import scala.collection.mutable
import junit.framework.Test

/**
 * Applies the Java & Scala Plugins
 * Replaces all Java Test actions with a <code>ScalaTestAction</code>
 */
class ScalaTestPlugin extends Plugin[Project] {
  @Override
  override def apply(p: Project) {
    if (!p.plugins.hasPlugin(ScalaTestPlugin)) {
      p.plugins.add(this)
      p.plugins.apply(JavaPlugin)
      p.plugins.apply(ScalaPlugin)
      p.tasks.withType(Test) {
        test -> {
          test.setMaxParallelForks(Runtime.getRuntime.availableProcessors())
          var actions = mutable.ArraySeq[Action]()
          actions = new ScalaTestAction :: actions
          test.setActions(actions)
        }
      }
    }
  }
}