package com.Vishal.FindShortestPath.model;


import java.util.*;

public class AlgorithmStep {
    private String action;
    private String currentNode;
    private String targetNode;
    private double distance;
    private Map<String, Double> distances;
    private Set<String> visited;

    public AlgorithmStep(String action, String currentNode, String targetNode,
                         double distance, Map<String, Double> distances, Set<String> visited) {
        this.action = action;
        this.currentNode = currentNode;
        this.targetNode = targetNode;
        this.distance = distance;
        this.distances = distances;
        this.visited = visited;
    }

    public String getAction() {
        return action;
    }

    public String getCurrentNode() {
        return currentNode;
    }

    public String getTargetNode() {
        return targetNode;
    }

    public double getDistance() {
        return distance;
    }

    public Map<String, Double> getDistances() {
        return distances;
    }

    public Set<String> getVisited() {
        return visited;
    }
}