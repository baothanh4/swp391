package com.example.SWP391.service.TestSubject;

import com.example.SWP391.DTO.AuthRequest.TestSubjectInfoRequest;
import com.example.SWP391.entity.Booking.Booking;
import com.example.SWP391.entity.TestSubjectInfo;
import com.example.SWP391.repository.BookingRepository.BookingRepository;
import com.example.SWP391.repository.TestSubject.TestSubjectInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TestSubjectInfoService {
    private final BookingRepository bookingRepository;
    private final TestSubjectInfoRepository testSubjectInfoRepository;

    public TestSubjectInfo createTestSubjectInfo(TestSubjectInfoRequest request){
        Booking booking=bookingRepository.findById(request.getBookingID()).orElseThrow(() ->new RuntimeException("Booking not found"));

        String fullname=booking.getCustomer().getFullName();

        TestSubjectInfo info=new TestSubjectInfo();
        info.setBooking(booking);
        info.setFullname(fullname);
        info.setRelationship(request.getRelationship());
        info.setBioType(request.getBioType());

        return testSubjectInfoRepository.save(info);

    }
}
