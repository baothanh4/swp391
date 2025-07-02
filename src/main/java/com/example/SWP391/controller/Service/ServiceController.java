package com.example.SWP391.controller.Service;

import com.example.SWP391.DTO.EntityDTO.ServiceDTO;
import com.example.SWP391.DTO.EntityDTO.SlotDTO;
import com.example.SWP391.entity.Service;
import com.example.SWP391.entity.Slot;
import com.example.SWP391.repository.BookingRepository.ServiceRepository;
import com.example.SWP391.repository.BookingRepository.SlotRepository;
import com.example.SWP391.service.Service.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/services")
public class ServiceController {
    @Autowired
    private ServiceService serviceService;
    @Autowired private ServiceRepository serviceRepository;
    @Autowired private SlotRepository slotRepository;
    @GetMapping("/{type}")
    public ResponseEntity<?> getServicesByName(@PathVariable String type) {
        List<Service> list = serviceRepository.findByNameIn(List.of(type));
        return ResponseEntity.ok(list);
    }
    @GetMapping("/slot")
    public ResponseEntity<?> getAllSlot(){
        List<Slot> slots=slotRepository.findAll();
        return ResponseEntity.ok(slots);
    }
    @GetMapping("/service")
    public ResponseEntity<List<ServiceDTO>> getAllServices(){

        List<Service> services = serviceRepository.findAll();
        List<ServiceDTO> serviceDTOs = services.stream().map(this::convertDTO).collect(Collectors.toList());
        return ResponseEntity.ok(serviceDTOs);

    }
    @GetMapping("/service/{id}")
    public ResponseEntity<?> getServiceById(@PathVariable(name = "id") String serviceID){
        try {
            Optional<Service> services = serviceRepository.findById(serviceID);
            List<ServiceDTO> serviceDTOs = services.stream().map(this::convertDTO).collect(Collectors.toList());
            return ResponseEntity.ok(serviceDTOs);
        }catch (Exception e){
            return ResponseEntity.badRequest().body("ServiceID not found");
        }
    }
    public ServiceDTO convertDTO(Service service){
        ServiceDTO dto=new ServiceDTO();
        dto.setServiceID(service.getServiceId());
        dto.setName(service.getName());
        dto.setType(service.getType());
        dto.setCost(service.getCost());
        dto.setEstimatedTime(service.getEstimatedTime());
        dto.setExpressPrice(service.getExpressPrice());
        return dto;
    }
    public SlotDTO convertToSlotDTO(Slot slot){
        SlotDTO slotDTO=new SlotDTO();
        slotDTO.setId(slot.getId());
        slotDTO.setDate(slot.getDate());
        slotDTO.setTimeRange(slot.getTimeRange());
        slotDTO.setCurrentBooking(slot.getCurrentBooking());
        return slotDTO;
    }
}
