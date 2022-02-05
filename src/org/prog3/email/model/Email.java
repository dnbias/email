package org.prog3.email.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Email {
    private int id;
    private final String sender, subject, body;
    private final List<String> receivers;
    private final Calendar date;

    public Email(String sender, List<String> receivers, String subject, String body, Calendar date) {
        this.sender = sender;
        this.receivers = new ArrayList<>(receivers);
        this.subject = subject;
        this.body = body;
        this.date = date;
    }
    public Email(String sender, String receiver, String subject, String body, Calendar date) {
        this.sender = sender;
        this.receivers = new ArrayList<>();
        this.receivers.add(receiver);
        this.subject = subject;
        this.body = body;
        this.date = date;
    }

    public int getId() {
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

    public Calendar getDate() {
        return date;
    }
}

