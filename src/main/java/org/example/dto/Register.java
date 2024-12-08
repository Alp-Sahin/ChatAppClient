package org.example.dto;

import java.io.Serializable;

public record Register(String username, String password)  implements Serializable {
}
