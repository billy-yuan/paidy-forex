package forex.services.rates.interpreters

import forex.services.rates.Algebra
import cats.Applicative
import forex.domain.Rate
import forex.services.rates.errors._
import forex.thirdPartyApi.oneFrame.OneFrameApiClient

class OneFrameDummy[F[_]: Applicative](val client: OneFrameApiClient) extends Algebra[F] {
  override def get(pair: Rate.Pair): F[Error Either Rate] = {
    // check if result is in cache
    // if not, make http request, save result to cache and return Rate
    val rates = this.client.get()
    val rateDto = rates.get(pair)
        
    val rate = rateDto match {
      case None => Left(Error.OneFrameLookupFailed("Rate not found"))
      case Some(r) => Right(r)
    }

    return Applicative[F].pure(rate)
  }

}
