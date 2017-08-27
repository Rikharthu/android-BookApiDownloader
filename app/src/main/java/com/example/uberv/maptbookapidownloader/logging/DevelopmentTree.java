package com.example.uberv.maptbookapidownloader.logging;

import timber.log.Timber;

public class DevelopmentTree extends Timber.DebugTree {

    @Override
    protected void log(int priority, String tag, String message, Throwable t) {
        super.log(priority, tag, message, t);
    }

    @Override
    protected String createStackElementTag(StackTraceElement element) {
        return String.format("%s#%s:%s", super.createStackElementTag(element), element.getMethodName(), element.getLineNumber());
    }
}
