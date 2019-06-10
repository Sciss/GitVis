package de.sciss.gitvis

import de.sciss.file._
import org.rogach.scallop.{ScallopConf, ScallopOption => Opt}

import scala.swing.Swing

object Main {
  final case class Config(repositories: List[File])

  val devel: File = userHome / "Documents" / "devel"

  def main(args: Array[String]): Unit = {
    object p extends ScallopConf {
      printedName = "GitVis"

      val repos: Opt[List[File]] = trailArg(name = "repositories", required = false,
        default = Some(defaultRepositories.toList), descr = "Directories of git repositories")

      verify()

      val config = Config(repositories = repos())
    }

    run(p.config)
  }

  // val repos = Seq("ScalaOSC", "ScalaAudioFile", "ScalaCollider")
  val defaultRepoNames: Vec[String] = Vec(
    "LucreSTM", "LucreData",
    "LucreEvent", "LucreConfluent", "SoundProcesses_OLD", "SoundProcesses",
    "ScalaCollider", "Lucre", "FScape-next", "Wolkenpumpe", "Patterns", "Mellite" /*, "Thesis"*/
  )

  def defaultRepoDir(name: String): File =
    if (name == "Thesis") userHome / "Desktop" / "diss" / "Thesis" else devel / name

  def defaultRepositories: Vec[File] = defaultRepoNames.map(defaultRepoDir)

  def run(config: Config): Unit = {
    val numRepos = config.repositories.size

    val data: Seq[(String, Vec[(Period, Int)])] = config.repositories.zipWithIndex.map { case (dir, idx) =>
      val name = dir.name
      println(s"Analysing '$name' (${idx + 1}/$numRepos)...")
      name -> Extract(dir)
    }

    Swing.onEDT {
      println("Plotting...")
      Show()(data: _*)
      println("Done.")
    }
  }
}
