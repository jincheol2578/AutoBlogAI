package com.autoblog.autoblog.dto;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotEmpty;

@Getter
@Setter
public class ApiKeyDto {

    private String subId;

    private String apiKey;

    private String secretKey;

    private String vivoldiKey;
    
    private String vivoldiId;

    private String keyword;
}
