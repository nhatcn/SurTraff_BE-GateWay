package com.example.demo.service;

import com.example.demo.DTO.VehicleDTO;
import com.example.demo.model.Vehicle;
import com.example.demo.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.VehicleTypeRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VehicleTypeRepository vehicleTypeRepository;

    public List<VehicleDTO> getAllVehicles() {
        return vehicleRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public VehicleDTO getVehicleById(Long id) {
        Optional<Vehicle> vehicle = vehicleRepository.findById(id);
        return vehicle.map(this::toDTO).orElse(null);
    }

    public VehicleDTO createVehicle(VehicleDTO dto) {
        Vehicle vehicle = toEntity(dto);
        vehicle = vehicleRepository.save(vehicle);
        return toDTO(vehicle);
    }

    public VehicleDTO updateVehicle(Long id, VehicleDTO dto) {
        Optional<Vehicle> optionalVehicle = vehicleRepository.findById(id);
        if (optionalVehicle.isPresent()) {
            Vehicle vehicle = optionalVehicle.get();
            vehicle.setName(dto.getName());
            vehicle.setLicensePlate(dto.getLicensePlate());
            vehicle.setColor(dto.getColor());
            vehicle.setBrand(dto.getBrand());

            // Update User
            if (dto.getUserId() != null) {
                vehicle.setUser(userRepository.findById(dto.getUserId()).orElse(null));
            } else {
                vehicle.setUser(null);
            }
            // Update VehicleType
            if (dto.getVehicleTypeId() != null) {
                vehicle.setVehicleType(vehicleTypeRepository.findById(dto.getVehicleTypeId()).orElse(null));
            } else {
                vehicle.setVehicleType(null);
            }

            vehicle = vehicleRepository.save(vehicle);
            return toDTO(vehicle);
        }
        return null;
    }

    public void deleteVehicle(Long id) {
        vehicleRepository.deleteById(id);
    }

    // Helper methods
    private VehicleDTO toDTO(Vehicle vehicle) {
        VehicleDTO dto = new VehicleDTO();
        dto.setId(vehicle.getId());
        dto.setName(vehicle.getName());
        dto.setLicensePlate(vehicle.getLicensePlate());
        dto.setUserId(vehicle.getUser() != null ? vehicle.getUser().getId() : null);
        dto.setVehicleTypeId(vehicle.getVehicleType() != null ? vehicle.getVehicleType().getId() : null);
        dto.setColor(vehicle.getColor());
        dto.setBrand(vehicle.getBrand());
        return dto;
    }

    private Vehicle toEntity(VehicleDTO dto) {
        Vehicle vehicle = new Vehicle();
        vehicle.setId(dto.getId());
        vehicle.setName(dto.getName());
        vehicle.setLicensePlate(dto.getLicensePlate());
        vehicle.setColor(dto.getColor());
        vehicle.setBrand(dto.getBrand());

        if (dto.getUserId() != null) {
            vehicle.setUser(userRepository.findById(dto.getUserId()).orElse(null));
        } else {
            vehicle.setUser(null);
        }
        if (dto.getVehicleTypeId() != null) {
            vehicle.setVehicleType(vehicleTypeRepository.findById(dto.getVehicleTypeId()).orElse(null));
        } else {
            vehicle.setVehicleType(null);
        }
        return vehicle;
    }

    public List<VehicleDTO> getVehiclesByUserId(Long userId) {
        return vehicleRepository.findByUserId(userId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}