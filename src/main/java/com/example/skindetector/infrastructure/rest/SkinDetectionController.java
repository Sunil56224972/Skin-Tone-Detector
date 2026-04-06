package com.example.skindetector.infrastructure.rest;

import com.example.skindetector.application.DetectSkinUseCase;
import com.example.skindetector.domain.SkinDetectionResult;
import com.example.skindetector.infrastructure.exception.InvalidFileException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Enterprise-grade Skin Detection Controller.
 * Specialized endpoint for skin processing with strict validation and error handling.
 */
@RestController
@RequestMapping("/api/v1/processor")
@CrossOrigin("*")
@RequiredArgsConstructor
@Slf4j
public class SkinDetectionController {

    private final DetectSkinUseCase detectSkinUseCase;
    private final com.example.skindetector.infrastructure.ai.GeminiService geminiService;
    private static final List<String> ALLOWED_CONTENT_TYPES = List.of(
            MediaType.IMAGE_JPEG_VALUE, 
            MediaType.IMAGE_PNG_VALUE
    );

    @GetMapping("/analyze-copilot")
    public ResponseEntity<Map<String, String>> analyzeCopilot(
            @RequestParam("percentage") double percentage,
            @RequestParam(value = "strategy", defaultValue = "bayesian") String strategy) {
        
        String analysis = geminiService.analyzeSkinTone(percentage, strategy);
        Map<String, String> response = Map.of("analysis", analysis);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/detect-skin", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> detectSkin(
            @RequestParam("image") MultipartFile file,
            @RequestParam(value = "format", defaultValue = "overlay") String format,
            @RequestParam(value = "strategy", required = false) String strategy) throws IOException {

        validateFile(file);

        BufferedImage inputImage = ImageIO.read(file.getInputStream());
        if (inputImage == null) {
            throw new InvalidFileException("The provided file is not a valid image format.");
        }

        SkinDetectionResult result = detectSkinUseCase.execute(inputImage, strategy);

        byte[] responseBytes;
        if ("mask".equalsIgnoreCase(format)) {
            responseBytes = createBinaryMaskImage(result.getBinaryMask());
        } else {
            responseBytes = imageToBytes(result.getHighlightedImage());
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        headers.setContentLength(responseBytes.length);
        headers.add("X-Skin-Percentage", String.format("%.2f", result.getSkinPercentage()));

        log.info("Detection complete | Strategy: {} | Skin percentage: {}% | Path: /detect-skin", 
            strategy == null ? "default" : strategy, result.getSkinPercentage());

        return new ResponseEntity<>(responseBytes, headers, HttpStatus.OK);
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new InvalidFileException("No image data provided. Please upload a JPEG or PNG file.");
        }
        
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new InvalidFileException("Unsupported file type. Only image/jpeg and image/png are allowed.");
        }
    }

    private byte[] imageToBytes(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return baos.toByteArray();
    }

    private byte[] createBinaryMaskImage(boolean[][] mask) throws IOException {
        int height = mask.length;
        int width = mask[0].length;
        BufferedImage maskImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int color = mask[y][x] ? 0xFFFFFF : 0x000000;
                maskImage.setRGB(x, y, color);
            }
        }
        return imageToBytes(maskImage);
    }
}
