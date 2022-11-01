package eu.opertusmundi.api_auth.model;

import javax.validation.constraints.NotBlank;

@lombok.Getter
@lombok.Setter
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public class ClientDto
{
    @NotBlank
    protected String key;   
}
