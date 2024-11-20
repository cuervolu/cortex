package com.cortex.backend.engine.internal;

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
public class ExerciseConfig {
  private String title;
  private MetaData meta;
  private ContentData content;
  private Prerequisites prerequisites;
  private TagsData tags;
}