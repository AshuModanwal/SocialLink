package com.socialLink.Dtos;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailBody {
    private String recipient;
    private String msgBody;
    private String subject;
}
