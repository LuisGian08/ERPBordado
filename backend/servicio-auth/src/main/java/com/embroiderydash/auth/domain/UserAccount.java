package com.embroiderydash.auth.domain;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "usuarios")
public class UserAccount {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, unique = true, length = 80) private String username;
    @Column(name = "display_name", nullable = false, length = 150) private String displayName;
    @Column(name = "password_hash", nullable = false, length = 100) private String passwordHash;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private Role role;
    @Column(nullable = false) private boolean enabled = true;
    @Column(name = "created_at", nullable = false) private OffsetDateTime createdAt;

    protected UserAccount() {}
    public UserAccount(String username, String displayName, String passwordHash, Role role) {
        this.username = username; this.displayName = displayName; this.passwordHash = passwordHash; this.role = role; this.enabled = true;
    }
    @PrePersist void onCreate() { if (createdAt == null) createdAt = OffsetDateTime.now(); }
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getDisplayName() { return displayName; }
    public String getPasswordHash() { return passwordHash; }
    public Role getRole() { return role; }
    public boolean isEnabled() { return enabled; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
}
