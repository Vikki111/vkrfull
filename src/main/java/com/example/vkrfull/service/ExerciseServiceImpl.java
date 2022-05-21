package com.example.vkrfull.service;

import com.example.vkrfull.model.Edge;
import com.example.vkrfull.model.Exercise;
import com.example.vkrfull.model.FileData;
import com.example.vkrfull.model.Graph;
import com.example.vkrfull.repository.ExerciseRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExerciseServiceImpl {

    private final ExerciseRepository exerciseRepository;

    public ExerciseServiceImpl(ExerciseRepository exerciseRepository) {
        this.exerciseRepository = exerciseRepository;
    }

    public String validate(Graph pythonGraph, String graphJson, UUID exerciseId) {
        log.debug("graph '{}' and exerciseId '{}'", graphJson, exerciseId);
        ObjectMapper objectMapper = new ObjectMapper();
        Graph studGraph = new Graph();
        try {
            studGraph = objectMapper.readValue(graphJson, Graph.class);

        } catch (IOException e) {
            e.printStackTrace();
        }
        String responseParseStudGraph = parseStudentGraph(studGraph);
        if (!responseParseStudGraph.equals("Верный синтаксис")) {
            return responseParseStudGraph;
        }
        System.out.println("python graph"+ pythonGraph);
        System.out.println("stud graph " + studGraph);
        return "Верный синтаксис";
    }

    public Graph parsePythonResponse(String str) {
        str = str.replaceAll("[({')]", "");
        str = str.replaceAll("}", "/");
        str = str.substring(0, str.length()-2);
        String[] strings = str.split("/, ");
        Graph graph = new Graph();
        graph.setEdges(new ArrayList<>());
        for (String str1: strings) {
            String[] leftright = str1.split(": ");
            String[] left = leftright[0].split(", ");
            String[] right = leftright[1].split(", ");
            String source = left[0];
            String target = left[1];
            for (String label : right) {
                graph.getEdges().add(new Edge(source, target, label));
            }
        }
        return graph;
    }

    public String parseStudentGraph(Graph studGraph) {
        // A|...|Z
        // (_|A|...|Z\E)|0|...|9
        // 0|...|9
        // *|-|+|/
        // =|>
        // K|L|M|P
        //space
        List<Edge> newEdges = new ArrayList<>(studGraph.getEdges());
        for (Edge edge : studGraph.getEdges()) {
            String str = edge.getLabel();
            if (str == null || str.equals("")) {
                System.out.println("Пустое наименование ребра между вершинами: "+edge.getSource() + " и "+ edge.getTarget());
                return "Пустое наименование ребра между вершинами: "+edge.getSource() + " и "+ edge.getTarget();
            }
            if (edge.getLabel().contains("space")) {
                newEdges.remove(edge);
                newEdges.add(new Edge(edge.getSource(), edge.getTarget(), " "));
                str = str.replace("space", "");
            }
            if (edge.getLabel().contains("A|...|Z") && edge.getLabel().contains("\\") && edge.getLabel().contains("(") && edge.getLabel().contains(")")) {
                List<Character> characterList = new ArrayList<>();
                for (int i = edge.getLabel().indexOf("\\"); i < edge.getLabel().indexOf(")"); i+=2) {
                    characterList.add(edge.getLabel().charAt(i+1));
                }
                str = str.replaceAll("[()]", "");
                str = str.replaceAll("A\\|...\\|Z", "");
                str = str.replace("\\", "");
                for (Character character : characterList) {
                    str = str.replace(character.toString(), "");
                }
                alphaWithoutChar(newEdges, edge, characterList);
            }
            if (edge.getLabel().contains("A|...|Z")) {
                str = str.replaceAll("A\\|...\\|Z", "");
                alpha(newEdges, edge);
            }
            if (edge.getLabel().contains("1|...|9")) {
                str = str.replaceAll("1\\|...\\|9", "");
                digit(newEdges, edge);
            }
            if (edge.getLabel().contains("0|...|9")) {
                str = str.replaceAll("0\\|...\\|9", "");
                digitzero(newEdges, edge);
            }
            if (str.contains("|")) {
                newEdges.remove(edge);
                String[] strings = str.split("\\|");
                for (String symbol : strings) {
                    if(symbol.length() > 1) {
                        return "Некорректное наименование ребра между вершинами: "+edge.getSource() + " и "+ edge.getTarget();
                    }
                    if (!symbol.equals("")) {
                        newEdges.add(new Edge(edge.getSource(), edge.getTarget(), symbol));
                        str = str.replaceAll(symbol, "");
                    }
                }
                str = str.replaceAll("\\|", "");
            }
            if (str.length() > 1) {
                return "Некорректное наименование ребра между вершинами: "+edge.getSource() + " и "+ edge.getTarget();
            }
        }
        studGraph.getEdges().clear();
        studGraph.getEdges().addAll(newEdges);
        return "Верный синтаксис";
    }

    public void alphaWithoutChar(List<Edge> newEdges, Edge edge, List<Character> characterList) {
        for (Edge edge1 : newEdges) {
            if (edge1.isSame(edge)) {
                newEdges.remove(edge1);
                break;
            }
        }
        for (int i = 65; i < 91; i++) {
            if (!characterList.contains((char)i)) {
                newEdges.add(new Edge(edge.getSource(), edge.getTarget(), String.valueOf((char) i)));
            }
        }
    }

    public void alpha(List<Edge> newEdges, Edge edge) {
        for (Edge edge1 : newEdges) {
            if (edge1.isSame(edge)) {
                newEdges.remove(edge1);
                break;
            }
        }
        for (int i = 65; i < 91; i++) {
            newEdges.add(new Edge(edge.getSource(), edge.getTarget(), String.valueOf((char)i)));
        }
    }

    public void digit(List<Edge> newEdges, Edge edge) {
        for (Edge edge1 : newEdges) {
            if (edge1.isSame(edge)) {
                newEdges.remove(edge1);
                break;
            }
        }
        for (int i = 1; i < 10; i++) {
            newEdges.add(new Edge(edge.getSource(), edge.getTarget(), String.valueOf(i)));
        }
    }

    public void digitzero(List<Edge> newEdges, Edge edge) {
        for (Edge edge1 : newEdges) {
            if (edge1.isSame(edge)) {
                newEdges.remove(edge1);
                break;
            }
        }
        for (int i = 0; i < 10; i++) {
            newEdges.add(new Edge(edge.getSource(), edge.getTarget(), String.valueOf(i)));
        }
    }

    public Exercise create(Exercise exercise) {
        exercise.setId(UUID.randomUUID());
        return exerciseRepository.save(exercise);
    }

    public void update(Exercise newExercise, UUID id) {
        if (exerciseRepository.existsById(id)) {
            newExercise.setId(id);
            exerciseRepository.save(newExercise);
        }
    }

    public Exercise get(UUID id) {
        return exerciseRepository.getById(id);
    }

    public List<Exercise> getAll() {
        return exerciseRepository.findAll();
    }

    public void delete(UUID id) {
        if (exerciseRepository.existsById(id)) {
            exerciseRepository.deleteById(id);
        }
    }

    public Resource download(String filename) {
        try {
            Path path = Paths.get("src/files");
            String pathString = path.toAbsolutePath().toString().replace("\\", "/");
            Path file = Paths.get(pathString)
                    .resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    public List<FileData> list() {
        try {
            Path root = Paths.get("src/files");

            if (Files.exists(root)) {
                return Files.walk(root, 1)
                        .filter(path -> !path.equals(root))
                        .filter(path -> path.toFile()
                                .isFile())
                        .collect(Collectors.toList())
                        .stream()
                        .map(this::pathToFileData)
                        .collect(Collectors.toList());
            }
            return Collections.emptyList();
        } catch (IOException e) {
            throw new RuntimeException("Could not list the files!");
        }
    }

    private FileData pathToFileData(Path path) {
        FileData fileData = new FileData();
        String filename = path.getFileName()
                .toString();
        fileData.setFilename(filename);

        try {
            fileData.setContentType(Files.probeContentType(path));
            fileData.setSize(Files.size(path));
        } catch (IOException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }

        return fileData;
    }

}
