package de.sciss.gitvis

import de.sciss.file._
import scala.swing.Swing

object Main extends App {
  // val repos = Seq("ScalaOSC", "ScalaAudioFile", "ScalaCollider")
  val repos = Vec(
    "LucreSTM", "LucreData",
    "LucreEvent", "LucreConfluent", "SoundProcesses_OLD", "SoundProcesses",
    "ScalaCollider", "Mellite", "Thesis"
  )

  val data  = repos zip repos.zipWithIndex.map { case (name, idx) =>
    println(s"Analysing '$name' (${idx + 1}/${repos.size})...")
    val repo = if (name == "Thesis") userHome / "Desktop" / "diss" / "Thesis" else devel / name
    Extract(repo)
  }

  Swing.onEDT {
    println("Plotting...")
    Show(data: _*)
    println("Done.")
  }
}
