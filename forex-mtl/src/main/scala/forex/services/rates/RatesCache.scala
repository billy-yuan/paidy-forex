package forex.services.rates

import forex.domain.Rate

trait RatesCache {
  def get(pair: Rate.Pair): Option[Rate]
}
