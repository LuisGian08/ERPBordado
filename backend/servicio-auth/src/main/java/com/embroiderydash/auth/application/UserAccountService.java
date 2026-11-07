package com.embroiderydash.auth.application;

import com.embroiderydash.auth.api.CreateUserRequest;
import com.embroiderydash.auth.api.LoginResponse;
import com.embroiderydash.auth.api.UserResponse;
import com.embroiderydash.auth.domain.Role;
import com.embroiderydash.auth.domain.UserAccount;
import com.embroiderydash.auth.domain.UserRepository;
import com.embroiderydash.auth.security.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.nio.charset.StandardCharsets;

@Service
public class UserAccountService {
    private final UserRepository users; private final PasswordEncoder encoder; private final AuthenticationManager authManager; private final JwtTokenProvider tokens;
    public UserAccountService(UserRepository users, PasswordEncoder encoder, AuthenticationManager authManager, JwtTokenProvider tokens) {
        this.users=users; this.encoder=encoder; this.authManager=authManager; this.tokens=tokens;
    }
    public LoginResponse login(String username, String password) {
        try { authManager.authenticate(new UsernamePasswordAuthenticationToken(username, password)); }
        catch (AuthenticationException e) { throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario o contraseña incorrectos"); }
        UserAccount user=users.findByUsername(username).filter(UserAccount::isEnabled).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario o contraseña incorrectos"));
        return LoginResponse.of(tokens.issue(user), tokens.getExpirationMs()/1000, user);
    }
    @Transactional
    public UserResponse createOperator(CreateUserRequest request) {
        if (users.existsByUsername(request.username())) throw new ResponseStatusException(HttpStatus.CONFLICT, "Ese nombre de usuario ya existe");
        if (request.password().getBytes(StandardCharsets.UTF_8).length > 72) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña excede los 72 bytes admitidos");
        Role role=request.role()==null?Role.OPERADOR:request.role();
        if (role==Role.ADMIN) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No se puede crear otra cuenta de administrador maestro");
        UserAccount saved=users.save(new UserAccount(request.username().trim(), request.displayName().trim(), encoder.encode(request.password()), role));
        return UserResponse.from(saved);
    }
    @Transactional(readOnly=true) public List<UserResponse> list() { return users.findAll().stream().map(UserResponse::from).toList(); }
    @Transactional(readOnly=true) public UserResponse get(String username) {
        return users.findByUsername(username).map(UserResponse::from).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }
}
