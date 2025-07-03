package org.gaung.wiwokdetok.fondasikehidupan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.gaung.wiwokdetok.fondasikehidupan.validation.AtLeastOneFieldNotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@AtLeastOneFieldNotNull
public class UpdateBookLocationRequest {

    private String locationName;

    private Double latitude;

    private Double longitude;
}

