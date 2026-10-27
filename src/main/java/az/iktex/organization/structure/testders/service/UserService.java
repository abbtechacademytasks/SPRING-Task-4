package az.iktex.organization.structure.testders.service;

import az.iktex.organization.structure.testders.request.PassportRequest;
import az.iktex.organization.structure.testders.request.UserRequest;
import az.iktex.organization.structure.testders.response.PassportResponse;
import az.iktex.organization.structure.testders.response.UserResponse;
import az.iktex.organization.structure.testders.entity.Passport;
import az.iktex.organization.structure.testders.entity.User;
import az.iktex.organization.structure.testders.enums.Gender;
import az.iktex.organization.structure.testders.mapper.PassportMapper;
import az.iktex.organization.structure.testders.mapper.UserMapper;
import az.iktex.organization.structure.testders.repository.PassportRepository;
import az.iktex.organization.structure.testders.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final PassportRepository passportRepository;
    private final UserMapper userMapper;
    private final PassportMapper passportMapper;

    @Transactional
    public UserResponse create(UserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalStateException("Email already exists");
        }

        User user = userMapper.toEntity(request);
        User savedUser = userRepository.save(user);

        return userMapper.toResponse(savedUser);
    }

    public UserResponse getById(Long id) {
        User user = userRepository.findByIdWithPassport(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("User not found: " + id));

        return userMapper.toResponse(user);
    }

    @Transactional
    public UserResponse update(Long id, UserRequest request) {
        User user = userRepository.findByIdWithPassport(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("User not found: " + id));

        if (userRepository.existsByEmailAndIdNot(request.email(), id)) {
            throw new IllegalStateException("Email already exists");
        }

        userMapper.updateEntity(request, user);

        return userMapper.toResponse(user);
    }

    @Transactional
    public void delete(Long id) {
        User user = userRepository.findByIdWithPassport(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("User not found: " + id));

        userRepository.delete(user);
    }

    public UserResponse getByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new EntityNotFoundException("User not found: " + email));

        return userMapper.toResponse(user);
    }

    public List<UserResponse> getAll(Gender gender) {
        List<User> users = gender == null
                ? userRepository.findAll()
                : userRepository.findAllByGender(gender);

        return userMapper.toResponseList(users);
    }

    public List<UserResponse> getAllWithoutPassport() {
        return userMapper.toResponseList(
                userRepository.findAllWithoutPassport()
        );
    }

    @Transactional
    public PassportResponse addPassport(Long userId, PassportRequest request) {
        User user = userRepository.findByIdWithPassport(userId)
                .orElseThrow(() ->
                        new EntityNotFoundException("User not found: " + userId));

        if (user.getPassport() != null) {
            throw new IllegalStateException("User already has a passport");
        }

        if (passportRepository.existsBySerialNumber(request.serialNumber())) {
            throw new IllegalStateException("Passport serial number already exists");
        }

        Passport passport = passportMapper.toEntity(request);
        user.setPassport(passport);

        Passport savedPassport = passportRepository.save(passport);

        return passportMapper.toResponse(savedPassport);
    }

    public PassportResponse getPassport(Long userId) {
        User user = userRepository.findByIdWithPassport(userId)
                .orElseThrow(() ->
                        new EntityNotFoundException("User not found: " + userId));

        if (user.getPassport() == null) {
            throw new EntityNotFoundException("Passport not found for user: " + userId);
        }

        return passportMapper.toResponse(user.getPassport());
    }

    @Transactional
    public void deletePassport(Long userId) {
        User user = userRepository.findByIdWithPassport(userId)
                .orElseThrow(() ->
                        new EntityNotFoundException("User not found: " + userId));

        if (user.getPassport() == null) {
            throw new EntityNotFoundException("Passport not found for user: " + userId);
        }

        user.setPassport(null);
    }
}
