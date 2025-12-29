package de.conti.tires.mandi.container.security.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class UserInfoResponse {
    private UUID uuid;
    private String jwtToken;
    private String username;
    private List<String> roles;
    private String language;

    public UserInfoResponse(UUID uuid, String username, List<String> roles, String jwtToken, String language) {
        this.uuid = uuid;
        this.username = username;
        this.roles = roles;
        this.jwtToken = jwtToken;
        this.language = language;
    }

    public UserInfoResponse(UUID uuid, String username, List<String> roles, String language) {
        this.uuid = uuid;
        this.username = username;
        this.roles = roles;
        this.language = language;
    }
}


