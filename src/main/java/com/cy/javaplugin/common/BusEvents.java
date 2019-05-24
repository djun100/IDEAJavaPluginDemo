package com.cy.javaplugin.common;

import com.google.common.eventbus.EventBus;

/**
 * Created by cy on 2017/3/7.
 */
public class BusEvents {
    private static EventBus mEventBus = new EventBus();
    public static EventBus getBus(){
        return mEventBus;
    }

    public static class Progress {
        public String message;

        public Progress(String message){
            this.message=message;
        }
    }

    public static void postProgress(String message){
        BusEvents.getBus().post(new BusEvents.Progress(message));
    }
}
