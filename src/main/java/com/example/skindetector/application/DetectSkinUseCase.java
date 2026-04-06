package com.example.skindetector.application;

import com.example.skindetector.domain.SkinDetectionResult;
import java.awt.image.BufferedImage;

/**
 * Modernized use case interface for skin detection.
 * Supports dynamic algorithm selection via strategies.
 */
public interface DetectSkinUseCase {
    /**
     * @param image Input image.
     * @param strategyName Name of the algorithm to use (e.g., "heuristic", "bayesian").
     */
    SkinDetectionResult execute(BufferedImage image, String strategyName);
}
