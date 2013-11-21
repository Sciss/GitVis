package de.sciss.gitvis

import de.sciss.file._
import java.lang.{ProcessBuilder => JProcessBuilder}
import java.util.Date
import scala.util.Try
import scalax.chart
import de.sciss.numbers
import language.implicitConversions

object Extract {
  val cmd   = """git log --shortstat --pretty=%at""".split(' ')
  // val repo  = devel / "SoundProcesses" // ScalaCollider" // LucreData" // ScalaCollider"

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

  def apply(dir: File): Vec[(Period, Int)] = {
    val pb    = new JProcessBuilder(cmd: _*).directory(dir)
    // note: there are sometimes multiple lines with "at", perhaps from amendments or merges.
    // therefore, we just use `sliding`, effectively dropping every second combination,
    // but automatically skipping over those pathological cases.
    val lines: Vec[Line] = pb.lines.filter(_.trim.nonEmpty).toIndexedSeq.sliding(2).collect {
      case Line(ln) => ln
    }.toIndexedSeq

    println(s"Date range: ${lines.head.date} ... ${lines.last.date}")

    // lines.foreach(println)

    import chart._
    import Charting._
    import numbers.Implicits._

    // type Period = Day
    // def Period(d: Date): Period = new Day(d)

    val data = (Vec.empty[(Period, Int)] /: lines) { (res, ln) =>
      val count  = ln.insertions + ln.deletions
      val period = Period(ln.date)
      res match {
        case init :+ ((lastPeriod, lastCount)) if lastPeriod == period => init :+ (lastPeriod, lastCount + count)
        case _ => res :+ (period, count)
      }
    }

    data
  }
}
