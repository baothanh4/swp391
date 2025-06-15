package com.example.SWP391.controller.Service;

import com.example.SWP391.DTO.EntityDTO.BookingDTO;
import com.example.SWP391.DTO.EntityDTO.ServiceDTO;
import com.example.SWP391.entity.Booking;
import com.example.SWP391.entity.Service;
import com.example.SWP391.repository.BookingRepository.ServiceRepository;
import com.example.SWP391.service.Service.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/services")
public class ServiceController {
    @Autowired
    private ServiceService serviceService;
    @Autowired private ServiceRepository serviceRepository;
    @GetMapping("/{name}")
    public ResponseEntity<?> getServicesByName(@PathVariable String name) {
        List<Service> list = serviceRepository.findByNameIn(List.of(name));
        return ResponseEntity.ok(list);
    }
    @GetMapping("/service")
    public ResponseEntity<List<ServiceDTO>> getAllServices(){

        List<Service> services = serviceRepository.findAll();
        List<ServiceDTO> serviceDTOs = services.stream().map(this::convertDTO).collect(Collectors.toList());
        return ResponseEntity.ok(serviceDTOs);

    }
    public ServiceDTO convertDTO(Service service){
        ServiceDTO dto=new ServiceDTO();
        dto.setServiceID(service.getServiceId());
        dto.setName(service.getName());
        dto.setType(service.getType());
        dto.setCost(service.getCost());
        dto.setEstimatedTime(service.getEstimatedTime());
        dto.setExpressService(service.getExpressService());
        return dto;
    }
}
