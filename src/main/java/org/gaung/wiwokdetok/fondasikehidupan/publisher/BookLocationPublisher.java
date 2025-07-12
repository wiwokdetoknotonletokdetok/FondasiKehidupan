package org.gaung.wiwokdetok.fondasikehidupan.publisher;

import org.gaung.wiwokdetok.fondasikehidupan.dto.AmqpBookLocationMessage;

public interface BookLocationPublisher {

    void sendNewBookLocationMessage(AmqpBookLocationMessage message);
}
