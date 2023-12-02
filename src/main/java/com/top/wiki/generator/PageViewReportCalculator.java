package com.top.wiki.generator;

import com.top.wiki.model.LogEntry;
import com.top.wiki.model.PageViewItem;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

@Value
@Slf4j
public class PageViewReportCalculator {

    Map<String, PageViewItem> map = new HashMap<>();

    /**
     * @param logEntries as a list of log entries to be calculated
     */
    public void calculate(List<LogEntry> logEntries) {
        int counter = 0;
        for (LogEntry le : logEntries) {
            var dc = le.getDomainCode();
            // checks if the domain code already presents in map
            if (map.containsKey(dc)) {
                var pageViewItem = map.get(dc);
                var minPageViewLe = pageViewItem.minLogEntryPageView();
                if (minPageViewLe == null) {
                    return;
                }
                if (le.getCountViews() >= minPageViewLe.getCountViews()) {
                    pageViewItem.addNewItem(le);
                    pageViewItem.updateMaxEntryCount();
                    map.put(dc, pageViewItem);
                }
            } else {
                //if domain code missed just create new i
                var set = new TreeSet<LogEntry>();
                set.add(le);
                var nle = new PageViewItem(1, set);
                map.put(dc, nle);
            }
            if (counter++ % 1000000 == 0) {
                log.info("Processed {} entries", counter);
            }
        }
    }

    public Map<String, PageViewItem> getMap() {
        return map;
    }
}
