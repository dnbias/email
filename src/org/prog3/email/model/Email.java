package org.prog3.email.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Email implements Serializable {
    private final UUID id = UUID.randomUUID();
    private final String sender, subject, body;
    private final List<String> receivers;
    private final Date date;

    public Email(String sender, List<String> receivers, String subject, String body, Date date) {
        this.sender = sender;
        this.receivers = new ArrayList<>(receivers);
        this.subject = subject;
        this.body = body;
        this.date = date;
    }
    public Email(String sender, String receiver, String subject, String body, Date date) {
        this.sender = sender;
        this.receivers = new ArrayList<>();
        this.receivers.add(receiver);
        this.subject = subject;
        this.body = body;
        this.date = date;
    }


    public UUID getId() {
        return id;
    }


    public String getSender() {
        return sender;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public List<String> getReceivers() {
        return receivers;
    }

    public Date getDate() {
        return date;
    }
}

