package org.gaung.wiwokdetok.fondasikehidupan.publisher;

public interface BookPublisher {

    void sendNewBookMessage(String message);

    void sendUpdateBookMessage(String message);
}
