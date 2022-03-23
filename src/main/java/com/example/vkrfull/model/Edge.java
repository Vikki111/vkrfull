package com.example.vkrfull.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Edge {
    @JsonProperty(value = "size")
    private String size;
    @JsonProperty(value = "color")
    private String color;
    @JsonProperty(value = "hover_color")
    private String hoverColor;
    @JsonProperty(value = "type")
    private String type;
    @JsonProperty(value = "source")
    private String source;
    @JsonProperty(value = "target")
    private String target;
    @JsonProperty(value = "id")
    private String id;
    @JsonProperty(value = "read_cam0:size")
    private String read_cam0size;
    @JsonProperty(value = "renderer1:size")
    private String renderer1size;
    @JsonProperty(value = "isSelected")
    private boolean isSelected;
    @JsonProperty(value = "label")
    private String label;

    public Edge(String source, String target, String label) {
        this.source = source;
        this.target = target;
        this.label = label;
    }

    public boolean isSame(Edge edge) {
        if (this.source.equals(edge.getSource())
                && this.target.equals(edge.getTarget())
                && this.label.equals(edge.getLabel())) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Edge{" +
                "source='" + source + '\'' +
                ", target='" + target + '\'' +
                ", label='" + label + '\'' +
                '}';
    }
}
