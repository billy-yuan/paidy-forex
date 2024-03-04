package forex.http.client

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec
import io.circe.parser.decode


final case class RateDto(from: String, to: String, bid: Float, ask: Float, price: Float, time_stamp: String)

object RateDto {
  implicit final val RateDtoCodec: Codec[RateDto] = deriveCodec
}


class OneFrameClient {
  def get()  = {
		this.getResponse()
	}
	
  private def getResponse(): List[RateDto] = {
		// TODO: delete and connect to API. Debug use only
    val jsonResponse = """[{"from":"USD","to":"JPY","bid":0.61,"ask":0.82,"price":0.71,"time_stamp":"2019-01-01T00:00:00.000"}]"""
		
		val rateDtos = decode[List[RateDto]](jsonResponse) match {
			case Right(i) => i
			case Left(_) => List()
		}

		return rateDtos
  }
}