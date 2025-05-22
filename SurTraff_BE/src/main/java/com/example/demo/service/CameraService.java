package com.example.demo.service;

import com.example.demo.model.Camera;
import com.example.demo.repository.CameraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CameraService {
    @Autowired
    private CameraRepository cameraRepository;


    public List<Camera> getAllCameras() {
        return cameraRepository.findAll();
    }

    public Optional<Camera> getCameraById(Long id) {
        return cameraRepository.findById(id);
    }

    @Transactional
    public Camera createCamera(Camera camera) {
        return cameraRepository.save(camera);
    }

    @Transactional
    public Camera updateCamera(Long id, Camera cameraDetails) {
        Camera camera = cameraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Camera not found"));
        
        camera.setName(cameraDetails.getName());
        camera.setLocation(cameraDetails.getLocation());
//        camera.setLocationAddress(cameraDetails.getLocationAddress());
        camera.setStreamUrl(cameraDetails.getStreamUrl());
        camera.setThumbnail(cameraDetails.getThumbnail());
        
        return cameraRepository.save(camera);
    }

    @Transactional
    public void deleteCamera(Long id) {
        if (!cameraRepository.existsById(id)) {
            throw new RuntimeException("Camera not found");
        }
        cameraRepository.deleteById(id);
    }


}