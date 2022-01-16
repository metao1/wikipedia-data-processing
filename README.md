Data engineer - Wikipedia pageview data pipeline

Build a simple application that we can run to compute the top 25 pages on Wikipedia for each of the Wikipedia sub-domains:

Accept input parameters for the date and hour of data to analyze (default to the current date/hour - 24 hours if not passed, i.e. previous day/hour).
Download the page view counts from wikipedia for the given date/hour from https://dumps.wikimedia.org/other/pageviews/
> More information on the format can be found here: https://wikitech.wikimedia.org/wiki/Analytics/Data_Lake/Traffic/Pageviews

Eliminate any pages found in this blacklist: https://s3.amazonaws.com/dd-interview-data/data_engineer/wikipedia/blacklist_domains_and_pages
Compute the top 25 articles for the given day and hour by total pageviews for each unique domain in the remaining data.
> Multi-level subdomains (for example en.m) must be processed separately. This means that you have to separately compute top 25 articles for en, top 25 articles for en.m, top 25 articles for en.q, etc.

Save the results to a file, either locally or on S3, sorted by domain and number of pageviews for easy perusal.
Only run these steps if necessary; that is, not rerun if the work has already been done for the given day and hour.
Be capable of being run for a range of dates and hours; each hour within the range should have its own result file.


For your solution, explain:

What additional things would you want to operate this application in a production setting?

#### Since the number of parallel connections to wikipedia is limited to 3 at the same time, one cannot use this application
#### in production for heavy cluster process. We can increase number of instances by running each instance of this 
#### application on different servers, and then let them write back to S3 bucket.

What might change about your solution if this application needed to run automatically for each hour of the day?
### We can use cronejobs that can be easily added in k8s. We might need to pass arguments each time changed prior the last time.
How would you test this application?
#### Using current Unit tests, and e2e tests.
#### For testing e2e run in command line : 
### ./gradlew build && java -Xms2G -Xmx4G -jar build/libs/wikipedia-processor-1.0-SNAPSHOT.jar "2018-11-04" "04" "2018-11-04" "06" 

#### The file(s) should be created into page_view folder
#### For unit testing run in command line : ./gradlew test
How would you improve on this application design?
#### However I can pick another strategy to make this application even better, by splitting the services
#### in different small microservices.
#### We can creat a microservice fetches wikipages. We can run many  parallel instances to operate at the same time.
#### Then each microservice can store the files into S3 Bucket.
#### This can done easily by assigning each microservice a job to read from different time periods.
#### Each download microservice is responsible to fetch only one set unique files from wikipedia.
#### This saves the result into S3 bucket to not lose any file in future and to be able to re-process them if needed.
#### Distributing the files close to the parsers by using S3 helps to increase speed of operation.
#### Then we crate another microservice to parse each line of file and send the parsed packets to a AMQ or Kafka Broker.
#### This helps to separate the concerns by running as many instance as we want to fetch files from wikipeages without
#### being worry of java heap space and synchronization limitation.
#### Another microservice to aggregate all packets and group them and sort and calculate page view in desired time window.
#### and normalize table to process further using Kafka Streams.
#### Another microservice to aggregate all packets and store the result into S3 as reporting result.
