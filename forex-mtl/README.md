## Problem Overview

### User Story
1. As a Paidy engineer, I want to be able to easily get foreign exchange rates for the services I am developing.

### Constraints
1. The rate should not be older than 5 minutes.
2. The One-Frame service supports a maximum of 1000 requests per day for any given authentication token.
3. The service should support at least 10,000 successful requests per day with 1 API token.

## Proposed Solution
Because the number of the requests the Forex service must support is greater than the maximum daily calls allowed by the One-Frame API, we cannot simply forward every request from the service to the One-Frame API.

Instead, we can do the following:

* Send a request to the One-Frame API with all pair combinations as query parameters.
* Store the rates from the One-Frame API in a cache (e.g. Redis) and fulfill Forex requests by querying this storage. 
* Whenever the results go stale, get the latest rates from the One-Frame API and update the cache.

Paidy engineers can access this service via `GET /forex-rates`.

### High Level Design
TODO

### Querying and storing the data from the One-Frame API
To minimize rounding errors that occurs from taking inverses of currency exchange rates (i.e. if 1 USD = 150.3 yen, the inverse is 1 yen = 1/150.3), we want to include all **permutations** of rate pairs in the query parameters. In other words:
```
/rates?pair=USDJPY&pair=JPYUSD
```

### Why cache and why Caffiene?
The reasons to use a cache are the following:
* **Extremely fast reads.** If a cache is well designed and has a high hit rate, it can serve a request in milliseconds, making it able to handle a large volume of requests easily.
* **Easy to expire data.** Since we want to serve the latest rates, removing outdated rates keeps
* **Not relational data.** The rates don't to have relational integrity with other entities, so a relational database isn't required.

As for why Caffiene was chosen, it was mostly due to how easy it is to develop locally with it. In addition, the amount of data we need to store is very low (72 pairs of rates) so an in-memory cache is fine.

### Calculating cache expiration time
The **maximum number of requests** we can send to the One-Frame API is as follows:
```
1,000 requests per 24 hours
41.6 requests per hour
3.47 requests per 5 minutes
1 request every 1.44 minutes
```

As long as there is **between 1.44 minutes and 5 minutes** between every One-Frame API request, we will not hit the daily limit of 1,000 requests. In other words, we can return rates than are at least 1.44 minutes and 5 minutes old. 

Now, we can theoretically set the cache expiration time to 5 minutes. However, due to network latency, the returned rate might be a few seconds old by the time the response is received.

Therefore, we will use **3 minutes** as the cache expiration.

## Implementation

### Edge cases

#### Number of supported currencies increases

#### Database goes down

#### Maximum daily requests per API token decreases

## Improvements

### Make service instansiations cleaner with dependency injection

### Add logging
To help with debugging, a logger that sends an error log with a stack trace can be implemented whenever an exception occurs.

## Limitations 
### Caffiene is in-memory and cannot be used by multiple instances
If more than one instance of this service is spun up, then each instance will have its own separate cache. Because the caches do not share data, they each need to fetch data using the OneFrame API client once its data expires. This will increase the number of API requests.

To prevent this, a centralized cache can be used (like Redis) by all instances.

### Historical rates cannot be queried
Because rates are stored in a cache and expire after 3 minutes, old rates cannot be queried. If this becomes a requirement in the future, then we can add this feature and save the rates to a database in addition to storing the data to a cache.