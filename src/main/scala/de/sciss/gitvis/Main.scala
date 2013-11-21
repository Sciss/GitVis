package de.sciss.gitvis

import de.sciss.file._
import scala.swing.Swing

object Main extends App {
  val repos = Seq("ScalaOSC", "ScalaAudioFile", "ScalaCollider")
  val data  = repos zip repos.map { name =>
    println(s"Analysing '$name'...")
    Extract(devel / name)
  }

  Swing.onEDT {
    println("Plotting...")
    Show(data: _*)
    println("Done.")
  }
}
