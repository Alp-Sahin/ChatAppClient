package org.example.dto;

import java.io.Serializable;

public record Message(String username, String message, java.time.LocalDateTime date)  implements Serializable {
}
