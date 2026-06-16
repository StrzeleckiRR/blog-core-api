package pl.mojastrona.accountant;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.mojastrona.client.Client;
import pl.mojastrona.client.ClientService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountantService {
    private final AccountantRepository accountantRepository;
    private final ClientService clientService;


    @Transactional
    public void create(CreateAccountantRequest accountantRequest){

        Accountant accountant = Accountant.builder()
                .name(accountantRequest.getName())
                .build();

        accountantRepository.save(accountant);
    }

    @Transactional
    public void attachClient(@Valid AttachClientRequest attachClientRequest) {

        Accountant accountant = accountantRepository.findByAccountantIdFetchGroupsInfo(attachClientRequest.getAccountantId())
                .orElseThrow(EntityNotFoundException::new);

        Client client = clientService.findById(attachClientRequest.getClientId());

        accountant.getClients().add(client);
    }

    @Transactional
    public void leaveAttachClient(@Valid DetachClientRequest detachClientRequest) {

        Accountant accountant = accountantRepository.findByAccountantIdFetchGroupsInfo(detachClientRequest.getAccountantId())
                .orElseThrow(EntityNotFoundException::new);

        Client client = clientService.findById(detachClientRequest.getClientId());

        accountant.getClients().remove(client);
    }

    public Page<FindAccountantResponse> find(Long clientId, int page, int size) {

        Page<Accountant> accountants = accountantRepository.find(clientId,
                PageRequest.of(page, size, Sort.by(Sort.Order.asc("name"))));

        return accountants.map(FindAccountantResponse::from);
    }

    public Page<FindAccountantResponse> find(FindAccountantRequest accountantRequest, Pageable pageable) {
        Page<Accountant> accountants = accountantRepository.findAll(prepareSpec(accountantRequest),pageable);

        return accountants.map(FindAccountantResponse::from);
    }

    private Specification<Accountant> prepareSpec(FindAccountantRequest accountantRequest) {

        return (root, query, criteriaBuilder) -> {

            Join<Accountant, Client> joinClients = root.join("clients");

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.equal(joinClients.get("id"), accountantRequest.clientId()));
            if (accountantRequest.name() != null) {
                predicates.add(criteriaBuilder.like(root.get("name"), "%" + accountantRequest.name() + "%"));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

    }
}
