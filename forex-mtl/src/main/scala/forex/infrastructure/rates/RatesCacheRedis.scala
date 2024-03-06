package forex.infrastructure.rates

import forex.services.rates.RatesCache
import forex.domain.Rate
import com.redis.RedisClient
import forex.domain.Currency
import forex.domain.Price
import forex.domain.Timestamp
import java.time.OffsetDateTime

class RatesCacheRedis(val redis: RedisClient) extends RatesCache {

  override def get(pair: Rate.Pair): Option[Rate] = {
    val redisResult = redis.hgetall(this.getKey(pair))

    val rate: Option[Rate] = redisResult match {
        case None => None
        case Some(value) => convertToRate(value)
    }

    return rate
  }


  private def convertToRate(map: Map[String, String]): Option[Rate] = {
      for {
            from <- map.get("from")
            to <- map.get("to")
            price <- map.get("price")
            timestamp <- map.get("timestamp")
        } yield {
                val pair = Rate.Pair(Currency.fromString(from), Currency.fromString(to))
                Rate(pair, Price(price.toDouble), Timestamp(OffsetDateTime.parse(timestamp)))
            }
  }

  private def getKey(pair: Rate.Pair): String = {
    return pair.from.toString() + pair.to.toString()
  } 
}
