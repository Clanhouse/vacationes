package pl.seahawks.vacationes.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import pl.seahawks.vacationes.converter.UserConverter;
import pl.seahawks.vacationes.exception.UserAlreadyExistsException;
import pl.seahawks.vacationes.repository.RoleRepository;
import pl.seahawks.vacationes.repository.UserRepository;
import pl.seahawks.vacationes.request.RegisterRequest;
import pl.seahawks.vacationes.response.RegisterResponse;
import pl.seahawks.vacationes.user.model.Role;
import pl.seahawks.vacationes.user.model.User;


import javax.transaction.Transactional;
import java.util.HashSet;

@Service
@AllArgsConstructor
@Slf4j
public class RegistrationService {

    private final BCryptPasswordEncoder passwordEncoder;
    private final UserConverter userConverter;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserService userService;

    private final Role DEFAULT_ROLE = Role.ROLE_USER;

    @Transactional
    public RegisterResponse registerNewUser(RegisterRequest request) {
        if (userService.isUserExists(request)) {
            return RegisterResponse
                    .builder()
                    .success(false)
                    .exception(new UserAlreadyExistsException(request.getEmail()))
                    .build();
        }
        User user = userConverter.from(request);
        user.setUserRoles(new HashSet<>());
        updateCredentials(user);
        userRepository.save(user);
        return RegisterResponse.builder()
                .success(true)
                .id(user.getId())
                .build();
    }


    private void updateCredentials(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.getUserRoles().add(roleRepository.findByRole(DEFAULT_ROLE));
        user.setMailAuthenticated(false);
        user.setNewsletterSigned(false);
    }
}
