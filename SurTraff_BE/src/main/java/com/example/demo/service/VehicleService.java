package com.example.demo.service;

import com.example.demo.DTO.VehicleDTO;
import com.example.demo.model.Vehicle;
import com.example.demo.repository.VehicleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.VehicleTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    @Autowired
    private CloudinaryService cloudinaryService;

    @Transactional(readOnly = true)
    public List<VehicleDTO> getAllVehicles() {
        return vehicleRepository.findAll().stream()
                .filter(vehicle -> !Boolean.TRUE.equals(vehicle.getIsDelete()))
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public VehicleDTO getVehicleById(Long id) {
        return vehicleRepository.findById(id)
                .filter(vehicle -> !Boolean.TRUE.equals(vehicle.getIsDelete()))
                .map(this::toDTO)
                .orElse(null);
    }

    @Transactional
    public VehicleDTO createVehicle(VehicleDTO dto, MultipartFile imageFile) throws IOException {
        if (dto == null || dto.getLicensePlate() == null || dto.getLicensePlate().trim().isEmpty()) {
            throw new IllegalArgumentException("License plate is required");
        }
        Vehicle vehicle = toEntity(dto);
        vehicle.setIsDelete(false);

        // Handle image upload if provided
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = cloudinaryService.uploadImage(imageFile);
            vehicle.setImage(imageUrl);
        }

        vehicle = vehicleRepository.save(vehicle);
        return toDTO(vehicle);
    }

    @Transactional
    public VehicleDTO updateVehicle(Long id, VehicleDTO dto, MultipartFile imageFile) throws IOException {
        Optional<Vehicle> optionalVehicle = vehicleRepository.findById(id)
                .filter(vehicle -> !Boolean.TRUE.equals(vehicle.getIsDelete()));
        if (optionalVehicle.isPresent()) {
            Vehicle vehicle = optionalVehicle.get();
            updateVehicleFields(vehicle, dto, imageFile);
            vehicle = vehicleRepository.save(vehicle);
            return toDTO(vehicle);
        }
        return null;
    }

    @Transactional
    public void deleteVehicle(Long id) {
        Optional<Vehicle> optionalVehicle = vehicleRepository.findById(id);
        if (optionalVehicle.isPresent()) {
            Vehicle vehicle = optionalVehicle.get();
            vehicle.setIsDelete(true);
            vehicleRepository.save(vehicle);
        } else {
            throw new IllegalArgumentException("Vehicle not found with ID: " + id);
        }
    }

    @Transactional(readOnly = true)
    public List<VehicleDTO> getVehiclesByUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID is required");
        }
        return vehicleRepository.findByUserId(userId)
                .stream()
                .filter(vehicle -> !Boolean.TRUE.equals(vehicle.getIsDelete()))
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Helper methods
    private VehicleDTO toDTO(Vehicle vehicle) {
        if (vehicle == null) return null;
        VehicleDTO dto = new VehicleDTO();
        dto.setId(vehicle.getId());
        dto.setName(vehicle.getName());
        dto.setLicensePlate(vehicle.getLicensePlate());
        dto.setUserId(vehicle.getUser() != null ? vehicle.getUser().getId() : null);
        dto.setVehicleTypeId(vehicle.getVehicleType() != null ? vehicle.getVehicleType().getId() : null);
        dto.setColor(vehicle.getColor());
        dto.setBrand(vehicle.getBrand());
        dto.setImage(vehicle.getImage());
        dto.setIsDelete(vehicle.getIsDelete());
        return dto;
    }

    private Vehicle toEntity(VehicleDTO dto) {
        if (dto == null) return null;
        Vehicle vehicle = new Vehicle();
        vehicle.setId(dto.getId());
        vehicle.setName(dto.getName());
        vehicle.setLicensePlate(dto.getLicensePlate());
        vehicle.setColor(dto.getColor());
        vehicle.setBrand(dto.getBrand());
        vehicle.setImage(dto.getImage());
        vehicle.setIsDelete(dto.getIsDelete() != null ? dto.getIsDelete() : false);

        if (dto.getUserId() != null) {
            vehicle.setUser(userRepository.findById(dto.getUserId()).orElse(null));
        }
        if (dto.getVehicleTypeId() != null) {
            vehicle.setVehicleType(vehicleTypeRepository.findById(dto.getVehicleTypeId()).orElse(null));
        }
        return vehicle;
    }

    private void updateVehicleFields(Vehicle vehicle, VehicleDTO dto, MultipartFile imageFile) throws IOException {
        if (dto.getName() != null) vehicle.setName(dto.getName());
        if (dto.getLicensePlate() != null) vehicle.setLicensePlate(dto.getLicensePlate());
        if (dto.getColor() != null) vehicle.setColor(dto.getColor());
        if (dto.getBrand() != null) vehicle.setBrand(dto.getBrand());
        if (dto.getImage() != null) vehicle.setImage(dto.getImage());
        if (dto.getIsDelete() != null) vehicle.setIsDelete(dto.getIsDelete());

        // Handle new image upload if provided, otherwise keep existing image
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = cloudinaryService.uploadImage(imageFile);
            vehicle.setImage(imageUrl);
        }

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
    }
}