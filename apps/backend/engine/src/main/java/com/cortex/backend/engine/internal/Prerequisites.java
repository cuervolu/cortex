package com.cortex.backend.engine.internal;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Prerequisites {
  private List<PrerequisiteItem> required;
  private List<PrerequisiteItem> recommended;
}

