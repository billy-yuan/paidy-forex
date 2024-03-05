package forex.thirdPartyApi

import sttp.model.Uri
import sttp.client4.quick._

object endpoint {
    def get(): Uri = {
        // if there are different endpoints for dev and prod, then list them here
        return uri"http://localhost:8000/rates"
    }
}