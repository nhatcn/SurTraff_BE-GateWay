package com.example.demo.controller;

import com.example.demo.DTO.UserDTO;
import com.example.demo.model.User;
import com.example.demo.service.RoleService;
import com.example.demo.service.UserService;
import com.example.demo.util.JwtUtil;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {

    private JavaMailSender javaMailSender;
    private UserService userService;
    private RoleService roleService;

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUser() {
        List<UserDTO> users = userService.getAllUser();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> createUser(@RequestBody UserDTO userDTO) {
        if (userService.getUserByUserName(userDTO.getUserName()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Username already exists."));
        }

        try {
            User newUser = new User();
            newUser.setFullName(userDTO.getFullName());
            newUser.setUserName(userDTO.getUserName());
            newUser.setPassword(userDTO.getPassword());
            newUser.setEmail(userDTO.getEmail());
            newUser.setAvatar("https://th.bing.com/th/id/OIP.Fogk0Q6C7GEQEdVyrbV9MwHaHa?rs=1&pid=ImgDetMain");
            newUser.setStatus(true);
            newUser.setRole(roleService.getRoleById(1L).orElseThrow());

            User createdUser = userService.createUser(newUser);
            String token = JwtUtil.generateToken(createdUser.getId().toString(), createdUser.getRole().getRoleName());

            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "userId", createdUser.getId().toString(),
                    "role", createdUser.getRole().getRoleName()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to register user. Please try again."));
        }
    }


    @PostMapping("/signin")
    public ResponseEntity<?> googleSignin(@RequestBody Map<String, String> request) {
        String googleToken = request.get("token");

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(), new JacksonFactory())
                .setAudience(Collections.singletonList("359834252791-bdrg125j62411mp1u8suqqnl6v79339a.apps.googleusercontent.com"))
                .build();

        try {
            GoogleIdToken idToken = verifier.verify(googleToken);
            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();
                String email = payload.getEmail();
                String name = (String) payload.get("name");
                String avatar = (String) payload.get("picture");

                Optional<UserDTO> existingUserDTO = userService.getUserByEmail(email);
                UserDTO userDTO;

                if (existingUserDTO.isPresent()) {
                    userDTO = existingUserDTO.get();
                } else {
                    User newUser = new User();
                    newUser.setFullName(name);
                    newUser.setEmail(email);
                    newUser.setAvatar(avatar);
                    newUser.setStatus(true);
                    newUser.setRole(roleService.getRoleById(1L).orElseThrow());

                    User createdUser = userService.createUser(newUser);
                    userDTO = userService.getUserById(createdUser.getId()).orElseThrow();
                }

                String tokenGenerated = JwtUtil.generateToken(userDTO.getUserId().toString(), userDTO.getRoleName());

                return ResponseEntity.ok(Map.of(
                        "token", tokenGenerated,
                        "userId", userDTO.getUserId().toString(),
                        "role", userDTO.getRoleName()
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Xác thực Google thất bại"));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Token không hợp lệ"));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody UserDTO userDTO) {
        try {
            UserDTO loggedInUser = userService.login(userDTO.getUserName(), userDTO.getPassword());
            String token = JwtUtil.generateToken(loggedInUser.getUserId().toString(), loggedInUser.getRoleName());

            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "userId", loggedInUser.getUserId().toString(),
                    "role", loggedInUser.getRoleName()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/forgotPassword")
    public ResponseEntity<?> forgotPassword(@RequestBody UserDTO usersDTO) throws IOException {
        Optional<UserDTO> optionalUserDto = userService.getUserByEmail(usersDTO.getEmail());

        if (optionalUserDto.isPresent()) {
            UserDTO userDto = optionalUserDto.get();
            String newPassword = userService.generateRandomPassword();
            userDto.setPassword(newPassword);

            User updatedUser = userService.updateUser(userDto.getUserId(), userDto,null);
            if (updatedUser != null) {
                sendEmail(userDto.getEmail(), newPassword, userDto.getUserName());
                return ResponseEntity.ok(userDto);
            }
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getUserProfile(HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");
        return userService.getUserById(Long.parseLong(userId))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable Long id,
            @ModelAttribute UserDTO updatedUser,
            @RequestPart(value = "avatarFile", required = false) MultipartFile avatarFile
    ) throws IOException {
        User user = userService.updateUser(id, updatedUser,avatarFile);

        if (user != null) {
            return ResponseEntity.ok(userService.getUserById(id).orElseThrow());
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    public void sendEmail(String toEmail, String password, String user) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(toEmail);
            helper.setSubject("Password Reset");

            String htmlContent = "<p style=\"color: #333; font-size: 16px;\">Dear " + user + ",</p>"
                    + "<p style=\"color: #333; font-size: 16px;\">Your new password is: "
                    + "<span style=\"font-size: 32px; background-color: #f0f0f0; padding: 5px;\">" + password + "</span></p>"
                    + "<p style=\"color: #333; font-size: 16px;\">Thank you for using our service.</p>"
                    + "<p style=\"color: #333; font-size: 16px;\">Best regards,<br>Group1-SE1712</p>";

            helper.setText(htmlContent, true);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
