package forex.services.rates

import cats.Applicative
import interpreters._
import forex.thirdPartyApi.OneFrameApiClient

object Interpreters {
  def dummy[F[_]: Applicative]: Algebra[F] = {
    val apiClient = new OneFrameApiClient()
    return new OneFrameDummy[F](apiClient)
  }
}
