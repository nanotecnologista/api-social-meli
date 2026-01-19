package com.api.social.meli.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginationParams {

    private String order;
    private Integer page;
    private Integer size;
    private Integer offset;
    private Integer limit;
}
