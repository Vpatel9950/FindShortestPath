package com.Vishal.FindShortestPath.controller;



import com.Vishal.FindShortestPath.model.Graph;
import com.Vishal.FindShortestPath.model.PathResult;
import com.Vishal.FindShortestPath.service.DijkstraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@RestController
@RequestMapping("/api/graph")
@CrossOrigin(origins = "*") // For production, replace * with your frontend domain
public class GraphController {

    @Autowired
    private DijkstraService dijkstraService;

    private Graph graph;

    public GraphController() {
        initializeDefaultGraph();
    }


    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createGraph(@RequestBody GraphRequest request) {
        graph = new Graph();

        // Add nodes
        for (NodeRequest node : request.getNodes()) {
            graph.addNode(node.getId(), node.getX(), node.getY());
        }

        // Add edges
        for (EdgeRequest edge : request.getEdges()) {
            graph.addEdge(edge.getFrom(), edge.getTo(), edge.getWeight());
        }

        // Prepare response
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Graph created successfully");
        response.put("nodes", graph.getNodes().size());

        // Count edges (divided by 2 for undirected graph)
        int totalEdges = graph.getAdjacencyList().values()
                .stream()
                .mapToInt(List::size)
                .sum() / 2;

        response.put("edges", totalEdges);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/default")
    public ResponseEntity<Map<String, Object>> getDefaultGraph() {
        initializeDefaultGraph();
        return ResponseEntity.ok(serializeGraph());
    }

    @PostMapping("/shortest-path")
    public ResponseEntity<?> findShortestPath(@RequestBody PathRequest request) {
        if (graph == null) {
            initializeDefaultGraph();
        }

        String algorithm = (request.getAlgorithm() == null)
                ? "dijkstra"
                : request.getAlgorithm().toLowerCase();

        PathResult result;

        switch (algorithm) {
            case "bellman-ford":
                result = dijkstraService.bellmanFord(graph, request.getStart(), request.getEnd());
                break;
            case "a-star":
            case "astar":
            case "a*":
                result = dijkstraService.aStar(graph, request.getStart(), request.getEnd());
                break;
            default:
                result = dijkstraService.findShortestPath(graph, request.getStart(), request.getEnd());
        }

        return ResponseEntity.ok(result);
    }


    @PostMapping("/compare")
    public ResponseEntity<Map<String, Object>> compareAlgorithms(@RequestBody PathRequest request) {
        if (graph == null) {
            initializeDefaultGraph();
        }

        Map<String, Object> comparison = new LinkedHashMap<>();

        // --- Dijkstra ---
        long start = System.nanoTime();
        PathResult dijkstra = dijkstraService.findShortestPath(graph, request.getStart(), request.getEnd());
        long end = System.nanoTime();
        comparison.put("Dijkstra", buildResultMap(dijkstra, start, end));

        // --- Bellman-Ford ---
        start = System.nanoTime();
        PathResult bellman = dijkstraService.bellmanFord(graph, request.getStart(), request.getEnd());
        end = System.nanoTime();
        comparison.put("Bellman-Ford", buildResultMap(bellman, start, end));

        // --- A* ---
        start = System.nanoTime();
        PathResult aStar = dijkstraService.aStar(graph, request.getStart(), request.getEnd());
        end = System.nanoTime();
        comparison.put("A*", buildResultMap(aStar, start, end));

        return ResponseEntity.ok(comparison);
    }


    private void initializeDefaultGraph() {
        graph = new Graph();

        // Nodes
        graph.addNode("A", 100, 100);
        graph.addNode("B", 300, 100);
        graph.addNode("C", 500, 100);
        graph.addNode("D", 100, 250);
        graph.addNode("E", 300, 250);
        graph.addNode("F", 500, 250);
        graph.addNode("G", 100, 400);
        graph.addNode("H", 300, 400);
        graph.addNode("I", 500, 400);

        // Edges
        graph.addEdge("A", "B", 4);
        graph.addEdge("A", "C", 2);
        graph.addEdge("B", "C", 5);
        graph.addEdge("B", "F", 7);
        graph.addEdge("C", "D", 3);
        graph.addEdge("C", "E", 3);
        graph.addEdge("D", "G", 2);
        graph.addEdge("E", "H", 4);
        graph.addEdge("E", "I", 4);
        graph.addEdge("F", "I", 2);
        graph.addEdge("G", "H", 1);
        graph.addEdge("H", "I", 3);
    }

    /** ====================== HELPER METHODS ====================== **/
    private Map<String, Object> serializeGraph() {
        Map<String, Object> map = new HashMap<>();
        map.put("nodes", graph.getNodes());
        map.put("edges", graph.getAdjacencyList());
        return map;
    }

    private Map<String, Object> buildResultMap(PathResult result, long start, long end) {
        Map<String, Object> map = new HashMap<>();
        map.put("result", result);
        map.put("executionTimeMs", (end - start) / 1_000_000.0);
        map.put("stepsCount", result.getSteps().size());
        return map;
    }

    /** ====================== DTO CLASSES ====================== **/
    public static class GraphRequest {
        private List<NodeRequest> nodes;
        private List<EdgeRequest> edges;

        public List<NodeRequest> getNodes() { return nodes; }
        public void setNodes(List<NodeRequest> nodes) { this.nodes = nodes; }
        public List<EdgeRequest> getEdges() { return edges; }
        public void setEdges(List<EdgeRequest> edges) { this.edges = edges; }
    }

    public static class NodeRequest {
        private String id;
        private double x;
        private double y;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public double getX() { return x; }
        public void setX(double x) { this.x = x; }
        public double getY() { return y; }
        public void setY(double y) { this.y = y; }
    }

    public static class EdgeRequest {
        private String from;
        private String to;
        private double weight;

        public String getFrom() { return from; }
        public void setFrom(String from) { this.from = from; }
        public String getTo() { return to; }
        public void setTo(String to) { this.to = to; }
        public double getWeight() { return weight; }
        public void setWeight(double weight) { this.weight = weight; }
    }

    public static class PathRequest {
        private String start;
        private String end;
        private String algorithm;

        public String getStart() { return start; }
        public void setStart(String start) { this.start = start; }
        public String getEnd() { return end; }
        public void setEnd(String end) { this.end = end; }
        public String getAlgorithm() { return algorithm; }
        public void setAlgorithm(String algorithm) { this.algorithm = algorithm; }
    }
}
