package org.example.main;

import org.example.main.connectivity.GzipConnectionService;
import org.example.main.connectivity.StreamConnectionService;
import org.example.main.connectivity.WikiPagesBlacklistConnectionService;
import org.example.main.connectivity.WikipediaPageViewConnectService;
import org.example.main.filter.BlackListFilterService;
import org.example.main.generator.WikipediaPageReportGenerator;
import org.example.main.storage.WikiPageViewFileOperator;

import java.io.IOException;

public class Test {

    public static void main(String[] args) throws IOException {
        long curr = System.currentTimeMillis();
        var srv = new WikipediaPageViewConnectService(new GzipConnectionService());
        var connection = new WikiPagesBlacklistConnectionService(new StreamConnectionService());
        var blf = new BlackListFilterService(connection);
        var wikiFieAddr = "https://dumps.wikimedia.org/other/pageviews/2019/2019-02/projectviews-20190224-150000";
        var logEntries = srv.fetchLogEntries(wikiFieAddr);
        var fileStorage = new WikiPageViewFileOperator();
        var wpv = new WikipediaPageReportGenerator(blf, srv, fileStorage, 25);
        wpv.mapSortingLogEntries(logEntries)
                .subscribe(System.out::println);
        System.out.println("time took:" + (System.currentTimeMillis() - curr));
    }


}
