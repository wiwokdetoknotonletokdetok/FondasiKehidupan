package org.gaung.wiwokdetok.fondasikehidupan.publisher;

import org.gaung.wiwokdetok.fondasikehidupan.dto.AmqpUserBookViewMessage;

public interface UserActivityPublisher {

    void sendUserBookViewMessage(AmqpUserBookViewMessage message);
}
