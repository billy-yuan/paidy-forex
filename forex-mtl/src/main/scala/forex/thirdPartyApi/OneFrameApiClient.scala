package forex.thirdPartyApi

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec
import io.circe.parser.decode
import forex.domain.Rate
import forex.domain.Price
import forex.domain.Currency
import forex.domain.Timestamp
import scala.collection.immutable.HashMap
import sttp.client4.quick._
import sttp.model.Uri
import java.time.OffsetDateTime

final case class RateDto(from: String, to: String, bid: BigDecimal, ask: BigDecimal, price: BigDecimal, time_stamp: String)

object RateDto {
  implicit final val RateDtoCodec: Codec[RateDto] = deriveCodec
}

class OneFrameApiClient {
  def get(): Map[Rate.Pair, Rate] = {
	// Build URL and add currency pairs as query parameters 
	var url: Uri = endpoint.get()
	this.getCurrencyPairs()
	.foreach((pair) => {
		url = url.addParam("pair", pair.from.toString() + pair.to.toString())
	})

	// Send request
	val response = quickRequest.get(url).header("token","10dc303535874aeccc86a8251e6992f5").send()
		
	// Convert response body to RateDtos
	val rateDtos = decode[List[RateDto]](response.body) match {
		case Right(i) => i
		case Left(_) => List()
	}

	// Convert list to map
	var rateMap: Map[Rate.Pair, Rate] = new HashMap()
	for (rateDto <- rateDtos) {
		val ratePair = Rate.Pair(Currency.fromString(rateDto.from), Currency.fromString(rateDto.to))
		val rate = Rate(ratePair, Price(rateDto.price), Timestamp(OffsetDateTime.parse(rateDto.time_stamp)))
		rateMap = rateMap + (ratePair -> rate)
	}

	return rateMap
  }

  private def getCurrencyPairs(): Set[Rate.Pair] = {
	val allCurrencies: Set[(Currency, Int)] = Currency.cases().zipWithIndex
	val pairs: Set[Rate.Pair] = for {
  		(c1, i) <- allCurrencies
  		(c2, j) <- allCurrencies if i != j
	} yield (Rate.Pair(c1,c2))

	return pairs
  }
}