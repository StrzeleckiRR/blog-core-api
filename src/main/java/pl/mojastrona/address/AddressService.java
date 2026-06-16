package pl.mojastrona.address;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.mojastrona.user.User;
import pl.mojastrona.user.UserService;

@Service
@RequiredArgsConstructor
public class AddressService {
    private final AddressRepository addressRepository;

    private final UserService userService;


    @Transactional
    public void createOrUpdate(CreateAddressRequest createAddressRequest){

        User user = userService.findById(createAddressRequest.getUserId());

        Address address = addressRepository.findById(createAddressRequest.getUserId())
                .orElse(Address.builder()
                .user(user)
                .build());

        address.setStreet(createAddressRequest.getStreet());
        address.setCity(createAddressRequest.getCity());

        addressRepository.save(address);
    }
}
