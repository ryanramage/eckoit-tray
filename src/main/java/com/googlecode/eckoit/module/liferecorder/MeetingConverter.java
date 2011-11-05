/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.eckoit.module.liferecorder;

import java.util.List;
import org.codehaus.jackson.JsonNode;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.ViewResult;
import org.ektorp.ViewResult.Row;
import org.joda.time.DateTime;
import org.joda.time.Instant;

/**
 *
 * The goal of this class is to convert a sequence of
 * liferecording sections into a meeting
 *
 * @author ryan
 */
public class MeetingConverter {


    /**
     * Given a start and end time, this method converts Liferecording audio
     * from the src couch, joins it, and inserts it into the the dst couch
     * @param start The liferecorder start time
     * @param end The liferecorder end time
     * @param src The couch that holds the liferecorder audio
     * @param dst The couch to add the meeting to
     */
    public void convertToMeeting(DateTime start, DateTime end, CouchDbConnector src, CouchDbConnector dst) {
        ViewResult viewResult = getLifercorderData(start, end, src);
    }

    protected List filterRecordings(ViewResult results, DateTime start, DateTime end) {
        long item_start = start.toDate().getTime();
        long item_end =  end.toDate().getTime();
        for (Row row:results){
            JsonNode value = row.getValueAsNode();
            if ("recording".equals(value.get("type").getTextValue())) {
                long test_start = value.get("start").getLongValue();
                long test_end   = value.get("end").getLongValue();
                if ((item_start <= test_end && item_end >= test_end) || (item_start <= test_start && item_end >= test_start) || (item_start >= test_start && item_end <= test_end)) {
                    //var mediaName = _.keys(test.value.file)[0]
                    //var url =  '/' + db_name + '/' + test.id + '/' + mediaName ;
                    if (test_start <= item_start && item_start <= test_end) {
                        long offset = item_start - test_start;
                        //recording.startOffset = offset;
                    }
                    if (test_start <= item_end && item_end <= test_end) {
                        long offset = item_end - test_start ;
                        //recording.endOffset = offset;
                    }
                    //recordings.push(recording);
                }

            }
        }
        return null;
    }


    protected ViewResult getLifercorderData(DateTime start, DateTime end, CouchDbConnector src) {
        // hack atack. Querying the view with the exact wont work
        // we need to pad by at least the size of one recording
        // an hour is an overkill, for sure
        long start_ms = start.toDate().getTime() - 3600000l; // before by an hour
        long end_ms =  end.toDate().getTime() + 3600000l; // after by an hour
        
        
        ViewQuery query = new ViewQuery().designDocId("_design/app")
                    .viewName("audio_by_time")
                    .startKey(start_ms)
                    .endKey(end_ms);
        return src.queryView(query);
    }
}
