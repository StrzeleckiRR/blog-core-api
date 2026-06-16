package pl.mojastrona.accountant;

import lombok.Value;

@Value
public class FindAccountantResponse {

    Long id;

    String name;

    public static FindAccountantResponse from(Accountant accountant) {
        return new FindAccountantResponse(accountant.getId(), accountant.getName());
    }
}
