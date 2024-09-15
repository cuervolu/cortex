package com.cortex.backend.entities.user;

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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.security.Principal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Table(name = "_user")
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
  
  private String avatar;

  @Column(name = "date_of_birth")
  private LocalDate dateOfBirth;

  @Enumerated(EnumType.STRING)
  @Column(name = "gender")
  private Gender gender;

  @Column(name = "country_code", length = 2)
  private String countryCode;

  @ManyToMany(fetch = FetchType.EAGER)
  private List<Role> roles;
  
  

  @Override
  public String getName() {
    return email;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return this.roles.stream()
        .map(r -> new SimpleGrantedAuthority(r.getName()))
        .toList();
  }
  public String getFullName() {
    return firstName + " " + lastName;
  }
}
