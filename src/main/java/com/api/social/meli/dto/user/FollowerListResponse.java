package com.api.social.meli.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FollowerListResponse {
    @JsonProperty("userId")
    private Long userId;

    @JsonProperty("userName")
    private String userName;

    @JsonProperty("followers")
    private List<UserBasicDto> followers;
}
