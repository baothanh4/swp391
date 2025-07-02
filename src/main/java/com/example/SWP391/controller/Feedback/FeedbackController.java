package com.example.SWP391.controller.Feedback;

import com.example.SWP391.DTO.EntityDTO.FeedbackDTO;
import com.example.SWP391.entity.Feedback;
import com.example.SWP391.repository.BookingRepository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/api/feedback")
@RestController
public class FeedbackController {
    @Autowired FeedbackRepository feedbackRepository;
    @GetMapping("/feedbacks")
    public ResponseEntity<?> getAllFeedback(){
        List<Feedback> feedbacks=feedbackRepository.findAll();
        List<FeedbackDTO> feedbackDTOS=feedbacks.stream().map(this::covertIntoFeedbackDTO).collect(Collectors.toList());
        return ResponseEntity.ok(feedbackDTOS);
    }
    public FeedbackDTO covertIntoFeedbackDTO(Feedback feedback){
        FeedbackDTO feedbackDTO=new FeedbackDTO();
        feedbackDTO.setTitle(feedback.getTitle());
        feedbackDTO.setContent(feedback.getContent());
        feedbackDTO.setRating(feedback.getRating());
        feedbackDTO.setCreateAt(feedback.getCreateAt());
        return feedbackDTO;
    }
}
