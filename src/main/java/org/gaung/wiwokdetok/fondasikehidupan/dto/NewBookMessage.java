package org.gaung.wiwokdetok.fondasikehidupan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewBookMessage {

    private UUID id;

    private String title;

    private String synopsis;

    private String bookPicture;

    private UUID createdBy;
}
