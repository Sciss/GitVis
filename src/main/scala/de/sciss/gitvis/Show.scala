package de.sciss.gitvis

import org.jfree.chart.axis.{DateAxis, LogarithmicAxis}
import scalax.chart._
import Charting._

object Show {
  def apply(dataSeq: (String, Vec[(Period, Int)])*): Unit = {
    val dt    = dataSeq.flatMap(_._2)
    val xMin  = dt.view.map(_._1.getStart).min
    val xMax  = dt.view.map(_._1.getEnd  ).max
    val yMin  = dt.view.map(_._2).min
    val yMax  = dt.view.map(_._2).max

    dataSeq.zipWithIndex.foreach { case ((name, data), idx) =>
      val coll = data.toTimeSeriesCollection()

      val chart = ChartFactories.XYBarChart(coll, legend = false)
      // val panel = chart.toPanel
      val plot = chart.plot
      val xAxis = plot.getDomainAxis.asInstanceOf[DateAxis]
      xAxis.setMinimumDate(xMin)
      xAxis.setMaximumDate(xMax)
      val yAxis = new LogarithmicAxis("") // ("LOC changed")
      plot.setRangeAxis(yAxis)
      yAxis.setLowerBound(yMin)
      yAxis.setUpperBound(yMax)
      chart.printableLook()

      val isLast = idx == dataSeq.size - 1

      if (!isLast) {
        xAxis.setAxisLineVisible(false)
        xAxis.setTickLabelsVisible(false)
      }

      chart.show2(w = 1600, h = 250 + (if (isLast) 16 else 0), title = name)
    }
  }
}
