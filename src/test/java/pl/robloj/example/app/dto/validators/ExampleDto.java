package pl.robloj.example.app.dto.validators;

import lombok.*;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
public class ExampleDto {

    @IBAN(allowEmpty = true, message = "account number can be empty or valid")
    private String optionalAccountNumber;

    @IBAN
    private String requiredAccountNumber;

}
