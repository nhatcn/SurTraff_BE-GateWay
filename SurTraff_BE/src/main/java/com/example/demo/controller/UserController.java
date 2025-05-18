// UserController.java
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
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {

    private JavaMailSender javaMailSender;
    private UserService userService;
    private RoleService roleService;


    @GetMapping
    public List<UserDTO> getAllUser() {
        return userService.getAllUser();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        Optional<UserDTO> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> createUser(@RequestBody UserDTO userDTO) {
        if (userService.getUserByUserName(userDTO.getUserName()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Username already exists."));
        }

        User newUser = new User();
        newUser.setFullName(userDTO.getName());
        newUser.setUserName(userDTO.getUserName());
        newUser.setPassword(userDTO.getPassword());
        newUser.setEmail(userDTO.getEmail());
        newUser.setStatus(userDTO.getStatus());
        newUser.setAvatar(userDTO.getAvatar());

        try {
            userService.createUser(newUser);

            String role = newUser.getRole().getRoleName();
            String token = JwtUtil.generateToken(newUser.getId().toString(), role);

            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            response.put("userId", newUser.getId().toString());
            response.put("role", role);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to register user. Please try again."));
        }
    }

    @GetMapping("home")
    public ResponseEntity<?> loginSuccess(){
        return ResponseEntity.ok("success");
    }

    @PostMapping("/signin")
    public ResponseEntity<?> googleSignin(@RequestBody Map<String, String> request) {
        String googleToken = request.get("token");

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(),
                new JacksonFactory()
        ).setAudience(Collections.singletonList("904077274507-k4034svo64dbgoitut47cqv7461fq5qs.apps.googleusercontent.com")).build();
        Optional<UserDTO> usersDTO=null;
        try {
            GoogleIdToken idToken = verifier.verify(googleToken);
            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();
                String email = payload.getEmail();
                String name = (String) payload.get("name");
                String avatar = (String) payload.get("picture");

                Optional<UserDTO> existingUser = userService.getUserByEmail(email);


                if (existingUser.isPresent()) {
                    usersDTO = userService.getUserByEmail(email);
                } else {
                    User newUser = new User();
                    newUser.setFullName(name);

                    newUser.setEmail(email);

                    newUser.setAvatar(avatar);
                    newUser.setRole(roleService.getRoleById(1L).get());
                    userService.createUser(newUser);
                }

                String tokenGenerated = JwtUtil.generateToken(usersDTO.get().getUserId().toString(), usersDTO.get().getRoleName());

                Map<String, String> response = new HashMap<>();
                response.put("token", tokenGenerated);
                response.put("userId", usersDTO.get().getUserId().toString());
                response.put("role", usersDTO.get().getName().toString());

                return ResponseEntity.ok(response);
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

            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            response.put("userId", loggedInUser.getUserId().toString());
            response.put("role", loggedInUser.getRoleName());

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }


    @PostMapping("/forgotPassword")
    public ResponseEntity<?> forgotPassword(@RequestBody UserDTO usersDTO) {
        Optional<UserDTO> optionalUserDto = userService.getUserByEmail(usersDTO.getEmail());

        if (optionalUserDto.isPresent()) {
            UserDTO userDto = optionalUserDto.get();

            String newPassword = userService.generateRandomPassword();
            userDto.setPassword(newPassword);

            User updatedUser = userService.updateUser(userDto.getUserId(), userDto);

            if (updatedUser != null) {
                sendEmail(usersDTO.getEmail(), newPassword, updatedUser.getUserName());
                return ResponseEntity.ok(userDto);
            }
        }

        return ResponseEntity.notFound().build();
    }
    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getUserProfile(HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");
        Optional<UserDTO> userDTO = userService.getUserById(Long.parseLong(userId));

        return userDTO.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserDTO updatedUser) {
        User user = userService.updateUser(id, updatedUser);
        if (user != null) {
            return ResponseEntity.ok(new UserDTO(
                    user.getId(),
                    user.getUserName(),
                    user.getFullName(),
                    user.getEmail(),
                    user.getStatus(), user.getPassword(), user.getAvatar(),
                    user.getRole().getId(),user.getRole().getRoleName()
            ));
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

            String htmlContent = "<p style=\"color: #333; font-size: 16px;\">Dear "+user+",</p>"
                    + "<p style=\"color: #333; font-size: 16px;\">Your new password is: <span style=\"font-size: 32px; background-color: #f0f0f0; padding: 5px;\">"
                    + password + "</span></p>"
                    + "<p style=\"color: #333; font-size: 16px;\">Thank you for using our service.</p>"
                    + "<p style=\"color: #333; font-size: 16px;\">Best regards,<br>Group1-SE1712</p>";

            // Set HTML content to the email body
            helper.setText(htmlContent, true);

            javaMailSender.send(message);
        } catch (MessagingException e) {
            // Handle the exception (e.g., log it) as needed
            e.printStackTrace();
        }
    }
}
