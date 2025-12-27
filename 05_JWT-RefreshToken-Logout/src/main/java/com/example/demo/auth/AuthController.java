package com.example.demo.auth;

import com.example.demo.auth.Dto.LoginResponseDto;
import com.example.demo.auth.Dto.RefreshTokenRequest;
import com.example.demo.auth.refreshToken.RefreshToken;
import com.example.demo.auth.refreshToken.RefreshTokenService;
import com.example.demo.exceptions.customHandlers.EmailAlreadyExists;
import com.example.demo.exceptions.customHandlers.ResourseNotFound;
import com.example.demo.role.Role;
import com.example.demo.role.RoleRepository;
import com.example.demo.auth.Dto.LoginRequestDto;
import com.example.demo.auth.Dto.RegisterDto;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Set;

@RestController
@RequestMapping("/api/auth")
public class AuthController{

    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final RoleRepository roleRepository;
    @Autowired
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private final AuthenticationManager authenticationManager;
    @Autowired
    private final JwtService jwtService;
    @Autowired
    private RefreshTokenService refreshTokenService;
    @Autowired
    private UserDetailsService userDetailsService;


    public AuthController(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }


    @PostMapping("/login")
    public LoginResponseDto login(@Valid @RequestBody LoginRequestDto dto){

        Authentication authentication = authenticationManager.
                authenticate(new UsernamePasswordAuthenticationToken(
                        dto.getEmail(),
                        dto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String accessToken = jwtService.generateToken(userDetails);

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(()-> new ResourseNotFound("User"));

        String refreshToken = refreshTokenService.createRefreshToken(user);

        return new LoginResponseDto(accessToken, refreshToken);

    }

    @PostMapping("/register")
    public void register(@Valid @RequestBody RegisterDto dto) throws EmailAlreadyExists, ResourseNotFound{

        if (userRepository.findByEmail(dto.getEmail()).isPresent()){
            throw new EmailAlreadyExists( dto.getEmail());
        }

        Role defaultRole = roleRepository.findByName("ROLE_USER").orElseThrow(() ->
                new ResourseNotFound("User Role"));

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRoles(Set.of(defaultRole));

        userRepository.save(user);
    }

    @PostMapping("/refresh")
    public LoginResponseDto refresh(@RequestBody RefreshTokenRequest request){

        RefreshToken oldToken = refreshTokenService.validateRefreshToken(request.getRefreshToken());

        String email = oldToken.getUser().getEmail();
        UserDetails user = userDetailsService.loadUserByUsername(email);

        String newAccessToken = jwtService.generateToken(user);

        String newRefreshToken = refreshTokenService.rotateRefreshToken(oldToken);

        return new LoginResponseDto(newAccessToken,newRefreshToken);
    }

    @PostMapping("/logout")
    public void logout() throws ResourseNotFound{

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourseNotFound(email));

        refreshTokenService.revokeAllUserTokens(user.getId());
    }
}
