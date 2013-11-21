package de.sciss

import scalax.chart.Chart
import org.jfree.chart.plot.{CategoryPlot, XYPlot, Plot}
import java.awt.{Font, Color}
import org.jfree.chart.renderer.xy.{StandardXYBarPainter, XYBarRenderer}
import de.sciss.file._
import org.jfree.data.time.Week
import java.util.Date
import scala.swing.{Rectangle, Frame, Swing}
import scala.swing.event.WindowClosing
import de.sciss.pdflitz.Generate.QuickDraw

package object gitvis {
  private lazy val defaultFontFace = "Helvetica"  // "Arial"

  type Period = Week
  def Period(d: Date): Period = new Week(d)

  type Vec[+A]  = collection.immutable.IndexedSeq[A]
  val Vec       = collection.immutable.IndexedSeq

  val devel = userHome / "Documents" / "devel"

  implicit class ScissRichChart[P <: Plot](chart: Chart[P]) {
    /** Adjust the chart with a black-on-white color scheme and
      * fonts that come out properly in PDF export.
      */
    def printableLook(): Unit = {
      val plot = chart.plot

      val (xAxis, yAxis) = plot match {  // shitty Plot / Renderer interfaces do not have common super types
        case p: XYPlot       =>
          p.setBackgroundPaint           (Color.white    )
          p.setDomainGridlinePaint       (Color.lightGray)
          p.setRangeGridlinePaint        (Color.lightGray)
          p.getRenderer.setSeriesPaint(0, Color.darkGray )
          // undo the crappy "3D" look
          p.getRenderer match {
            case r: XYBarRenderer => r.setBarPainter(new StandardXYBarPainter())
            case _ =>
          }
          (p.getDomainAxis, p.getRangeAxis)
        case p: CategoryPlot =>
          p.setBackgroundPaint           (Color.white    )
          p.setDomainGridlinePaint       (Color.lightGray)
          p.setRangeGridlinePaint        (Color.lightGray)
          p.getRenderer.setSeriesPaint(0, Color.darkGray )
          // undo the crappy "3D" look
          p.getRenderer match {
            case r: XYBarRenderer => r.setBarPainter(new StandardXYBarPainter())
            case _ =>
          }
          (p.getDomainAxis, p.getRangeAxis)
      }

      //      val xAxis         = plot.getDomainAxis
      //      val yAxis         = plot.getRangeAxis
      val fnt1          = new Font(defaultFontFace, Font.BOLD , 14)
      val fnt2          = new Font(defaultFontFace, Font.PLAIN, 12)
      xAxis.setLabelFont(fnt1)
      xAxis.setTickLabelFont(fnt2)
      yAxis.setLabelFont(fnt1)
      yAxis.setTickLabelFont(fnt2)
    }

    def show2(w: Int, h: Int, title: String = ""): Unit = {
      val title0 = title
      import Swing._
      val p = chart.toPanel
      p.peer.asInstanceOf[org.jfree.chart.ChartPanel].setMouseWheelEnabled(true) // SO #19281374
      val f = new Frame {
        contents = p
          if (title0.nonEmpty) title = title0

        listenTo(this)
        reactions += {
          case WindowClosing(_) => sys.exit()
        }
      }

      val sz  = new Rectangle(0, 0, w, h)
      val draw = QuickDraw(w -> h) { g =>
        chart.peer.draw(g, sz)
      }

      new pdflitz.SaveAction(draw :: Nil).setupMenu(f)

      f.pack()
      f.centerOnScreen()
      f.open()
    }
  }
}