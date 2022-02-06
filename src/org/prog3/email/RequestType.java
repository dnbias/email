package org.prog3.email;

import java.io.Serializable;

public enum RequestType implements Serializable {
    PullMessages,
    PushMessage,
    DeleteMessage
}
