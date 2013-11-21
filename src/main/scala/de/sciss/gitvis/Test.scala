package de.sciss.gitvis

import de.sciss.file._
import java.lang.{ProcessBuilder => JProcessBuilder}
import java.util.Date
import scala.util.Try
import scalax.chart
import collection.immutable.{IndexedSeq => Vec}
import scala.swing.Swing
import de.sciss.numbers
import language.implicitConversions
import org.jfree.data.time.Day
import scala.Some
import org.jfree.chart.axis.LogarithmicAxis

object Test extends App {
  val devel = userHome / "Documents" / "devel"
  val cmd   = """git log --shortstat --pretty=%at""".split(' ')
  val repo  = devel / "ScalaOSC" // LucreData" // ScalaCollider"

  import sys.process._

  object Stats {
    private val Ex = """(\d+) files changed, (\d+) insertions\(\+\), (\d+) deletions\(-\)""".r

    def unapply(line: String): Option[(Int, Int, Int)] = line.trim match {
      case Ex(c, i, d) => (for {
          ci <- Try(c.toInt)
          ii <- Try(i.toInt)
          di <- Try(d.toInt)
        }
        yield (ci, ii, di)).toOption

      case _ => None
    }
  }
  object Line {
    def unapply(group: Seq[String]): Option[Line] = group match {
      case Date(t) +: Stats(c, i, d) +: _ => Some(Line(t, c, i, d))
      case _ => None
    }
  }
  case class Line(date: Date, changed: Int, insertions: Int, deletions: Int)

  object Date {
    def unapply(line: String): Option[Date] = Try(new Date(line.toLong * 1000)).toOption
  }

  val pb    = new JProcessBuilder(cmd: _*).directory(repo)
  // note: there are sometimes multiple lines with "at", perhaps from amendments or merges.
  // therefore, we just use `sliding`, effectively dropping every second combination,
  // but automatically skipping over those pathological cases.
  val lines: Vec[Line] = pb.lines.filter(_.trim.nonEmpty).toIndexedSeq.sliding(2).collect {
    case Line(ln) => ln
  }.toIndexedSeq

  // lines.foreach(println)

  import chart._
  import Charting._
  import numbers.Implicits._

  // implicit def dateToTime(d: Date): java.lang.Long = d.getTime

  // val xy    = new TimeSeries("changes")
  // xy.add(new XYDataItem())

  // implicit def dateToTime(d: Date): RegularTimePeriod = new Day(d)

  val data = (Vec.empty[(Day, Int)] /: lines) { (res, ln) =>
    val count = ln.insertions + ln.deletions
    val day   = new Day(ln.date)
    res match {
      case init :+ ((lastDay, lastCount)) if lastDay == day => init :+ (lastDay, lastCount + count)
      case _ => res :+ (day, count)
    }
  }

  val coll = data.toTimeSeriesCollection()

  Swing.onEDT {
    val chart = ChartFactories.XYBarChart(coll, legend = false)
    // val panel = chart.toPanel
    val plot = chart.plot
    plot.setRangeAxis(new LogarithmicAxis("LOC changed"))
    chart.printableLook()
    chart.show(title = s"${repo.name}")
  }
}
