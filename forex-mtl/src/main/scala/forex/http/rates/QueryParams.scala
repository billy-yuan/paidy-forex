package forex.http.rates

import forex.domain.Currency
import org.http4s.QueryParamDecoder
import org.http4s.dsl.impl.ValidatingQueryParamDecoderMatcher
import org.http4s.ParseFailure

object QueryParams {

  case class Params(from: Option[Currency], to: Option[Currency]) {}
  case class ValidatedParams(from: Currency, to: Currency) {}

  def validateParams(p: Params): Either[ParseFailure,ValidatedParams] = {
    for {
      from <- p.from.toRight(ParseFailure("failed to parse from", ""))
      to <- p.to.toRight(ParseFailure("failed to parse to", ""))
    } yield new ValidatedParams(from, to)
  }

  private[http] implicit val currencyQueryParam: QueryParamDecoder[Currency] =
    QueryParamDecoder[String].emap((q) => {
      Currency.tryFromString(q) match {
        case None =>  Left(ParseFailure(s"$q is not a valid currency.", s"$q is not a valid currency."))
        case Some(value) => Right(value)
      }
    }
  )

  object FromQueryParam extends ValidatingQueryParamDecoderMatcher[Currency]("from")
  object ToQueryParam extends ValidatingQueryParamDecoderMatcher[Currency]("to")

}
