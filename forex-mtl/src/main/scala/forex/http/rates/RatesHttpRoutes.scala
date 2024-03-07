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
        case Invalid(_) => BadRequest("TODO: Invalid from JSON")
        case Valid(from) => 
          maybeTo match {
            case Invalid(_) => BadRequest("TODO: Invalid to JSON")
            case Valid(to) =>
              rates.get(RatesProgramProtocol.GetRatesRequest(from, to)).flatMap {
                value => 
                  value match {
                    case Left(_) => BadRequest("d")
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
