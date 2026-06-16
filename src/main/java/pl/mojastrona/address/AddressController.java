package pl.mojastrona.address;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
@Slf4j
public class AddressController {

    private final AddressService addressService;

        @PostMapping
        public void createOrUpdate(@Valid @RequestBody CreateAddressRequest createAddressRequest) {

            addressService.createOrUpdate(createAddressRequest);
        }

}
