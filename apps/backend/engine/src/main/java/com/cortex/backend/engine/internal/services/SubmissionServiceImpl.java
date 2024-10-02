package com.cortex.backend.engine.internal.services;

import com.cortex.backend.core.domain.Exercise;
import com.cortex.backend.core.domain.Language;
import com.cortex.backend.core.domain.Solution;
import com.cortex.backend.core.domain.Submission;
import com.cortex.backend.core.domain.User;
import com.cortex.backend.engine.api.ExerciseRepository;
import com.cortex.backend.engine.api.LanguageRepository;
import com.cortex.backend.engine.api.SolutionRepository;
import com.cortex.backend.engine.api.SubmissionRepository;
import com.cortex.backend.engine.api.SubmissionService;
import com.cortex.backend.engine.api.dto.CodeExecutionRequest;
import com.cortex.backend.engine.api.dto.CodeExecutionResult;
import com.cortex.backend.engine.api.dto.SubmissionResponse;
import com.cortex.backend.engine.internal.mappers.SubmissionMapper;
import com.cortex.backend.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class SubmissionServiceImpl implements SubmissionService {

  private final SubmissionRepository submissionRepository;
  private final SolutionRepository solutionRepository;
  private final ExerciseRepository exerciseRepository;
  private final LanguageRepository languageRepository;
  private final UserRepository userRepository;
  private final SubmissionMapper submissionMapper;

  @Override
  @Transactional
  public SubmissionResponse createSubmission(CodeExecutionRequest request, Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    Exercise exercise = exerciseRepository.findById(request.exerciseId())
        .orElseThrow(() -> new EntityNotFoundException("Exercise not found"));
    Language language = languageRepository.findByName(request.language())
        .orElseThrow(() -> new EntityNotFoundException("Language not found"));

    Solution solution = solutionRepository.findByUserAndExercise(user, exercise)
        .orElseGet(() -> {
          Solution newSolution = Solution.builder()
              .user(user)
              .exercise(exercise)
              .status(0L)  // Pending status
              .pointsEarned(0)
              .submissions(new HashSet<>())
              .build();
          return solutionRepository.save(newSolution);
        });

    Submission submission = Submission.builder()
        .code(new String(Base64.getDecoder().decode(request.code())))
        .language(language)
        .solution(solution)
        .build();

    solution.getSubmissions().add(submission);
    submissionRepository.save(submission);

    return submissionMapper.submissionToSubmissionResponse(submission);
  }
  @Override
  @Transactional
  public void updateSubmissionWithResult(Long submissionId, CodeExecutionResult result) {
    Submission submission = submissionRepository.findById(submissionId)
        .orElseThrow(() -> new IllegalArgumentException("Submission not found"));

    submission.setStdin(result.getStdout());
    submission.setExpectedOutput(result.getStderr());

    Solution solution = submission.getSolution();
    if (result.isSuccess()) {
      solution.setStatus(1L);  // Completed status
      solution.setPointsEarned(solution.getExercise().getPoints());
    } else {
      solution.setStatus(2L);  // Failed status
    }

    submissionRepository.save(submission);
    solutionRepository.save(solution);
  }

  @Override
  @Transactional(readOnly = true)
  public SubmissionResponse getSubmission(Long submissionId) {
    Submission submission = submissionRepository.findById(submissionId)
        .orElseThrow(() -> new IllegalArgumentException("Submission not found"));
    return submissionMapper.submissionToSubmissionResponse(submission);
  }
}