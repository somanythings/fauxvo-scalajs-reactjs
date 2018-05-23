package spatutorial.client.modules

import diode.data.Pot
import diode.react.{ModelProxy, ReactConnectProxy}
import japgolly.scalajs.react.ScalaComponent
import japgolly.scalajs.react.vdom.html_<^.<
import spatutorial.client.services._
import diode.react.ReactPot._
import diode.react._
import diode.data.Pot
import japgolly.scalajs.react._
import japgolly.scalajs.react.component.builder.Builder
import japgolly.scalajs.react.vdom.html_<^._
import spatutorial.client.components.Bootstrap._
import spatutorial.client.components.{Chart, ChartData, ChartDataset}
import spatutorial.client.components.Chart.ChartProps

import scala.util.{Failure, Success, Try}

object MeterRead {

  case class Props(proxy: ModelProxy[(Pot[Option[Int]], Pot[MeterHistory])])

  case class State(newReading: Int,
                   tooSmall: Boolean,
                   rcp: ReactConnectProxy[(Pot[Option[Int]], Pot[MeterHistory])])

  val component = ScalaComponent.builder[Props]("MeterRead")
    .initialStateFromProps((p) => {
      State(0, true, p.proxy.connect(identity))
    })
    .renderPS((t, p, s) =>
      <.div(
        <.h2("Meter Read"),
        p.proxy()._1.renderReady(v => <.p(v.fold(s"No latest reading yet")(latest => s"Your latest reading was: $latest"))),
        p.proxy()._1.renderReady(v => <.input(
          ^.placeholder := "Latest Reading",
          ^.value := s.newReading,
          ^.onChange ==> ((e) =>  onChange(t, s, p, e)))),
        <.button(
          "Submit",
          ^.disabled := s.tooSmall,
          ^.cls := "btn",
          ^.onClick ==> ((e) => {
          p.proxy.dispatchCB(SubmitMeterReading(s.newReading))
            .thenRun({
              p.proxy.dispatchNow(GetMeterHistory)
            }).void
        }))))
    .build

  private def onChange(t: Builder.Step3[Props, State, Unit]#$,
                       s: State,
                       p: Props,
                       e: _root_.japgolly.scalajs.react.ReactEventFromInput) = {

    val newval = Try(Integer.parseInt(e.target.value))
    val lastVal = p.proxy()._1.map(potLastRead => potLastRead.getOrElse(0)).getOrElse(0)
    newval match {
      case Success(v) =>
        t.setState(s.copy(tooSmall = v < lastVal, newReading = v))
      case Failure(f) =>
        println(s"oh dear! bad things happen to good values $f")
        t.setState(s.copy(newReading = 0))
    }
  }

  def chartMeterHistory(v: MeterHistory): VdomElement = {
    val dataList = v.history.map(_.doubleValue())
    val data = ChartData(dataList.map(_.toString), ChartDataset(dataList, "readings") :: Nil)
    Chart(ChartProps("readings", style = Chart.LineChart, data = data))
  }

  def apply(last: ModelProxy[(Pot[Option[Int]], Pot[MeterHistory])]) = component(Props(last))
}

object HistoryView {

  case class Props(proxy: ModelProxy[(Pot[Option[Int]], Pot[MeterHistory])])

  val component = ScalaComponent.builder[Props]("MeterRead")
    .renderPS((t, p, s) =>
      <.div(
        <.h2("Meter Read History"),
        p.proxy()._2.renderReady(v => {
          chartMeterHistory(v)
        })))
    .build

  def chartMeterHistory(v: MeterHistory): VdomElement = {
    val dataList = v.history.map(_.doubleValue())
    val data = ChartData(dataList.map(_.toString), ChartDataset(dataList, "readings") :: Nil)
    Chart(ChartProps("readings", style = Chart.LineChart, data = data))
  }

  def apply(last: ModelProxy[(Pot[Option[Int]], Pot[MeterHistory])]) = component(Props(last))
}
