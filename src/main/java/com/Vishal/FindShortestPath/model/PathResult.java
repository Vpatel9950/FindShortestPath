package com.Vishal.FindShortestPath.model;

import java.util.List;

public class PathResult {
    private List<String> path;
    private double totalDistance;
    private List<AlgorithmStep> steps;
    private String algorithm;

    public PathResult(List<String> path, double totalDistance,
                      List<AlgorithmStep> steps, String algorithm) {
        this.path = path;
        this.totalDistance = totalDistance;
        this.steps = steps;
        this.algorithm = algorithm;
    }

    public List<String> getPath() {
        return path;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public List<AlgorithmStep> getSteps() {
        return steps;
    }

    public String getAlgorithm() {
        return algorithm;
    }
}