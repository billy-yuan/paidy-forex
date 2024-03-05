package forex.thirdPartyApi.oneFrame

object errors {

  object Error {
    final case class JsonDecodingError(msg: String) extends Error
  }

}
