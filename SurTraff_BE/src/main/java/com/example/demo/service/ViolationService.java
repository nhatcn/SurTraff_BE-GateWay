package com.example.demo.service;
import com.example.demo.repository.CameraRepository;
import com.example.demo.repository.ViolationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ViolationService {
    private final ViolationRepository violationRepository;
    private final CameraRepository cameraRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public ViolationService(ViolationRepository violationRepository, CameraRepository cameraRepository) {
        this.violationRepository = violationRepository;
        this.cameraRepository = cameraRepository;
    }


}