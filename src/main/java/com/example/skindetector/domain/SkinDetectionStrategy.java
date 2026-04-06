package com.example.skindetector.domain;

/**
 * Strategy interface for skin detection algorithms.
 * Modernized to support swappable Bayesian and Heuristic models.
 */
public interface SkinDetectionStrategy {
    /**
     * @return true if the RGB combination represents human skin.
     */
    boolean isSkin(SkinRGB rgb);

    /**
     * @return User-friendly name of the strategy.
     */
    String getName();
}
