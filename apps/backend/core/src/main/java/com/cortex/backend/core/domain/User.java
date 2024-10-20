package com.cortex.backend.core.domain;

import com.cortex.backend.core.common.types.Gender;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Table(name = "_user", indexes = {
    @Index(name = "idx_user_username", columnList = "username"),
    @Index(name = "idx_user_email", columnList = "email"),
    @Index(name = "idx_user_external_id", columnList = "external_id"),
    @Index(name = "idx_user_country_code", columnList = "country_code"),
    @Index(name = "idx_user_first_name", columnList = "first_name"),
    @Index(name = "idx_user_last_name", columnList = "last_name")
})
@Entity
@EntityListeners(AuditingEntityListener.class) 
public class User implements UserDetails, Principal {
  @Id @GeneratedValue private Long id;
  
  @Column(unique = true)
  private String username;
  
  @Column(unique = true)
  private String email;

  @Column(name = "first_name")
  private String firstName;

  @Column(name = "last_name")
  private String lastName;

  private String password;
  
  @Column(name = "account_locked")
  private boolean accountLocked;
  
  private boolean enabled;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "user_providers", joinColumns = @JoinColumn(name = "user_id"))
  @Column(name = "provider")
  private Set<String> providers;

  @Column(name = "external_id")
  private String externalId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "avatar_id")
  private Media avatar;

  @Column(name = "date_of_birth")
  private LocalDate dateOfBirth;

  @Enumerated(EnumType.STRING)
  @Column(name = "gender")
  private Gender gender;

  @Column(name = "country_code", length = 2)
  private String countryCode;

  @ManyToMany(fetch = FetchType.EAGER)
  private List<Role> roles;

  @Column(name = "last_login")
  private LocalDate lastLogin;

  @Column(name = "login_streak")
  private int loginStreak = 0;

  @Column(name = "total_logins")
  private int totalLogins = 0;

  @Override
  public String getName() {
    return email;
  }

  @Column(name = "created_at", nullable = false, updatable = false)
  @CreatedDate
  private LocalDateTime createdAt;

  @Column(name = "updated_at", insertable = false)
  @LastModifiedDate
  private LocalDateTime updatedAt;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return this.roles.stream()
        .map(r -> new SimpleGrantedAuthority("ROLE_" + r.getName()))
        .toList();
  }
  
  public String getFullName() {
    return firstName + " " + lastName;
  }

  public void updateLoginStats() {
    LocalDate currentDate = LocalDate.now();
    this.totalLogins++;

    if (this.lastLogin != null) {
      if (this.lastLogin.plusDays(1).isEqual(currentDate)) {
        // The user has logged in on consecutive days
        this.loginStreak++;
      } else if (this.lastLogin.isBefore(currentDate)) {
        // The user has logged in after at least one day of inactivity.
        this.loginStreak = 1;
      }
      // If it is connected several times in the same day, it does not affect the streak.
    } else {
      // First user connection
      this.loginStreak = 1;
    }

    this.lastLogin = currentDate;
  }
}
