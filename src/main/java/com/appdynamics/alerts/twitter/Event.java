package com.appdynamics.alerts.twitter;

import java.util.List;

public abstract class Event {
    String appName;
    String appID;
    String priority;
    String severity;
    String tag;
    String deepLinkUrl;
}

