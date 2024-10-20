package com.cortex.backend.education.progress.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LessonCompletedEvent {
  private final Long lessonId;
  private final Long userId;

}
