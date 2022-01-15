package org.example.main;

import org.example.main.connectivity.GzipConnectionService;
import org.example.main.connectivity.StreamConnectionService;
import org.example.main.connectivity.WikiPagesBlacklistConnectionService;
import org.example.main.connectivity.WikipediaPageViewConnectService;
import org.example.main.filter.BlackListFilterService;
import org.example.main.generator.WikipediaPageReportGenerator;
import org.example.main.parser.ArgumentParser;
import org.example.main.storage.WikiPageViewFileOperator;
import org.example.main.util.StandardTimeUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public class Cli {

    public static void main(String[] args) throws IOException, InterruptedException {
        var usage = "Usage: <this> yyyy-MM-dd HH [yyyy-MM-dd HH]";
        if (args.length != 0 && args.length != 2 && args.length != 4) {
            System.err.println(usage);
            System.exit(1);
        }
        var argParser = new ArgumentParser(args);
        var timeIterator = argParser.parse();
        List<LocalDateTime> timeList = StandardTimeUtils.getTimeList(timeIterator);
        var connectionService = new StreamConnectionService();
        var blackListService = new WikiPagesBlacklistConnectionService(connectionService);
        var bls = new BlackListFilterService(blackListService);
        var wikiPageOperator = new WikipediaPageViewConnectService(new GzipConnectionService());
        var fileStorage = new WikiPageViewFileOperator();
        var worker = new WikipediaPageReportGenerator(bls, wikiPageOperator, fileStorage, 25);
        var operatedPaths = worker.execute(timeList);
        System.out.println("operatedPaths = " + operatedPaths);
        worker.exit();
    }
}
