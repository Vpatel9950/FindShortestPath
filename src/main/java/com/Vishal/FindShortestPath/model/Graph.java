package com.Vishal.FindShortestPath.model;


import java.util.*;

public class Graph {
    private Map<String, Node> nodes;
    private Map<String, List<Edge>> adjacencyList;

    public Graph() {
        this.nodes = new HashMap<>();
        this.adjacencyList = new HashMap<>();
    }

    public void addNode(String id, double x, double y) {
        Node node = new Node(id, x, y);
        nodes.put(id, node);
        adjacencyList.putIfAbsent(id, new ArrayList<>());
    }

    public void addEdge(String from, String to, double weight) {
        // ensure nodes exist
        adjacencyList.putIfAbsent(from, new ArrayList<>());
        adjacencyList.putIfAbsent(to, new ArrayList<>());

        Edge edge = new Edge(from, to, weight);
        adjacencyList.get(from).add(edge);
        // For undirected graph, add reverse edge
        adjacencyList.get(to).add(new Edge(to, from, weight));
    }

    public Node getNode(String id) {
        return nodes.get(id);
    }

    public List<Edge> getEdges(String nodeId) {
        return adjacencyList.getOrDefault(nodeId, new ArrayList<>());
    }

    public Set<String> getAllNodeIds() {
        return nodes.keySet();
    }

    public Map<String, Node> getNodes() {
        return nodes;
    }

    public Map<String, List<Edge>> getAdjacencyList() {
        return adjacencyList;
    }
}