package forex.services.rates

import forex.domain.Rate
import forex.infrastructure.ratesCacheCaffiene

trait RatesCache {
  def get(pair: Rate.Pair): Option[Rate]
}

object RatesCache {
    final val ratesCache = ratesCacheCaffiene
}