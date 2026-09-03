package com.extreme.humanresources.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class RoleResponse {

    private Long id;
    private String name;
    private String description;
}
