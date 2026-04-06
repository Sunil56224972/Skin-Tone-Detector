package com.example.skindetector.infrastructure.image;

import com.example.skindetector.domain.SkinDetectionStrategy;
import com.example.skindetector.domain.SkinRGB;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Optimized image processing for skin detection.
 * Logic is now decoupled from specific strategies to allow concurrent use 
 * with multiple algorithms.
 */
@Component
@RequiredArgsConstructor
public class BufferedSkinImageProcessor {

    public ProcessedImageData process(BufferedImage image, SkinDetectionStrategy strategy) {
        int width = image.getWidth();
        int height = image.getHeight();

        boolean[][] mask = new boolean[height][width];
        BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        
        Graphics2D g2d = output.createGraphics();
        g2d.drawImage(image, 0, 0, null);

        int skinPixels = 0;
        int highlightRgb = new Color(255, 0, 0, 120).getRGB();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;

                if (strategy.isSkin(new SkinRGB(r, g, b))) {
                    mask[y][x] = true;
                    skinPixels++;
                    output.setRGB(x, y, highlightRgb);
                }
            }
        }
        g2d.dispose();

        return new ProcessedImageData(output, mask, skinPixels);
    }

    public record ProcessedImageData(BufferedImage image, boolean[][] mask, int skinCount) {}
}
