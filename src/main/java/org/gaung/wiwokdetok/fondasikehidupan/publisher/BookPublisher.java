package org.gaung.wiwokdetok.fondasikehidupan.publisher;

import org.gaung.wiwokdetok.fondasikehidupan.dto.AmqpBookMessage;

public interface BookPublisher {

    void sendBookMessage(AmqpBookMessage message);
}
