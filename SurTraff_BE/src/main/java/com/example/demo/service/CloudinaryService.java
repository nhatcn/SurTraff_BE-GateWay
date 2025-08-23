package com.example.demo.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CloudinaryService {

    private static final Logger logger = LoggerFactory.getLogger(CloudinaryService.class);

    @Autowired
    private Cloudinary cloudinary;

    public String uploadImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            logger.warn("Attempted to upload an empty or null image file.");
            throw new IllegalArgumentException("Image file is empty or null.");
        }

        // Validate content type
        String contentType = file.getContentType();
        List<String> allowedImageTypes = Arrays.asList("image/jpeg", "image/png", "image/gif");
        if (!allowedImageTypes.contains(contentType)) {
            logger.error("Unsupported image format: {}", contentType);
            throw new IllegalArgumentException("Only JPEG, PNG, or GIF images are allowed.");
        }

        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "resource_type", "image",
                    "folder", "react_uploads_images"
            ));
            String secureUrl = uploadResult.get("secure_url") != null ? uploadResult.get("secure_url").toString() : null;
            if (secureUrl == null) {
                logger.error("Cloudinary upload returned no secure_url for image. Upload result: {}", uploadResult);
                throw new RuntimeException("Failed to obtain image URL.");
            }
            logger.info("Image uploaded successfully: {}", secureUrl);
            return secureUrl;
        } catch (IOException e) {
            logger.error("IO Error during image upload: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Error uploading image to Cloudinary: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to upload image.", e);
        }
    }

    public String uploadVideo(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            logger.warn("Attempted to upload an empty or null video file.");
            throw new IllegalArgumentException("Video file is empty or null.");
        }

        String contentType = file.getContentType();
        List<String> allowedVideoTypes = Arrays.asList("video/mp4", "video/mpeg", "video/quicktime");
        if (!allowedVideoTypes.contains(contentType)) {
            logger.error("Unsupported video format: {}", contentType);
            throw new IllegalArgumentException("Only MP4, MPEG, or QuickTime videos are allowed.");
        }

        if (file.getSize() > 100 * 1024 * 1024) {
            logger.error("Video file size exceeds limit: {} bytes", file.getSize());
            throw new IllegalArgumentException("Video file size exceeds 100MB limit.");
        }

        File tempFile = null;
        try {
            tempFile = File.createTempFile("temp-video", ".mp4");
            file.transferTo(tempFile);
            logger.info("Video file transferred to temporary path: {}", tempFile.getAbsolutePath());

            Map uploadResult = cloudinary.uploader().upload(
                    tempFile,
                    ObjectUtils.asMap(
                            "resource_type", "video",
                            "folder", "react_uploads_videos",
                            "eager", Arrays.asList(
                                    // Transformation 1: resize + encode
                                    new Transformation<>()
                                            .width(1280).height(720).crop("limit")
                                            .videoCodec("h264")
                                            .audioCodec("aac")
                                            .quality("auto")
                                            .fetchFormat("mp4"),

                                    // Transformation 2: chỉ có streaming_profile
                                    new Transformation<>()
                                            .streamingProfile("hd")
                            ),
                            "eager_async", false
                    )
            );


            logger.info("Upload result: {}", uploadResult);

            String secureUrl = null;
            if (uploadResult.containsKey("eager")) {
                List<Map> eagerList = (List<Map>) uploadResult.get("eager");
                if (!eagerList.isEmpty() && eagerList.get(0).get("secure_url") != null) {
                    secureUrl = eagerList.get(0).get("secure_url").toString();
                }
            }
            if (secureUrl == null && uploadResult.get("secure_url") != null) {
                secureUrl = uploadResult.get("secure_url").toString();
            }

            if (secureUrl == null) {
                throw new RuntimeException("Failed to obtain playable video URL.");
            }

            logger.info("Video uploaded successfully: {}", secureUrl);
            return secureUrl;

        } finally {
            if (tempFile != null && tempFile.exists()) {
                if (tempFile.delete()) {
                    logger.info("Temporary video file deleted: {}", tempFile.getAbsolutePath());
                } else {
                    logger.warn("Failed to delete temporary video file: {}", tempFile.getAbsolutePath());
                }
            }
        }
    }


}