package com.example.demo.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import org.slf4j.Logger; // Import Logger
import org.slf4j.LoggerFactory; // Import LoggerFactory

@Service
public class CloudinaryService {

    // Khởi tạo Logger cho class này
    private static final Logger logger = LoggerFactory.getLogger(CloudinaryService.class);

    @Autowired
    private Cloudinary cloudinary;

    public String uploadImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            logger.warn("Attempted to upload an empty or null image file.");
            return null; // Trả về null nếu file rỗng hoặc null
        }
        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "resource_type", "image",
                    "folder", "react_uploads_images"
            ));
            String secureUrl = uploadResult.get("secure_url").toString();
            logger.info("Image uploaded successfully: {}", secureUrl);
            return secureUrl;
        } catch (IOException e) {
            logger.error("IO Error during image upload: {}", e.getMessage(), e);
            throw e; // Ném lại ngoại lệ để được xử lý ở tầng cao hơn
        } catch (Exception e) {
            logger.error("Error uploading image to Cloudinary: {}", e.getMessage(), e);
            return null; // Trả về null cho các lỗi khác
        }
    }

    public String uploadVideo(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            logger.warn("Attempted to upload an empty or null video file.");
            return null; // Trả về null nếu file rỗng hoặc null
        }

        File tempFile = null;
        try {
            // Ghi tạm vào ổ đĩa
            tempFile = File.createTempFile("temp-video", ".mp4");
            file.transferTo(tempFile);
            logger.info("Video file transferred to temporary path: {}", tempFile.getAbsolutePath());

            // Upload từ File
            Map uploadResult = cloudinary.uploader().upload(tempFile, ObjectUtils.asMap(
                    "resource_type", "video",
                    "folder", "react_uploads_videos"
            ));

            // Kiểm tra xem secure_url có tồn tại không trước khi gọi toString()
            String secureUrl = uploadResult.get("secure_url") != null ? uploadResult.get("secure_url").toString() : null;
            if (secureUrl != null) {
                logger.info("Video uploaded successfully to Cloudinary: {}", secureUrl);
            } else {
                logger.error("Cloudinary upload returned no secure_url for video. Upload result: {}", uploadResult);
            }
            return secureUrl;

        } catch (IOException e) {
            logger.error("IO Error during video file transfer or upload: {}", e.getMessage(), e);
            throw e; // Ném lại ngoại lệ để được xử lý ở tầng cao hơn
        } catch (Exception e) {
            logger.error("Error uploading video to Cloudinary: {}", e.getMessage(), e);
            return null; // Trả về null cho các lỗi khác
        } finally {
            // Xoá file tạm (nên làm)
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