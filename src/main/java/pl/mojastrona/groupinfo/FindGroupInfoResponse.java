package pl.mojastrona.groupinfo;

import lombok.Value;

@Value
public class FindGroupInfoResponse {

    String name;

    public static FindGroupInfoResponse from(GroupInfo groupInfo){
        return new FindGroupInfoResponse(groupInfo.getName());
    }
}
