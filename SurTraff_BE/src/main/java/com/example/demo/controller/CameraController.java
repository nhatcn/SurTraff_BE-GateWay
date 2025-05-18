package com.example.demo.controller;


import com.example.demo.service.CameraService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cameras")
@AllArgsConstructor
public class CameraController {
    private final CameraService cameraService;
    
   
}
