package pl.mojastrona.accountant;


import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.*;
import pl.mojastrona.BaseUnitTest;
import pl.mojastrona.client.Client;
import pl.mojastrona.client.ClientService;

import java.util.HashSet;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


class AccountantServiceTest extends BaseUnitTest {

    @Mock
    private AccountantRepository accountantRepository;

    @Mock
    private ClientService clientService;

    @Captor
    ArgumentCaptor<Accountant> argumentCaptor;

    @InjectMocks
    private AccountantService accountantService;


    @Test
    void givenCorrectRequest_whenCreate_thenCorrectAccountant() {

        //Arrange(Przygotowanie)

        CreateAccountantRequest accountantRequest = new CreateAccountantRequest("Marcin Strzelecki");

        //Act(wykonanie)
        accountantService.create(accountantRequest);

        //Assert(weryfikacja)

        Mockito.verify(accountantRepository).save(argumentCaptor.capture());
        // verify() - "Upewnij się, że metoda save została wywołana"
        // argumentCaptor.capture() - "Złap argument, który został przekazany do save"

        Accountant accountant = argumentCaptor.getValue();
        assertThat(accountant).isNotNull();
        assertThat(accountant.getId()).isNull();
        assertThat(accountant.getVersion()).isNull();
        assertThat(accountant.getCreatedDateTime()).isNull();
        assertThat(accountant.getLastModifiedDateTime()).isNull();
        assertThat(accountant.getClients()).isNull();

    }

    @Test
    void givenAccountantIdNotExist_whenAttachClient_ThenEntityNotFoundException() {

        AttachClientRequest request = new AttachClientRequest(67L, 100L);
        Mockito.when(accountantRepository.findByAccountantIdFetchGroupsInfo(request.getAccountantId())).thenReturn(Optional.empty());

        Executable executable = () -> accountantService.attachClient(request);

        assertThrows(EntityNotFoundException.class, executable);
        Mockito.verifyNoMoreInteractions(clientService);
    }

    @Test
    void givenClientIdNotExist_whenAttachClient_ThenEntityNotFoundException() {

        AttachClientRequest request = new AttachClientRequest(67L, 100L);
        Mockito.when(accountantRepository.findByAccountantIdFetchGroupsInfo(request.getAccountantId()))
                .thenReturn(Optional.of(Mockito.mock(Accountant.class)));
        Mockito.when(clientService.findById(request.getClientId())).thenThrow(IllegalArgumentException.class);

        Executable executable = () -> accountantService.attachClient(request);

        assertThrows(IllegalArgumentException.class, executable);

    }

    @Test
    void givenCorrectRequest_whenAttachClient_ThenAttachClientToAccountant() {

        AttachClientRequest request = new AttachClientRequest(67L, 100L);

        Accountant accountant = Accountant.builder()
                .id(request.getAccountantId())
                .clients(new HashSet<>())
                .build();

        Client client = Client.builder()
                .id(request.getClientId())
                .build();

        Mockito.when(accountantRepository.findByAccountantIdFetchGroupsInfo(request.getAccountantId()))
                .thenReturn(Optional.of(accountant));

        Mockito.when(clientService.findById(request.getClientId())).thenReturn(client);

        accountantService.attachClient(request);

        assertThat(accountant.getClients())
                .hasSize(1)
                .containsExactly(client);

    }
}