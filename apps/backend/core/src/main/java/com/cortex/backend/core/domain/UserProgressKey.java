package com.cortex.backend.core.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class UserProgressKey implements Serializable {
  @Column(name= "user_id")
  private Long userId;
  
  @Column(name= "entity_id")
  private Long entityId;

  @Enumerated(EnumType.STRING) 
  @Column(name = "entity_type")
  private EntityType entityType;

}
