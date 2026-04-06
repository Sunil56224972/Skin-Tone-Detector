package com.example.skindetector.core;

import com.example.skindetector.domain.SkinRGB;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Path;
import java.util.List;

/**
 * Utility to generate a Bayesian knowledge base (.dat file) from a skin/non-skin dataset.
 * It simulates the training phase by counting RGB frequencies across 256^3 possible colors.
 */
@Component
@Slf4j
public class KnowledgeGenerator {

    private static final int COLOR_SIZE = 256;
    private static final int TOTAL_COLORS = COLOR_SIZE * COLOR_SIZE * COLOR_SIZE;

    /**
     * Trains the system and generates the image_knowledge.dat file.
     * 
     * @param inputDir Directory containing training images and their masks.
     * @param outputPath Path where the .dat file will be saved.
     */
    public void generateKnowledgeFile(File inputDir, Path outputPath) throws IOException {
        long[] skinFreq = new long[TOTAL_COLORS];
        long[] nonSkinFreq = new long[TOTAL_COLORS];

        // Laplace Smoothing: Start all counts at 1.
        for (int i = 0; i < TOTAL_COLORS; i++) {
            skinFreq[i] = 1;
            nonSkinFreq[i] = 1;
        }

        File[] files = inputDir.listFiles((dir, name) -> name.endsWith(".jpg") || name.endsWith(".png"));
        if (files == null) return;

        long totalSkinPixels = 0;
        long totalNonSkinPixels = 0;

        for (File imgFile : files) {
            String maskName = imgFile.getName().replace(".jpg", "_mask.png").replace(".png", "_mask.png");
            File maskFile = new File(imgFile.getParent(), maskName);

            if (!maskFile.exists()) continue;

            BufferedImage img = ImageIO.read(imgFile);
            BufferedImage mask = ImageIO.read(maskFile);

            for (int y = 0; y < img.getHeight(); y++) {
                for (int x = 0; x < img.getWidth(); x++) {
                    int rgb = img.getRGB(x, y);
                    int r = (rgb >> 16) & 0xFF;
                    int g = (rgb >> 8) & 0xFF;
                    int b = rgb & 0xFF;
                    int index = (r << 16) | (g << 8) | b;

                    int maskRgb = mask.getRGB(x, y);
                    // Mask logic: white pixel = skin.
                    if ((maskRgb & 0xFFFFFF) > 0x888888) {
                        skinFreq[index]++;
                        totalSkinPixels++;
                    } else {
                        nonSkinFreq[index]++;
                        totalNonSkinPixels++;
                    }
                }
            }
        }

        double priorSkin = (double) totalSkinPixels / (totalSkinPixels + totalNonSkinPixels);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath.toFile()))) {
            for (int r = 0; r < COLOR_SIZE; r++) {
                for (int g = 0; g < COLOR_SIZE; g++) {
                    for (int b = 0; b < COLOR_SIZE; b++) {
                        int index = (r << 16) | (g << 8) | b;
                        
                        double prob = (double) skinFreq[index] * priorSkin / (skinFreq[index] + nonSkinFreq[index]);
                        writer.write(String.format("%.3f\n", prob));
                    }
                }
            }
        }
        log.info("Knowledge generation complete. Stored in {}", outputPath);
    }
}
