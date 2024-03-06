package forex

import forex.infrastructure.cache.RatesCacheCaffiene

package object infrastructure {
  final val ratesCacheCaffiene = new RatesCacheCaffiene()
}
