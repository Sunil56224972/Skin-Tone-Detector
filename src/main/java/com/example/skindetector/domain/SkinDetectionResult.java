package com.example.skindetector.domain;

import lombok.Builder;
import lombok.Value;

import java.awt.image.BufferedImage;

/**
 * Domain object encapsulating the results of the skin detection process.
 */
@Value
@Builder
public class SkinDetectionResult {
    /**
     * The original image with skin areas highlighted (semi-transparent overlay).
     */
    BufferedImage highlightedImage;

    /**
     * Binary mask (1 for skin, 0 for non-skin). 
     * Represented as a boolean 2D array for internal logic.
     */
    boolean[][] binaryMask;

    /**
     * Percentage of the image identified as skin.
     */
    double skinPercentage;
}
