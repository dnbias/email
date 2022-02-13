package org.prog3.email.model;

import java.io.Serializable;

public enum RequestType implements Serializable {
    PullMessages,
    PushMessage,
    DeleteMessage,
    CloseConnection,
    Identification,
    CheckNotifications
}
