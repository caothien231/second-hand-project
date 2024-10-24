package com.example.jwt_autho.dtos;

import java.util.Map;

import lombok.Data;

@Data
public class EmailRequestDto {
    
    private String toEmail;
    private String subject;
    private String templateName;
    private Map<String, Object> templateModel;

}
