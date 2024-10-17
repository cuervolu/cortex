package com.cortex.backend.education.mentorship.api.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Data transfer object for adding a note to a mentorship")
public record MentorshipNote(
    @Schema(description = "Content of the note in Markdown format")
        @NotNull(message = "Content is required")
        @NotBlank(message = "Content cannot be blank")
    String content
) {}
