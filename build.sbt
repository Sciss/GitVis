name            := "GitVis"
organization    := "de.sciss"
version         := "0.1.0-SNAPSHOT"
description     := "Visualizing some git histories"
homepage        := Some(url(s"https://git.iem.at/sciss/${name.value}"))
licenses        := Seq("GPL v3+" -> url("http://www.gnu.org/licenses/gpl-3.0.txt"))
scalaVersion    := "2.12.8"

libraryDependencies ++= Seq(
  "de.sciss"    %% "scala-chart" % "0.7.1",
  "de.sciss"    %% "fileutil"    % "1.1.3",
  "de.sciss"    %% "pdflitz"     % "1.4.1",
  "de.sciss"    %% "numbers"     % "0.2.0",
  "org.rogach"  %% "scallop"     % "3.3.0"
)

scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature", "-Xlint", "-Xsource:2.13")
