package forex.infrastructure.cache

import com.github.blemale.scaffeine.Scaffeine
import scala.concurrent.duration._
import forex.services.rates.RatesCache
import forex.domain.Rate

class RatesCacheCaffiene extends RatesCache {
  override def get(pair: Rate.Pair): Option[Rate] = {
    // TODO: move cache creation outside
    val cache =
      Scaffeine()
        .recordStats()
        .expireAfterWrite(3.minute)
        .maximumSize(100)
        .build[String, Rate]()

    return cache.getIfPresent(this.getKey(pair))
  }

  private def getKey(pair: Rate.Pair): String = {
    val from = pair.from.toString()
    val to = pair.to.toString()
    return s"rate:pair:$from:$to"
  }
}
