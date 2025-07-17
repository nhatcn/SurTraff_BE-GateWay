// src/main/java/com/example/config/CloudinaryConfig.java
package com.example.demo.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dh7bridgn",
                "api_key", "958111531242264",
                "api_secret", "QFTx26bP9MxnA_rzDpZUrcezwxI",
                "secure", true
        ));
    }
}
