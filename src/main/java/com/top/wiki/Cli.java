package com.top.wiki;

import com.top.wiki.connectivity.*;
import com.top.wiki.filter.BlackListFilterService;
import com.top.wiki.generator.AsyncWikipediaPageViewReportGenerator;
import com.top.wiki.parser.StandardDateArgumentParser;
import com.top.wiki.storage.WikiPageViewFileOperator;
import com.top.wiki.util.CollectionUtils;
import com.top.wiki.util.TimeUtilsService;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;

public class Cli {

    public static void main(String[] args) throws IOException, InterruptedException {
        // start
        // 1- Gets the input from user, there should be 4 space-separated inputs inserted
        // if nothing provided the default would be used which is the current local system date time
        // which it will converts to utc datetime, because on wikipedia dates are all in utc format.
        var usage = "Usage: <this> yyyy-MM-dd HH [yyyy-MM-dd HH]";
        if (args.length != 0 && args.length != 2 && args.length != 4) {
            System.err.println(usage);
            System.exit(1);
        }
        // 2- Parses the arguments as set of inputs
        var argParser = new StandardDateArgumentParser(args);
        // 3- Retrieve the parse datetime list in utc format,
        // Passes it to parser, in tern it gives us the iterator for all of hour range
        // e.f between "2012-02-12 12" and "2012-02-12 15" there are 4 hours
        Iterator<LocalDateTime> timeIterator = argParser.parse();
        // 4- Converts the iterator to list for each traversal for other class methods
        var timeService = new TimeUtilsService(new UrlCheckConnectionService());
        List<LocalDateTime> timeList = timeService.getTimeList(timeIterator);
        // 5- Creates a connector to blacklist service and fetches blacklist from server
        var connectionService = new StandardInputStreamService();
        var blackListService = new WikiPagesBlacklistConnectionService(connectionService);
        var bls = new BlackListFilterService(blackListService);
        // 6- Crates a connector to wikipedia page view service
        var wikiPageOperator = new WikipediaPageViewConnectService(new GzipInputStreamService());
        var fileStorage = new WikiPageViewFileOperator();
        // 7- Creates page view report generator service which fetches items from server asynchronously
        // and saves them into storage -- here is local device
        var worker = new AsyncWikipediaPageViewReportGenerator(bls, wikiPageOperator, fileStorage, 25);
        CollectionUtils.requiresNonNull(timeList, "provided time list was empty");
        System.out.printf("times in range = %d.%n", timeList.size());
        // 8- Starts report generator service executing in background
        List<Path> operatedPaths = worker.execute(timeList);
        // 9- Prints out the files in which saved into disk as report
        System.out.println("operatedPaths = " + operatedPaths);
        // 10 - exists from worker report generator service
        worker.exit();
        // end
    }
}
