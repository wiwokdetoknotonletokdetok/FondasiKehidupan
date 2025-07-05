package org.gaung.wiwokdetok.fondasikehidupan.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class UserPointMessage {
    private String userId;
    private int points;
}
