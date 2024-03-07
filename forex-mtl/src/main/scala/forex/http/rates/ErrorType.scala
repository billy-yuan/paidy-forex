package forex.http.rates

import cats.Show


sealed trait ErrorType

object ErrorType {
  case object InvalidRate extends ErrorType

  implicit val show: Show[ErrorType] = Show.show {
    case InvalidRate => "invalid_rate"
  }
}