package com.autoblog.autoblog.dto;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotEmpty;

@Getter
@Setter
public class ApiKeyDto {

    @NotEmpty(message = "Subld는 비어 있을 수 없습니다.")
    private String subId;

    @NotEmpty(message = "API Key는 비어 있을 수 없습니다.")
    private String apiKey;

    @NotEmpty(message = "Secret Key는 비어 있을 수 없습니다.")
    private String secretKey;

    @NotEmpty(message = "Vivoldi Key는 비어 있을 수 없습니다.")
    private String vivoldiKey;
    
    @NotEmpty(message = "Vivoldi ID는 비어 있을 수 없습니다.")
    private String vivoldiId;

    private String keyword;
}
