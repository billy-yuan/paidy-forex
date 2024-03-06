package forex.services.rates.interpreters

import forex.services.rates.Algebra
import cats.Applicative
import forex.domain.Rate
import forex.services.rates.errors._
import forex.thirdPartyApi.oneFrame.errors._
import forex.services.rates.errors.Error.OneFrameLookupFailed
import forex.thirdPartyApi.oneFrame.OneFrameApiClient

class OneFrameDummy[F[_]: Applicative](oneFrameApiClient: OneFrameApiClient) extends Algebra[F] {
  override def get(pair: Rate.Pair): F[Error Either Rate] = {
    // check if result is in cache
    // if not, make http request, save result to cache and return Rate
    val ratesFromClient: Either[OneFrameApiClientError, Map[Rate.Pair, Rate]] = oneFrameApiClient.getAll()

    val rateFromMap: Either[Error, Option[Rate]] = ratesFromClient match {
      case Left(_) => Left(OneFrameLookupFailed("Error with getting response from OneFrameApiClient")) 
      case Right(r) => 
        Right(r.get(pair))
    }

    val rate: Either[Error, Rate] = rateFromMap match {
      case Left(e) => Left(e)
      case Right(value) => 

        if (value.isEmpty) 
          Left(OneFrameLookupFailed("Pair does not exist"))
          Right(value.get)
    }

    return Applicative[F].pure(rate)
  }

}
