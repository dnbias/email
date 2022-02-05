package org.prog3.email;

import org.prog3.email.model.*;

public class Request {
    private final RequestType type;
    private final String account;
    private Email email = null;

    public Request(RequestType type, String account, Email email) {
        this.type = type;
        this.account = account;
        if (type != RequestType.PullMessages) {
            this.email = email;
        }
    }

    public RequestType getType() {
        return type;
    }

    public String getAccount(){
        return account;
    }

    public Email getEmail() {
        return email;
    }
}


