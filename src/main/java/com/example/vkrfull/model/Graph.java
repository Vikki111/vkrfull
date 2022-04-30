package com.example.vkrfull.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Graph {
    private List<Node> nodes;
    private List<Edge> edges;

    public HashMap<String, String> getEdges(String source) {
        HashMap<String, String> map = new HashMap<>();
        for (Edge edge : this.edges) {
            if (edge.getSource().equals(source)) {
                map.put(edge.getLabel(), edge.getTarget());
            }
        }
        return map;
    }
}
