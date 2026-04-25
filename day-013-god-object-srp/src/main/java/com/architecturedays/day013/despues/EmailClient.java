package com.architecturedays.day013.despues;

public interface EmailClient {
    void send(String to, String subject, byte[] attachment);
}
