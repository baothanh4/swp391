package com.example.SWP391.service.Service;

import com.example.SWP391.DTO.EntityDTO.BookingDTO;
import com.example.SWP391.DTO.EntityDTO.ServiceDTO;
import com.example.SWP391.entity.Service;
import com.example.SWP391.repository.BookingRepository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.util.List;

@org.springframework.stereotype.Service
public class ServiceService {
    @Autowired
    private ServiceRepository serviceRepository;

    public Service updateCost(String serviceID,float newCost){
        Service service=serviceRepository.findById(serviceID).orElseThrow(() -> new RuntimeException("ServiceID not found"));
        service.setCost(newCost);
        return serviceRepository.save(service);
    }
    public List<Service> getLegalAndNonLegalServices() {
        return serviceRepository.findByNameIn(List.of("Legal", "Non-Legal"));
    }
//    public ServiceDTO convertDTO(Service service){
//        ServiceDTO dto=new ServiceDTO();
//        dto.setServiceID(service.getServiceId());
//        dto.setName(service.getName());
//        dto.setType(service.getType());
//        dto.setCost(service.getCost());
//        dto.setEstimatedTime(service.getEstimatedTime());
//        dto.setExpressService(service.getExpressService());
//        return dto;
//    }



}
