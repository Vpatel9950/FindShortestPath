package com.Vishal.FindShortestPath.service;

import com.Vishal.FindShortestPath.model.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DijkstraService {

    public PathResult findShortestPath(Graph graph, String startId, String endId) {
        Map<String, Double> distances = new HashMap<>();
        Map<String, String> previous = new HashMap<>();
        PriorityQueue<NodeDistance> pq = new PriorityQueue<>(
                Comparator.comparingDouble(NodeDistance::getDistance)
        );
        Set<String> visited = new HashSet<>();
        List<AlgorithmStep> steps = new ArrayList<>();

        // Initialize distances
        for (String nodeId : graph.getAllNodeIds()) {
            distances.put(nodeId, Double.MAX_VALUE);
        }
        distances.put(startId, 0.0);
        pq.offer(new NodeDistance(startId, 0.0));

        steps.add(new AlgorithmStep("initialize", startId, null, 0.0,
                new HashMap<>(distances), new HashSet<>()));

        while (!pq.isEmpty()) {
            NodeDistance current = pq.poll();
            String currentId = current.getNodeId();

            if (visited.contains(currentId)) continue;
            visited.add(currentId);

            steps.add(new AlgorithmStep("visit", currentId, null,
                    distances.get(currentId), new HashMap<>(distances), new HashSet<>(visited)));

            if (currentId.equals(endId)) break;

            for (Edge edge : graph.getEdges(currentId)) {
                String neighborId = edge.getTo();
                if (visited.contains(neighborId)) continue;

                double newDist = distances.get(currentId) + edge.getWeight();

                if (newDist < distances.getOrDefault(neighborId, Double.MAX_VALUE)) {
                    distances.put(neighborId, newDist);
                    previous.put(neighborId, currentId);
                    pq.offer(new NodeDistance(neighborId, newDist));

                    steps.add(new AlgorithmStep("relax", currentId, neighborId,
                            newDist, new HashMap<>(distances), new HashSet<>(visited)));
                }
            }
        }

        // Reconstruct path
        List<String> path = new ArrayList<>();
        String curr = endId;
        // if end node unreachable, distances.get(endId) might be null or Double.MAX_VALUE
        if (!distances.containsKey(endId) || distances.get(endId) == Double.MAX_VALUE) {
            return new PathResult(Collections.emptyList(), Double.POSITIVE_INFINITY, steps, "Dijkstra");
        }

        while (curr != null) {
            path.add(0, curr);
            curr = previous.get(curr);
        }

        return new PathResult(
                path,
                distances.get(endId),
                steps,
                "Dijkstra"
        );
    }

    // Bellman-Ford Algorithm (handles negative weights)
    public PathResult bellmanFord(Graph graph, String startId, String endId) {
        Map<String, Double> distances = new HashMap<>();
        Map<String, String> previous = new HashMap<>();
        List<AlgorithmStep> steps = new ArrayList<>();

        // Initialize
        for (String nodeId : graph.getAllNodeIds()) {
            distances.put(nodeId, Double.MAX_VALUE);
        }
        distances.put(startId, 0.0);

        int n = graph.getAllNodeIds().size();

        // Relax edges n-1 times
        for (int i = 0; i < n - 1; i++) {
            boolean updated = false;
            for (String nodeId : graph.getAllNodeIds()) {
                if (distances.get(nodeId) == Double.MAX_VALUE) continue;

                for (Edge edge : graph.getEdges(nodeId)) {
                    double newDist = distances.get(nodeId) + edge.getWeight();
                    if (newDist < distances.getOrDefault(edge.getTo(), Double.MAX_VALUE)) {
                        distances.put(edge.getTo(), newDist);
                        previous.put(edge.getTo(), nodeId);
                        updated = true;
                        steps.add(new AlgorithmStep("relax", nodeId, edge.getTo(),
                                newDist, new HashMap<>(distances), new HashSet<>()));
                    }
                }
            }
            if (!updated) break;
        }

        // Reconstruct path
        List<String> path = new ArrayList<>();
        String curr = endId;
        if (!distances.containsKey(endId) || distances.get(endId) == Double.MAX_VALUE) {
            return new PathResult(Collections.emptyList(), Double.POSITIVE_INFINITY, steps, "Bellman-Ford");
        }

        while (curr != null) {
            path.add(0, curr);
            curr = previous.get(curr);
        }

        return new PathResult(path, distances.get(endId), steps, "Bellman-Ford");
    }

    // A* Algorithm (with heuristic)
    public PathResult aStar(Graph graph, String startId, String endId) {
        Map<String, Double> gScore = new HashMap<>();
        Map<String, Double> fScore = new HashMap<>();
        Map<String, String> previous = new HashMap<>();
        PriorityQueue<NodeDistance> openSet = new PriorityQueue<>(
                Comparator.comparingDouble(NodeDistance::getDistance)
        );
        Set<String> visited = new HashSet<>();
        List<AlgorithmStep> steps = new ArrayList<>();

        Node endNode = graph.getNode(endId);
        if (endNode == null) {
            return new PathResult(Collections.emptyList(), Double.POSITIVE_INFINITY, steps, "A*");
        }

        for (String nodeId : graph.getAllNodeIds()) {
            gScore.put(nodeId, Double.MAX_VALUE);
            fScore.put(nodeId, Double.MAX_VALUE);
        }

        gScore.put(startId, 0.0);
        fScore.put(startId, heuristic(graph.getNode(startId), endNode));
        openSet.offer(new NodeDistance(startId, fScore.get(startId)));

        while (!openSet.isEmpty()) {
            NodeDistance current = openSet.poll();
            String currentId = current.getNodeId();

            if (visited.contains(currentId)) continue;
            visited.add(currentId);

            steps.add(new AlgorithmStep("visit", currentId, null,
                    gScore.get(currentId), new HashMap<>(gScore), new HashSet<>(visited)));

            if (currentId.equals(endId)) break;

            for (Edge edge : graph.getEdges(currentId)) {
                String neighborId = edge.getTo();
                if (visited.contains(neighborId)) continue;

                double tentativeG = gScore.get(currentId) + edge.getWeight();

                if (tentativeG < gScore.getOrDefault(neighborId, Double.MAX_VALUE)) {
                    previous.put(neighborId, currentId);
                    gScore.put(neighborId, tentativeG);
                    fScore.put(neighborId, tentativeG + heuristic(graph.getNode(neighborId), endNode));
                    openSet.offer(new NodeDistance(neighborId, fScore.get(neighborId)));

                    steps.add(new AlgorithmStep("relax", currentId, neighborId,
                            tentativeG, new HashMap<>(gScore), new HashSet<>(visited)));
                }
            }
        }

        List<String> path = new ArrayList<>();
        String curr = endId;
        if (!gScore.containsKey(endId) || gScore.get(endId) == Double.MAX_VALUE) {
            return new PathResult(Collections.emptyList(), Double.POSITIVE_INFINITY, steps, "A*");
        }

        while (curr != null) {
            path.add(0, curr);
            curr = previous.get(curr);
        }

        return new PathResult(path, gScore.get(endId), steps, "A*");
    }

    private double heuristic(Node a, Node b) {
        if (a == null || b == null) return 0.0;
        double dx = a.getX() - b.getX();
        double dy = a.getY() - b.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }
}