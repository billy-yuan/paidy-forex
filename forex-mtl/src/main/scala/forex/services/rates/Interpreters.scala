package forex.services.rates

import cats.Applicative
import interpreters._
import forex.thirdPartyApi.oneFrameApiClient

object Interpreters {
  def dummy[F[_]: Applicative]: Algebra[F] = new OneFrameDummy[F](oneFrameApiClient)
}
