package com.example.skindetector.domain;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Bayesian implementation using pre-trained probability matrices.
 * Modernized to handle automatic fallback and thread-safe lookup.
 */
@Component("bayesian")
@Slf4j
@RequiredArgsConstructor
public class BayesianDetectionStrategy implements SkinDetectionStrategy {

    private final ResourceLoader resourceLoader;
    private final HeuristicDetectionStrategy fallbackStrategy;

    private float[] probabilityMap;
    private boolean isDataLoaded = false;

    @Value("${app.skin.bayesian.threshold:0.15}")
    private double threshold;

    @Value("${app.skin.bayesian.data-path:classpath:image_knowledge.dat}")
    private String dataPath;

    @PostConstruct
    public void init() {
        try {
            Resource resource = resourceLoader.getResource(dataPath);
            if (resource.exists()) {
                loadProbabilityMap(resource);
                isDataLoaded = true;
                log.info("Successfully loaded Bayesian probability matrix from {}", dataPath);
            } else {
                log.warn("Bayesian dataset NOT found at {}. Falling back to Heuristic strategy.", dataPath);
            }
        } catch (Exception e) {
            log.error("Failed to load Bayesian data, starting with fallback strategy.", e);
        }
    }

    private void loadProbabilityMap(Resource resource) throws IOException {
        probabilityMap = new float[256 * 256 * 256];
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            String line;
            int count = 0;
            while ((line = reader.readLine()) != null && count < probabilityMap.length) {
                probabilityMap[count++] = Float.parseFloat(line.trim());
            }
        }
    }

    @Override
    public boolean isSkin(SkinRGB rgb) {
        if (!isDataLoaded) {
            return fallbackStrategy.isSkin(rgb);
        }

        int index = (rgb.r() << 16) | (rgb.g() << 8) | rgb.b();
        return probabilityMap[index] > threshold;
    }

    @Override
    public String getName() {
        return "bayesian";
    }
}
