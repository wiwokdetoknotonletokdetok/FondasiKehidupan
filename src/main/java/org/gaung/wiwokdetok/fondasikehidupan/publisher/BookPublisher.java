package org.gaung.wiwokdetok.fondasikehidupan.publisher;

import org.gaung.wiwokdetok.fondasikehidupan.dto.NewBookMessage;

public interface BookPublisher {

    void sendNewBookMessage(NewBookMessage message);

    void sendUpdateBookMessage(String message);
}
