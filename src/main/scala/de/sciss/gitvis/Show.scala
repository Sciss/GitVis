package de.sciss.gitvis

import org.jfree.chart.axis.{DateAxis, LogarithmicAxis}
import scalax.chart._
import Charting._

object Show {
  def apply(dataSeq: (String, Vec[(Period, Int)])*): Unit = {
    val x     = dataSeq.flatMap(_._2)
    val xMin  = x.map(_._1.getStart).min
    val xMax  = x.map(_._1.getEnd  ).max

    dataSeq.foreach { case (name, data) =>
      val coll = data.toTimeSeriesCollection()

      val chart = ChartFactories.XYBarChart(coll, legend = false)
      // val panel = chart.toPanel
      val plot = chart.plot
      val xAxis = plot.getDomainAxis.asInstanceOf[DateAxis]
      xAxis.setMinimumDate(xMin)
      xAxis.setMaximumDate(xMax)
      plot.setRangeAxis(new LogarithmicAxis("LOC changed"))
      chart.printableLook()
      chart.show(title = s"$name")
    }
  }
}
