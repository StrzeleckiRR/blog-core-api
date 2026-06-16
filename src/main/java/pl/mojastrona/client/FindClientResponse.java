package pl.mojastrona.client;

import lombok.Value;

@Value
public class FindClientResponse {

    String name;

    public static FindClientResponse from(Client client) {
        return new FindClientResponse(client.getName());
    }
}
