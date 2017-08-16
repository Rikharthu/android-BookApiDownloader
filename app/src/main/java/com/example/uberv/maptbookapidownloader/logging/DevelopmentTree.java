package com.example.uberv.maptbookapidownloader.logging;

import timber.log.Timber;

public class DevelopmentTree extends Timber.DebugTree {
    @Override
    protected String createStackElementTag(StackTraceElement element) {
        return String.format("%s#%s:%s", super.createStackElementTag(element), element.getMethodName(), element.getLineNumber());
    }
}
