package pl.mojastrona.client;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
@Slf4j
public class ClientController {

    private final ClientService clientService;

        @PostMapping
        public void create(@Valid @RequestBody CreateClientRequest clientRequest) {

            clientService.create(clientRequest);
        }

    @GetMapping
    public ResponseEntity<Page<FindClientResponse>> find(@RequestParam(value = "acc_id")Long accountantId,
                                                             @RequestParam int page,
                                                             @RequestParam int size){
        Page<FindClientResponse> body = clientService.find(accountantId, page, size);
        return ResponseEntity.ok(body);
    }

    @PostMapping("/find")
    public ResponseEntity<Page<FindClientResponse>> find(@Valid @RequestBody FindClientRequest clientRequest, Pageable pageable){
        Page<FindClientResponse> body = clientService.find(clientRequest, pageable);

        return ResponseEntity.ok(body);
    }

}
