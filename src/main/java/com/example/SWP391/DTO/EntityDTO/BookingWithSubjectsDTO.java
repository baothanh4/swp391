package com.example.SWP391.DTO.EntityDTO;

import lombok.Data;

import java.util.List;

@Data
public class BookingWithSubjectsDTO {
    private BookingDTO bookingDTO;
    private List<TestSubjectInfoDTO> subject;
}
