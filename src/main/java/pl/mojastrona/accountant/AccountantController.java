package pl.mojastrona.accountant;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accountants")
@RequiredArgsConstructor
@Slf4j
public class AccountantController {

    private final AccountantService accountantService;

        @PostMapping
        public void create(@Valid @RequestBody CreateAccountantRequest accountantRequest) {

            accountantService.create(accountantRequest);
        }

    @PostMapping("/attach-client")
    public void detachClient(@Valid @RequestBody AttachClientRequest attachClientRequest) {

        accountantService.attachClient(attachClientRequest);
    }

    @PostMapping("/detach-client")
    public void detachClient(@Valid @RequestBody DetachClientRequest detachClientRequest) {

        accountantService.leaveAttachClient(detachClientRequest);
    }

    @GetMapping
    public ResponseEntity<Page<FindAccountantResponse>> find(@RequestParam(value = "c_id")Long clientId,
                                                       @RequestParam int page,
                                                       @RequestParam int size){
        Page<FindAccountantResponse> body = accountantService.find(clientId, page, size);
        return ResponseEntity.ok(body);
    }

    @PostMapping("/find")
    public ResponseEntity<Page<FindAccountantResponse>> find(@Valid @RequestBody FindAccountantRequest accountantRequest, Pageable pageable){
        Page<FindAccountantResponse> body = accountantService.find(accountantRequest, pageable);

        return ResponseEntity.ok(body);
    }

}
