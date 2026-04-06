package com.example.skindetector.application;

import com.example.skindetector.domain.DetectionStrategyFactory;
import com.example.skindetector.domain.SkinDetectionResult;
import com.example.skindetector.domain.SkinDetectionStrategy;
import com.example.skindetector.infrastructure.image.BufferedSkinImageProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;

/**
 * Modernized Skin Detection Service with Strategy Pattern orchestration.
 * Thread-safe and supports runtime algorithm switching.
 */
@Service
@RequiredArgsConstructor
public class SkinDetectionService implements DetectSkinUseCase {

    private final BufferedSkinImageProcessor processor;
    private final DetectionStrategyFactory strategyFactory;

    @Override
    public SkinDetectionResult execute(BufferedImage image, String strategyName) {
        SkinDetectionStrategy strategy = strategyFactory.getStrategy(strategyName);
        var data = processor.process(image, strategy);
        
        int totalPixels = image.getWidth() * image.getHeight();
        double percentage = (double) data.skinCount() / totalPixels * 100;

        return SkinDetectionResult.builder()
                .highlightedImage(data.image())
                .binaryMask(data.mask())
                .skinPercentage(percentage)
                .build();
    }
}
