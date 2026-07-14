package pl.mojastrona.user;


import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.mojastrona.BaseUnitTest;
import pl.mojastrona.groupinfo.GroupInfo;
import pl.mojastrona.groupinfo.GroupInfoService;

import java.util.HashSet;
import java.util.Optional;


import static org.assertj.core.api.Assertions.assertThat;

class UserServiceTest extends BaseUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private GroupInfoService groupInfoService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Captor
    private ArgumentCaptor<User> argumentCaptor;

    @InjectMocks
    private UserService underTest;

    @Test
    void givenCorrectRequest_whenCreate_thenCreateUser() {

        String encodedPass = "encodedPass";

        CreateUserRequest createUserRequest = new CreateUserRequest("StrzeleckiRR", "marcin123");
        Mockito.when(passwordEncoder.encode(createUserRequest.getPassword())).thenReturn(encodedPass);

        underTest.create(createUserRequest);

        Mockito.verify(userRepository).save(argumentCaptor.capture());
        User user = argumentCaptor.getValue();
        assertThat(user).isNotNull();
        assertThat(user.getLogin()).isEqualTo(createUserRequest.getLogin());
        assertThat(user.getPassword()).isEqualTo(encodedPass);
        assertThat(user.getRole()).isEqualTo(UserRole.USER);
        assertThat(user.getVersion()).isNull();
        assertThat(user.getCreatedDateTime()).isNull();
        assertThat(user.getLastModifiedDateTime()).isNull();
        assertThat(user.getGroupsInfos()).isNull();
        assertThat(user.getAddress()).isNull();
    }

    @Test
    void givenUserIdNotExist_whenJoinToGroup_ThenEntityNotFoundException() {


        JoinToGroupRequest request = new JoinToGroupRequest(62L, 23L);
        Mockito.when(userRepository.findById(request.getUserId())).thenReturn(Optional.empty());


        Executable executable = () -> underTest.joinToGroup(request);

        Assertions.assertThrows(EntityNotFoundException.class, executable);
        Mockito.verifyNoInteractions(groupInfoService);
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test
    void givenGroupIdNotExsist_whenJoinToGroup_ThenEntityNotFoundException() {


        JoinToGroupRequest request = new JoinToGroupRequest(62L, 23L);
        Mockito.when(userRepository.findById(request.getUserId())).thenReturn(Optional.of(User.builder()
                .groupsInfos(new HashSet<>())
                .build()));

        Mockito.when(groupInfoService.findById(request.getGroupId())).thenThrow(EntityNotFoundException.class);


        Executable executable = () -> underTest.joinToGroup(request);

        Assertions.assertThrows(EntityNotFoundException.class, executable);
        Mockito.verify(userRepository).findById(request.getUserId());
        Mockito.verify(groupInfoService).findById(request.getGroupId());
        Mockito.verifyNoMoreInteractions(userRepository, groupInfoService);
    }

    @Test
    void givenCorrectRequest_whenJoinToGroup_ThenAddGroupToUser() {


        JoinToGroupRequest request = new JoinToGroupRequest(62L, 23L);
        User user = User.builder()
                .id(request.getUserId())
                .groupsInfos(new HashSet<>())
                .build();

        Mockito.when(userRepository.findById(request.getUserId())).thenReturn(Optional.of(user));

        GroupInfo groupInfo = GroupInfo.builder()
                .id(request.getGroupId())
                .build();

        Mockito.when(groupInfoService.findById(request.getGroupId())).thenReturn(groupInfo);


        underTest.joinToGroup(request);

        assertThat(user.getGroupsInfos())
                .hasSize(1)
                        .containsExactly(groupInfo);

        Mockito.verify(userRepository).findById(request.getUserId());
        Mockito.verify(groupInfoService).findById(request.getGroupId());
    }
}