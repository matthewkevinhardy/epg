# EPG Webservice
Imports channel and EPG data from XML files into an ElasticSearch index.

EPG data available from [epgshare01.online](https://epgshare01.online)

## Batch Import
XML data downloaded and imported using Spring Batch:

`java -jar batch/target/batch-0.0.1-SNAPSHOT.jar`

## EPG Search API
Data from ElasticSearch made available via a webservice hosted within Spring Boot Starter Web:

`java -jar search/target/search-0.0.1-SNAPSHOT.jar`

### Channel endpoints
- [https://mkhardy.uk.to/epg/v1/channel](https://mkhardy.uk.to/epg/v1/channel)
- [https://mkhardy.uk.to/epg/v1/channel/bbc](https://mkhardy.uk.to/epg/v1/channel/bbc)

### Programme endpoints
- [https://mkhardy.uk.to/epg/v1/programme/now?channel=BBC.One.Lon.HD.uk](https://mkhardy.uk.to/epg/v1/programme/now?channel=BBC.One.Lon.HD.uk)
- [https://mkhardy.uk.to/epg/v1/programme/nextHour?channerl=ITV1.HD.uk](https://mkhardy.uk.to/epg/v1/programme/nextHour?channerl=ITV1.HD.uk)
- [https://mkhardy.uk.to/epg/v1/programme/nowAndNext?channel=Channel.5.HD.uk](https://mkhardy.uk.to/epg/v1/programme/nowAndNext?channel=Channel.5.HD.uk)
