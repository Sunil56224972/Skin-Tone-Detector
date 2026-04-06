package com.example.skindetector.domain;

import org.springframework.stereotype.Component;

/**
 * Heuristic implementation based on the Peer et al. RGB skin detection rule.
 * Modernized to use the Strategy Pattern.
 */
@Component("heuristic")
public class HeuristicDetectionStrategy implements SkinDetectionStrategy {

    @Override
    public boolean isSkin(SkinRGB rgb) {
        int r = rgb.r();
        int g = rgb.g();
        int b = rgb.b();

        int max = Math.max(r, Math.max(g, b));
        int min = Math.min(r, Math.min(g, b));

        return r > 95 && g > 40 && b > 20 &&
                (max - min) > 15 &&
                Math.abs(r - g) > 15 &&
                r > g && r > b;
    }

    @Override
    public String getName() {
        return "heuristic";
    }
}
