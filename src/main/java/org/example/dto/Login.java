package org.example.dto;

import java.io.Serializable;

public record Login(String username, String password)  implements Serializable {
}
