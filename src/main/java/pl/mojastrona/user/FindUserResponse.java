package pl.mojastrona.user;

import lombok.Value;

@Value
public class FindUserResponse {

    Long id;

    String login;

    public static FindUserResponse from(User user){ return new FindUserResponse(user.getId(),user.getLogin());
    }
}
