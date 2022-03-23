package com.example.vkrfull.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Node {
    @JsonProperty(value = "size")
    private String size;
    @JsonProperty(value = "label")
    private String label;
    @JsonProperty(value = "x")
    private Integer x;
    @JsonProperty(value = "y")
    private Integer y;
    @JsonProperty(value = "dX")
    private Integer dX;
    @JsonProperty(value = "dY")
    private Integer dY;
    @JsonProperty(value = "id")
    private String id;
    @JsonProperty(value = "read_cam0:size")
    private String readCam0size;
    @JsonProperty(value = "read_cam0:x")
    private String read_cam0x;
    @JsonProperty(value = "read_cam0:y")
    private String read_cam0y;
    @JsonProperty(value = "renderer1:x")
    private String renderer1x;
    @JsonProperty(value = "renderer1:y")
    private String renderer1y;
    @JsonProperty(value = "renderer1:size")
    private String renderer1size;
    @JsonProperty(value = "color")
    private String color;
    @JsonProperty(value = "isSelected")
    private boolean isSelected;

    @Override
    public String toString() {
        return "Node{" +
                "label='" + label + '\'' +
                '}';
    }
}
