/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.eckoit.diarization;

import com.googlecode.eckoit.audio.RecordingFinishedManager;
import com.googlecode.eckoit.events.ConversionFinishedEvent;
import com.googlecode.eckoit.events.DiarizationFinishedEvent;
import com.googlecode.eckoit.events.DiarizeConfigEvent;
import com.googlecode.eckoit.events.MeetingFinalProcessingEvent;
import fr.lium.spkDiarization.system.Eckoization;
import java.io.File;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventSubscriber;

/**
 *
 * @author ryan
 */
public class Diarizer implements  EventSubscriber<ConversionFinishedEvent> {

    RecordingFinishedManager manager;
    private boolean diarize = false;

    public Diarizer(RecordingFinishedManager manager) {
        this.manager = manager;
        EventBus.subscribeStrongly(ConversionFinishedEvent.class, this);
        EventBus.subscribeStrongly(DiarizeConfigEvent.class, new EventSubscriber<DiarizeConfigEvent>() {

            @Override
            public void onEvent(DiarizeConfigEvent t) {
                diarize = t.getDiarize();
            }
        });
    }


    @Override
    public void onEvent(ConversionFinishedEvent recordingSplit) {
        File file = recordingSplit.getFinishedFile();
        if (diarize) {
            EventBus.publish(new MeetingFinalProcessingEvent("Analyzing Interesting Moments", 0.20f));
            System.out.println("Diarization of: " + file);
            Eckoization.main(new String[] {file.getAbsolutePath()});
            System.out.println("Diarization Complete");
            EventBus.publish(new DiarizationFinishedEvent(file));
        }
        // lame to call this directly. Should be an event.
        manager.DiarizationComplete(file);
    }
}
