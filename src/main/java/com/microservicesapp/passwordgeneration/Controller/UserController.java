package com.microservicesapp.passwordgeneration.Controller;

import com.microservicesapp.passwordgeneration.Exception.UserAlreadyExistsException;
import com.microservicesapp.passwordgeneration.Service.UserService;
import com.microservicesapp.passwordgeneration.entity.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        try {
            String savedUser = userService.registerUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }
    @PostMapping("/login")
    public ResponseEntity<String> loginUsers(@RequestBody User user, HttpServletRequest request, HttpServletResponse response) {
        try {
            String msg = userService.LoginUser(user);
            User user1 = userService.getUserByUsername(user.getUsername());
            HttpSession session = request.getSession(true);
            session.setAttribute("user", user1);
            session.setMaxInactiveInterval(2 * 60);

            Authentication auth = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
            SecurityContextHolder.getContext().setAuthentication(auth);

            // Set a secure cookie for session management
            Cookie cookie = new Cookie("JSESSIONID", session.getId());
            cookie.setHttpOnly(true);
            cookie.setSecure(false); // Set to true if you're using HTTPS
            cookie.setPath("/");
            response.addCookie(cookie);
            return ResponseEntity.status(HttpStatus.OK).body(msg);
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            User user = (User) session.getAttribute("user");
            userService.LogoutUser(user);
            session.invalidate();
        }
        // Clear the security context
        SecurityContextHolder.clearContext();
        // Clear the JSESSIONID cookie
        Cookie cookie = new Cookie("JSESSIONID", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // Set to true if you're using HTTPS
        cookie.setPath("/");
        cookie.setMaxAge(0); // Set the cookie to expire immediately
        response.addCookie(cookie);

        return ResponseEntity.ok("User logged out successfully.");
    }

    @GetMapping("/data")
    public ResponseEntity<?> fetchUserData(HttpServletRequest request, HttpServletResponse response) {

        HttpSession session = request.getSession(false);
        if (session == null || isSessionExpired(session)) {
            Cookie cookie = new Cookie("JSESSIONID", null);
            cookie.setHttpOnly(true);
            cookie.setSecure(false); // Set to true if you're using HTTPS
            cookie.setPath("/");
            cookie.setMaxAge(0); // Set the cookie to expire immediately
            response.addCookie(cookie);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)

                    .header("Location", "http://localhost:5173/login")
                    .build();
        }

        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .header("Location", "http://localhost:5173/login")
                    .build();
        }

        return ResponseEntity.ok(user);
    }
    private boolean isSessionExpired(HttpSession session) {
        long sessionCreationTime = session.getCreationTime();
        long currentTime = System.currentTimeMillis();
        return (currentTime - sessionCreationTime) > 2 * 60 * 1000;
    }
    @GetMapping
    public ResponseEntity<List<User>> GetUsers(){
        return ResponseEntity.status(HttpStatus.OK).body(userService.getAllUsers());
    }
    @DeleteMapping("/delete-all")
    public void deleteAl(){
        userService.DeleteAll();
    }
}
