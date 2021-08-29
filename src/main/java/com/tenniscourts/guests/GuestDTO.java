package com.tenniscourts.guests;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Column;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Data
public class GuestDTO {

    private Long id;

    private String name;

    private String ipNumberCreate;

    private LocalDateTime dateCreate;

}
