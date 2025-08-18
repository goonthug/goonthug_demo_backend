package com.example.goonthug_demo_backend.service;

import com.example.goonthug_demo_backend.dto.FeedbackDTO;
import com.example.goonthug_demo_backend.dto.FeedbackRequestDTO;
import com.example.goonthug_demo_backend.model.Feedback;
import com.example.goonthug_demo_backend.model.GameAssignment;
import com.example.goonthug_demo_backend.model.User;
import com.example.goonthug_demo_backend.repository.FeedbackRepository;
import com.example.goonthug_demo_backend.repository.GameAssignmentRepository;
import com.example.goonthug_demo_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private GameAssignmentRepository gameAssignmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public FeedbackDTO createFeedback(FeedbackRequestDTO request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User tester = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Тестер не найден: " + email));

        // Находим активное назначение для этого тестера и игры
        GameAssignment assignment = gameAssignmentRepository
                .findByGameIdAndTesterIdAndStatus(request.getGameId(), tester.getId(), "в работе")
                .orElseThrow(() -> new RuntimeException("У вас нет активного назначения для этой игры"));

        // Проверяем, что назначение активно
        if (!"в работе".equals(assignment.getStatus())) {
            throw new RuntimeException("Тестирование уже завершено или не активно");
        }

        // Для финального фидбека проверяем дополнительные условия
        if (request.getFeedbackType() == Feedback.FeedbackType.FINAL) {
            if (request.getRating() == null || request.getRating() < 1 || request.getRating() > 10) {
                throw new RuntimeException("Для финального фидбека необходим рейтинг от 1 до 10");
            }

            // Проверяем, что финальный фидбек еще не оставлен
            if (feedbackRepository.existsFinalFeedbackForAssignment(assignment)) {
                throw new RuntimeException("Финальный фидбек уже оставлен для этого назначения");
            }
        } else {
            // Для обычных фидбеков рейтинг не нужен, устанавливаем null если он был передан
            request.setRating(null);
        }

        Feedback feedback = new Feedback();
        feedback.setGameAssignment(assignment);
        feedback.setTester(tester);
        feedback.setComment(request.getComment());
        feedback.setFeedbackType(request.getFeedbackType());
        feedback.setRating(request.getRating());

        feedback = feedbackRepository.save(feedback);

        return new FeedbackDTO(feedback);
    }

    @Transactional
    public FeedbackDTO createFinalFeedbackAndCompleteTest(FeedbackRequestDTO request) {
        // Создаем финальный фидбек
        request.setFeedbackType(Feedback.FeedbackType.FINAL);
        FeedbackDTO finalFeedback = createFeedback(request);

        // Завершаем тестирование
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User tester = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Тестер не найден: " + email));

        GameAssignment assignment = gameAssignmentRepository
                .findByGameIdAndTesterIdAndStatus(request.getGameId(), tester.getId(), "в работе")
                .orElseThrow(() -> new RuntimeException("У вас нет активного назначения для этой игры"));

        assignment.setStatus("завершено");
        gameAssignmentRepository.save(assignment);

        return finalFeedback;
    }

    public List<FeedbackDTO> getFeedbacksForGame(Long gameId) {
        List<Feedback> feedbacks = feedbackRepository.findByGameIdOrderByCreatedAtDesc(gameId);
        return feedbacks.stream()
                .map(FeedbackDTO::new)
                .collect(Collectors.toList());
    }

    public List<FeedbackDTO> getFeedbacksForAssignment(Long assignmentId) {
        GameAssignment assignment = gameAssignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Назначение игры не найдено"));

        List<Feedback> feedbacks = feedbackRepository.findByGameAssignmentOrderByCreatedAtDesc(assignment);
        return feedbacks.stream()
                .map(FeedbackDTO::new)
                .collect(Collectors.toList());
    }

    public List<FeedbackDTO> getMyFeedbacks() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User tester = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Тестер не найден: " + email));

        List<Feedback> feedbacks = feedbackRepository.findByTesterIdOrderByCreatedAtDesc(tester.getId());
        return feedbacks.stream()
                .map(FeedbackDTO::new)
                .collect(Collectors.toList());
    }

    public boolean hasFinalFeedback(Long assignmentId) {
        GameAssignment assignment = gameAssignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Назначение игры не найдено"));

        return feedbackRepository.existsFinalFeedbackForAssignment(assignment);
    }

    public boolean hasFinalFeedbackForGame(Long gameId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User tester = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Тестер не найден: " + email));

        // Находим активное назначение для этого тестера и игры
        var assignment = gameAssignmentRepository
                .findByGameIdAndTesterIdAndStatus(gameId, tester.getId(), "в работе");

        if (assignment.isEmpty()) {
            return false; // Нет активного назначения - значит и фидбека быть не может
        }

        return feedbackRepository.existsFinalFeedbackForAssignment(assignment.get());
    }
}
