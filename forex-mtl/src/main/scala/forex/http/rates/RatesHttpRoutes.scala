package forex.http
package rates

import cats.effect.Sync
import cats.syntax.flatMap._
import forex.programs.RatesProgram
import forex.programs.rates.{ Protocol => RatesProgramProtocol }
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
import cats.data.Validated.Invalid
import cats.data.Validated.Valid

class RatesHttpRoutes[F[_]: Sync](rates: RatesProgram[F]) extends Http4sDsl[F] {

  import Converters._, QueryParams._

  private[http] val prefixPath = "/rates"

  private val httpRoutes: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root :? FromQueryParam(maybeFrom) +& ToQueryParam(maybeTo) =>
      maybeFrom match {
        case Invalid(e) => BadRequest(Protocol.GetApiError(ErrorType.InvalidRate, e.head.sanitized))
        case Valid(from) => 
          maybeTo match {
            case Invalid(e) => BadRequest(Protocol.GetApiError(ErrorType.InvalidRate, e.head.sanitized))
            case Valid(to) =>
              rates
                .get(RatesProgramProtocol.GetRatesRequest(from, to))
                .flatMap { rate => 
                  rate match {
                    case Left(_) => 
                      InternalServerError(Protocol.GetApiError(
                        ErrorType.InterpreterError, "Error has occurred. Please try again later."
                      ))
                    case Right(value) => Ok(value.asGetApiResponse)
                  }
                }
          }
      }

  }

  val routes: HttpRoutes[F] = Router(
    prefixPath -> httpRoutes
  )

}
