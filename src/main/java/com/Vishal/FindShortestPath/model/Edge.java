package com.Vishal.FindShortestPath.model;


public class Edge {
    private String from;
    private String to;
    private double weight;

    public Edge(String from, String to, double weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
    }

    public String getFrom() { return from; }
    public String getTo() { return to; }
    public double getWeight() { return weight; }
}