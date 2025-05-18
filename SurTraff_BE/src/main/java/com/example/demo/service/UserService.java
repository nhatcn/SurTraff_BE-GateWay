package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.DTO.UserDTO;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private UserDTO convertToDTO(User user, boolean includePassword) {
        if (includePassword) {
            return new UserDTO(
                    user.getId(),
                    user.getUserName(),
                    user.getFullName(),
                    user.getEmail(),
                    user.getStatus(),
                    user.getPassword(),
                    user.getAvatar(),
                    user.getRole().getId(),
                    user.getRole().getRoleName()
            );
        } else {
            UserDTO dto = new UserDTO(
                    user.getId(),
                    user.getUserName(),
                    user.getFullName(),
                    user.getEmail(),
                    user.getStatus(),
                    user.getAvatar(),
                    user.getRole().getId(),
                    user.getRole().getRoleName()
            );
            dto.setPassword(null);
            return dto;
        }
    }

    public List<UserDTO> getAllUser() {
        return userRepository.findAll().stream()
                .map(user -> convertToDTO(user, false))
                .collect(Collectors.toList());
    }

    public Optional<UserDTO> getUserById(Long id) {
        return userRepository.findById(id)
                .map(user -> convertToDTO(user, true));
    }

    public Optional<UserDTO> getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(user -> convertToDTO(user, false));
    }

    public Optional<UserDTO> getUserByUserName(String userName) {
        return userRepository.findByUserName(userName)
                .map(user -> convertToDTO(user, true));
    }

    public User createUser(User user) {
        // ✅ Mã hóa mật khẩu trước khi lưu
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User updateUser(Long id, UserDTO updatedUser) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User existingUser = userOptional.get();
            existingUser.setFullName(updatedUser.getUserName());
            existingUser.setEmail(updatedUser.getEmail());

            // ✅ Nếu mật khẩu mới != null và không rỗng, mã hóa lại
            if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
            }

            existingUser.setStatus(updatedUser.getStatus());
            existingUser.setRole(roleService.getRoleById(updatedUser.getRoleId()).get());

            return userRepository.save(existingUser);
        }
        return null;
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public UserDTO login(String userName, String rawPassword) {
        Optional<User> userOptional = userRepository.findByUserName(userName);
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // bcrypt
            if (passwordEncoder.matches(rawPassword, user.getPassword())) {
                return convertToDTO(user, true);
            } else {
                throw new IllegalArgumentException("Invalid password");
            }
        } else {
            throw new IllegalArgumentException("Invalid username or password");
        }
    }

    public static String generateRandomPassword() {
        Random random = new Random();
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            password.append(random.nextInt(10));
        }
        return password.toString();
    }
}
