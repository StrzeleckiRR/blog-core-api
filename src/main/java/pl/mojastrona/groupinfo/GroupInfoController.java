package pl.mojastrona.groupinfo;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/groups-info")
@RequiredArgsConstructor
@Slf4j
public class GroupInfoController {

    private final GroupInfoService groupInfoService;

    @PostMapping
    public void create(@Valid @RequestBody CreateGroupInfoRequest groupInfoRequest) {

        groupInfoService.create(groupInfoRequest);
    }

    @GetMapping
    //@PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Page<FindGroupInfoResponse>> find(@RequestParam(value = "u_id")Long userId,
                                                       @RequestParam int page,
                                                       @RequestParam int size){
        Page<FindGroupInfoResponse> body = groupInfoService.find(userId, page, size);
        return ResponseEntity.ok(body);
    }

}
