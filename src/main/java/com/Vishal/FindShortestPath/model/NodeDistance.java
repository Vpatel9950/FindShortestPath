package com.Vishal.FindShortestPath.model;

public class NodeDistance implements Comparable<NodeDistance> {
    private String nodeId;
    private double distance;

    public NodeDistance(String nodeId, double distance) {
        this.nodeId = nodeId;
        this.distance = distance;
    }

    public String getNodeId() {
        return nodeId;
    }

    public double getDistance() {
        return distance;
    }

    @Override
    public int compareTo(NodeDistance o) {
        return Double.compare(this.distance, o.distance);
    }
}
