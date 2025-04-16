package com.autoblog.autoblog.dto;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotEmpty;

@Getter
@Setter
public class CoupangDto {

    @NotEmpty(message = "Subld는 비어 있을 수 없습니다.")
    private String subld;

    @NotEmpty(message = "API Key는 비어 있을 수 없습니다.")
    private String apiKey;

    @NotEmpty(message = "Secret Key는 비어 있을 수 없습니다.")
    private String secretKey;

}
