package com.cortex.backend.user.api;

import com.cortex.backend.user.api.dto.ChangePasswordRequest;
import com.cortex.backend.user.api.dto.UpdateProfileRequest;
import com.cortex.backend.user.api.dto.UserResponse;
import com.cortex.backend.core.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "User", description = "Endpoints for user management and profile operations")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

  private final UserService userService;

  @GetMapping("/me")
  @Operation(summary = "Get current user info", description = "Retrieves information about the currently authenticated user")
  @ApiResponse(responseCode = "200", description = "User information retrieved successfully",
      content = @Content(schema = @Schema(implementation = UserResponse.class)))
  public ResponseEntity<UserResponse> getUserInfo(Authentication authentication) {
    User user = (User) authentication.getPrincipal();
    return ResponseEntity.ok(userService.getUserById(user.getId()).orElseThrow());
  }

  @PatchMapping("/me")
  @Operation(summary = "Update user profile", description = "Updates the profile of the currently authenticated user")
  @ApiResponse(responseCode = "200", description = "Profile updated successfully",
      content = @Content(schema = @Schema(implementation = UserResponse.class)))
  public ResponseEntity<UserResponse> updateProfile(
      @ModelAttribute @Valid UpdateProfileRequest request,
      Authentication authentication) throws IOException {
    User user = (User) authentication.getPrincipal();
    UserResponse updatedUser = userService.updateProfile(user.getId(), request);
    return ResponseEntity.ok(updatedUser);
  }

  @PatchMapping("/change-password")
  @Operation(summary = "Change user password", description = "Changes the password of the currently authenticated user")
  @ApiResponse(responseCode = "200", description = "Password changed successfully")
  public ResponseEntity<?> changePassword(
      @RequestBody @Valid ChangePasswordRequest changePasswordRequest,
      Authentication authentication) {
    userService.changePassword(changePasswordRequest, authentication);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/me")
  @Operation(summary = "Delete user account", description = "Deletes the account of the currently authenticated user")
  @ApiResponse(responseCode = "200", description = "Account deleted successfully")
  public ResponseEntity<?> deleteAccount(Authentication authentication) {
    User user = (User) authentication.getPrincipal();
    userService.deleteUser(user.getId());
    return ResponseEntity.ok().build();
  }

  @PostMapping("/{userId}/re-enable")
  @PreAuthorize("hasRole('ADMIN')")
  @Operation(summary = "Re-enable user account", description = "Re-enables a previously disabled user account (Admin only)")
  @ApiResponse(responseCode = "200", description = "User account re-enabled successfully",
      content = @Content(schema = @Schema(implementation = UserResponse.class)))
  public ResponseEntity<UserResponse> reEnableUser(
      @Parameter(description = "ID of the user to re-enable") @PathVariable Long userId) {
    UserResponse reEnabledUser = userService.reEnableUser(userId);
    return ResponseEntity.ok(reEnabledUser);
  }

  @GetMapping("/all")
  @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
  @Operation(summary = "Get all users", description = "Retrieves a list of all users (Moderator and Admin only)")
  @ApiResponse(responseCode = "200", description = "List of users retrieved successfully",
      content = @Content(schema = @Schema(implementation = UserResponse.class)))
  public ResponseEntity<List<UserResponse>> getAllUsers() {
    return ResponseEntity.ok(userService.getAllUsers());
  }

  @PutMapping("/{userId}/roles")
  @PreAuthorize("hasRole('ADMIN')")
  @Operation(summary = "Update user roles", description = "Updates the roles of a specified user (Admin only)")
  @ApiResponse(responseCode = "200", description = "User roles updated successfully",
      content = @Content(schema = @Schema(implementation = UserResponse.class)))
  public ResponseEntity<UserResponse> updateUserRoles(
      @Parameter(description = "ID of the user whose roles are to be updated") @PathVariable Long userId,
      @RequestBody List<String> roleNames) {
    UserResponse updatedUser = userService.updateUserRoles(userId, roleNames);
    return ResponseEntity.ok(updatedUser);
  }
}