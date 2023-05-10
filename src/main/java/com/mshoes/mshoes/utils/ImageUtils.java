package com.mshoes.mshoes.utils;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
public class ImageUtils {
    private static final String CLOUD_NAME = "ddkt7qyd9";
    private static final String API_KEY = "463683734549674";
    private static final String API_SECRET = "-lTTtRnvL4wq3QThqBrCnyUoaIU";
    public String saveImage(MultipartFile file){
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", CLOUD_NAME);
        config.put("api_key", API_KEY);
        config.put("api_secret", API_SECRET);
        Cloudinary cloudinary = new Cloudinary(config);
        String publicId = FilenameUtils.getBaseName(file.getOriginalFilename());

        // Tải lên
        try {
            File uploadedFile = convertMultipartFileToFile(file);
            cloudinary.uploader().upload(uploadedFile, ObjectUtils.asMap("public_id", publicId, "folder", "upload_mshoes"));
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }

        return cloudinary.url().transformation(new Transformation().crop("fill")).generate("upload_mshoes/"+publicId);
    }

    private File convertMultipartFileToFile(MultipartFile file) throws IOException {
        File convertedFile = new File("D:/DATN2023/src/main/resources/static/assets/images/uploads", Objects.requireNonNull(file.getOriginalFilename()));
        FileOutputStream fos = new FileOutputStream(convertedFile);
        fos.write(file.getBytes());
        fos.close();
        return convertedFile;
    }

}
