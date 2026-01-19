package com.api.social.meli.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserBasicDto {
    @JsonProperty("userId")
    private Long userId;

    @JsonProperty("userName")
    private String userName;
}
