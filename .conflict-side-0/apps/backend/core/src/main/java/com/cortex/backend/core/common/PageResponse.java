package com.cortex.backend.core.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A generic class representing a paginated response. This class is used to wrap paginated data
 * along with metadata about the pagination.
 *
 * @param <T> The type of elements in the content list
 *
 *            <p>Example usage:
 *            <pre>
 *            {@code
 *            @GetMapping
 *            @Operation(summary = "Get all roadmaps", description = "Retrieves a list of all roadmaps")
 *            @ApiResponse(responseCode = "200", description = "Successful operation",
 *                content = @Content(schema = @Schema(implementation = RoadmapResponse.class)))
 *            public ResponseEntity<PageResponse<RoadmapResponse>> getAllRoadmaps(
 *                @RequestParam(name = "page", defaultValue = "0") int page,
 *                @RequestParam(name = "size", defaultValue = "10") int size
 *            ) {
 *              return ResponseEntity.ok(roadmapService.getAllRoadmaps(page, size));
 *            }
 *
 *            // In the service:
 *            @Override
 *            @Transactional(readOnly = true)
 *            public PageResponse<RoadmapResponse> getAllRoadmaps(int page, int size) {
 *              Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
 *              Page<Roadmap> roadmaps = roadmapRepository.findAllPublishedRoadmaps(pageable);
 *
 *              List<RoadmapResponse> response = roadmaps.stream()
 *                  .map(roadmapMapper::toRoadmapResponse)
 *                  .toList();
 *
 *              return new PageResponse<>(
 *                  response, roadmaps.getNumber(), roadmaps.getSize(), roadmaps.getTotalElements(),
 *                  roadmaps.getTotalPages(), roadmaps.isFirst(), roadmaps.isLast()
 *              );
 *            }
 *            }
 *            </pre>
 *           
 * @version 1.0
 * @author cuervolu 
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageResponse<T> {

  /**
   * The list of items in the current page.
   */
  private List<T> content;

  /**
   * The current page number (0-indexed).
   */
  private int number;

  /**
   * The size of the page (number of items per page).
   */
  private int size;

  /**
   * The total number of elements across all pages.
   */
  @JsonProperty("total_elements")
  private long totalElements;

  /**
   * The total number of pages.
   */
  @JsonProperty("total_pages")
  private int totalPages;

  /**
   * Indicates whether this is the first page.
   */
  private boolean first;

  /**
   * Indicates whether this is the last page.
   */
  private boolean last;
}