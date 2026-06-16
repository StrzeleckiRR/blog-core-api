package pl.mojastrona.groupinfo;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GroupInfoService {
    private final GroupInfoRepository groupRepository;


    @Transactional
    public void create(CreateGroupInfoRequest groupInfoRequest){

        GroupInfo groupInfo = GroupInfo.builder()
                .name(groupInfoRequest.getName())
                .build();

        groupRepository.save(groupInfo);
    }

    public GroupInfo findById(@NotNull Long groupId) {

        return groupRepository.findById(groupId)
                .orElseThrow(EntityNotFoundException::new);
    }

    public Page<FindGroupInfoResponse> find(Long userId, int page, int size) {

        Page<GroupInfo> groupsInfo = groupRepository.find(userId,
                PageRequest.of(page, size, Sort.by(Sort.Order.asc("name"))));

        return groupsInfo.map(FindGroupInfoResponse::from);
    }
}
