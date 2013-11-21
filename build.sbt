name            := "GitVis"

organization    := "de.sciss"

version         := "0.1.0-SNAPSHOT"

description     := "Visualizing some git histories"

homepage        := Some(url("https://github.com/Sciss/" + name.value))

licenses        := Seq("GPL v3+" -> url("http://www.gnu.org/licenses/gpl-3.0.txt"))

scalaVersion    := "2.10.3"

libraryDependencies ++= Seq(
  "com.github.wookietreiber" %% "scala-chart" % "0.3.0",
  "de.sciss" %% "fileutil" % "1.1.+",
  "de.sciss" %% "pdflitz"  % "1.0.+",
  "de.sciss" %% "numbers"  % "0.1.+"
)

retrieveManaged := true

scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature")
