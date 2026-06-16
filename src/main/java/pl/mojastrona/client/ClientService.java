package pl.mojastrona.client;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.mojastrona.accountant.Accountant;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientService {
    private final ClientRepository clientRepository;


    @Transactional
    public void create(CreateClientRequest clientRequest){

        Client client = Client.builder()
                .name(clientRequest.getName())
                .build();

        clientRepository.save(client);
    }

    public Client findById(@NotNull Long clientId) {
        return clientRepository.findById(clientId)
                .orElseThrow(EntityNotFoundException::new);
    }

    public Page<FindClientResponse> find(Long accountantId, int page, int size) {

        Page<Client> accountants = clientRepository.find(accountantId,
                PageRequest.of(page, size, Sort.by(Sort.Order.asc("name"))));

        return accountants.map(FindClientResponse::from);
    }

    public Page<FindClientResponse> find(@Valid FindClientRequest clientRequest, Pageable pageable) {

        Page<Client> clients = clientRepository.findAll(prepareSpec(clientRequest),pageable);

        return clients.map(FindClientResponse::from);
    }

    private Specification<Client> prepareSpec(@Valid FindClientRequest clientRequest) {

        return (root, query, criteriaBuilder) -> {

            Join<Client, Accountant> joinAccountants = root.join("accountants");

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.equal(joinAccountants.get("id"), clientRequest.accountantId()));
            if (clientRequest.name() != null) {
                predicates.add(criteriaBuilder.like(root.get("name"), "%" + clientRequest.name() + "%"));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
