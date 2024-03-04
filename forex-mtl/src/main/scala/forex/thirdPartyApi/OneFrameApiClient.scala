package forex.thirdPartyApi

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec
import io.circe.parser.decode
import forex.domain.Rate
import forex.domain.Currency
import scala.collection.immutable.HashMap


final case class RateDto(from: String, to: String, bid: BigDecimal, ask: BigDecimal, price: BigDecimal, time_stamp: String)

object RateDto {
  implicit final val RateDtoCodec: Codec[RateDto] = deriveCodec
}

class OneFrameApiClient {
  def get(): Map[Rate.Pair, RateDto] = {
	// TODO: delete and connect to API. Debug use only
    val jsonResponse = """[{"from":"USD","to":"JPY","bid":0.6118225421857174,"ask":0.8243869101616611,"price":0.71810472617368925,"time_stamp":"2022-01-11T07:47:40.734Z"}]"""
		
	val rateDtos = decode[List[RateDto]](jsonResponse) match {
		case Right(i) => i
		case Left(_) => List()
	}

	// Convert list to map
	var rateMap: Map[Rate.Pair, RateDto] = new HashMap()
	for (rateDto <- rateDtos) {
		val ratePair = Rate.Pair(Currency.fromString(rateDto.from), Currency.fromString(rateDto.to))
		rateMap = rateMap + (ratePair -> rateDto)
	}

	return rateMap
  }
}