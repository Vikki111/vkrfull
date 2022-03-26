package com.example.vkrfull.service;

import com.example.vkrfull.model.Edge;
import com.example.vkrfull.model.Exercise;
import com.example.vkrfull.model.Graph;
import com.example.vkrfull.repository.ExerciseRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ExerciseServiceImpl {

    private String[] vertex;    // Коллекция вершин
    private int[][] matrix;   // Матрица смежности
    private String[] vertexStud;    // Коллекция вершин
    private int[][] matrixStud;   // Матрица смежности
    private static final int INF = 999999; // Максимум
    private static int count = 0;
    private static int countStud = 0;
    private Graph studGraph = new Graph();
    private Graph pythonGraph = new Graph();
    private List<String> sequenceStud = new ArrayList<>();
    private List<String> sequencePython = new ArrayList<>();

    private final ExerciseRepository exerciseRepository;

    public ExerciseServiceImpl(ExerciseRepository exerciseRepository) {
        this.exerciseRepository = exerciseRepository;
    }

    /**
     * График прохождения поиска по глубине
     */
    public void DFS() {
        boolean[] visited = new boolean[vertex.length]; // Записываем метку доступа к вершине
        // Инициализируем все вершины не посещаются
        for(int i = 0; i < vertex.length; i++)
            visited[i] = false;
        System.out.println("Переход DFS:" );
        for(int i = 0; i < vertex.length; i++) {
            if(!visited[i])
                DFS(i, visited);
        }
        System.out.println();
    }

    private void DFS(int i, boolean[] visited) {
        count++;
        visited[i] = true;
        this.sequencePython.add(this.vertex[i]);
        if(count == vertex.length) {
            System.out.println(vertex[i]);
        }else {
            System.out.print(vertex[i] + "————>");
        }
        // Обходим все соседние вершины вершины, если она не была посещена, продолжаем
        for(int j = firstVertex(i); j >= 0; j = nextVertex(i, j)) {
            if(!visited[j]) {
                DFS(j, visited);
            }
        }
    }

    public void DFSStud() {
        boolean[] visited = new boolean[vertexStud.length]; // Записываем метку доступа к вершине
        // Инициализируем все вершины не посещаются
        for(int i = 0; i < vertexStud.length; i++)
            visited[i] = false;
        System.out.println("Переход DFS:" );
        for(int i = 0; i < vertexStud.length; i++) {
            if(!visited[i])
                DFSStud(i, visited);
        }
        System.out.println();
    }

    private void DFSStud(int i, boolean[] visited) {
        countStud++;
        visited[i] = true;
        this.sequenceStud.add(this.vertexStud[i]);
        if(countStud == vertexStud.length) {
            System.out.println(vertexStud[i]);
        }else {
            System.out.print(vertexStud[i] + "————>");
        }
        // Обходим все соседние вершины вершины, если она не была посещена, продолжаем
        for(int j = firstVertex(i); j >= 0; j = nextVertex(i, j)) {
            if(!visited[j]) {
                DFSStud(j, visited);
            }
        }
    }
    
    public boolean comparator() {
        for (int i = 0; i < this.sequenceStud.size()-1; i++) {
            List<String> py =findEdgeSymbols(sequencePython.get(i), sequencePython.get(i+1), this.pythonGraph);
            List<String> stud = findEdgeSymbols(sequenceStud.get(i), sequenceStud.get(i+1), this.studGraph);
            if (!equalCustom(py, stud)) {
                System.out.println("Stud "+sequenceStud.get(i)+"||"+sequenceStud.get(i+1));
                System.out.println("Py "+sequencePython.get(i)+"||"+sequencePython.get(i+1));
                System.out.println("py "+py);
                System.out.println("stud "+ stud);
                return false;
            }
        }
        return true;
    }

    /**
     * Вернуть индекс первой смежной вершины вершины v, вернуть -1 в случае неудачи
     */
    private int firstVertex(int v) {
        if(v < 0 || v > (vertex.length - 1))
            return -1;
        for(int i = 0; i < vertex.length; i++) {
            if(matrix[v][i] != 0 && matrix[v][i] != INF) {
                return i;
            }
        }
        return -1;
    }
    /**
     * Возвращает индекс следующей смежной вершины вершины v относительно w или -1 в случае ошибки
     */
    private int nextVertex(int v, int j) {
        if(v < 0 || v > (vertex.length - 1) || j < 0 || j > (vertex.length - 1))
            return -1;
        for(int i = j + 1; i < vertex.length; i++) {
            if(matrix[v][i] != 0 && matrix[v][i] != INF)
                return i;
        }
        return -1;
    }

    public void fillVertex(Graph graph) {
        ArrayList<String> vertexList = new ArrayList<>();
        for (Edge edge : graph.getEdges()) {
            if(!vertexList.contains(edge.getSource())) {
                vertexList.add(edge.getSource());
            }
            if(!vertexList.contains(edge.getTarget())) {
                vertexList.add(edge.getTarget());
            }
        }
        this.vertex = new String[vertexList.size()];
        for (int i = 0; i < vertexList.size(); i++) {
            this.vertex[i] = vertexList.get(i);
        }
    }

    public void fillVertexStud(Graph graph) {
        ArrayList<String> vertexList = new ArrayList<>();
        for (Edge edge : graph.getEdges()) {
            if(!vertexList.contains(edge.getSource())) {
                vertexList.add(edge.getSource());
            }
            if(!vertexList.contains(edge.getTarget())) {
                vertexList.add(edge.getTarget());
            }
        }
        this.vertexStud = new String[vertexList.size()];
        for (int i = 0; i < vertexList.size(); i++) {
            this.vertexStud[i] = vertexList.get(i);
        }
    }

    public void fillMatrix(Graph graph) {
        //строки/столбцы
        this.matrix = new int[this.vertex.length][this.vertex.length];
        for (int i = 0; i < this.vertex.length; i++) { //по строкам
            for (int j = 0; j < this.vertex.length; j++) { //по столбцам
                if (edgeExists(this.vertex[i], this.vertex[j], graph)) {
                    this.matrix[i][j] = 1;
                } else {
                    this.matrix[i][j] = 0;
                }
            }
        }
    }

    public void fillMatrixStud(Graph graph) {
        //строки/столбцы
        this.matrixStud = new int[this.vertexStud.length][this.vertexStud.length];
        for (int i = 0; i < this.vertexStud.length; i++) { //по строкам
            for (int j = 0; j < this.vertexStud.length; j++) { //по столбцам
                if (edgeExists(this.vertexStud[i], this.vertexStud[j], graph)) {
                    this.matrixStud[i][j] = 1;
                } else {
                    this.matrixStud[i][j] = 0;
                }
            }
        }
    }

    public boolean equalCustom(List<String> list1, List<String> list2) {
        for (String str :list1) {
            if(!list2.contains(str)) {
                return false;
            }
        }
        for (String str :list2) {
            if(!list1.contains(str)) {
                return false;
            }
        }
        return true;
    }

    public boolean edgeExists(String source, String target, Graph graph) {
        for (Edge edge : graph.getEdges()) {
            if (edge.getSource().equals(source) && edge.getTarget().equals(target)) {
                return true;
            }
        }
        return false;
    }

    public List<String> findEdgeSymbols(String source, String target, Graph graph) {
        List<String> symbols = new ArrayList<>();
        for (Edge edge : graph.getEdges()) {
            if (edge.getSource().equals(source) && edge.getTarget().equals(target)) {
                symbols.add(edge.getLabel());
            }
        }
        return symbols;
    }

    public Boolean validate(Graph pythonGraph, String graphJson, Integer exerciseId) {
        log.debug("graph '{}' and exerciseId '{}'", graphJson, exerciseId);
        ObjectMapper objectMapper = new ObjectMapper();
        Graph studGraph = new Graph();
        try {
            studGraph = objectMapper.readValue(graphJson, Graph.class);

        } catch (IOException e) {
            e.printStackTrace();
        }
        parseStudentGraph(studGraph);
        this.pythonGraph = pythonGraph;
        this.studGraph = studGraph;
        fillVertex(pythonGraph);
        fillVertexStud(studGraph);
        fillMatrix(pythonGraph);
        fillMatrixStud(studGraph);
        DFS();
        DFSStud();
//        boolean test = comparator();// не работает
        System.out.println("python graph"+ pythonGraph);
        System.out.println("stud graph " + studGraph);
        return true;
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

    public void parseStudentGraph(Graph studGraph) {
        // A|...|Z
        // (_|A|...|Z\E)|0|...|9
        // 0|...|9
        // *|-|+|/
        // =|>
        // K|L|M|P
        List<Edge> newEdges = new ArrayList<>(studGraph.getEdges());
        for (Edge edge : studGraph.getEdges()) {
            String str = edge.getLabel();
            if (str == null) {
                System.out.println("Empty label between: "+edge.getSource() + " "+ edge.getTarget());
            }
            if (edge.getLabel().contains("space")) {
                newEdges.remove(edge);
                newEdges.add(new Edge(edge.getSource(), edge.getTarget(), " "));
            }
            if (edge.getLabel().contains("\\")) {
                Character character = edge.getLabel().charAt(edge.getLabel().indexOf("\\")+1);
                str = str.replaceAll("[()]", "");
                str = str.replaceAll("A\\|...\\|Z", "");
                str = str.replace("\\", "");
                str = str.replace(character.toString(), "");
                alphaWithoutChar(newEdges, edge, character);
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
                    if (!symbol.equals("")) {
                        newEdges.add(new Edge(edge.getSource(), edge.getTarget(), symbol));
                    }
                }
            }
        }
        studGraph.getEdges().clear();
        studGraph.getEdges().addAll(newEdges);
    }

    public void alphaWithoutChar(List<Edge> newEdges, Edge edge, Character character) {
        for (Edge edge1 : newEdges) {
            if (edge1.isSame(edge)) {
                newEdges.remove(edge1);
                break;
            }
        }
        for (int i = 65; i < 91; i++) {
            if (!character.equals((char)i)) {
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

    public void create(Exercise exercise) {
        exerciseRepository.save(exercise);
    }

    public void update(Exercise newExercise, int id) {
        if (exerciseRepository.existsById(id)) {
            newExercise.setId(id);
            exerciseRepository.save(newExercise);
        }
    }

    public Exercise get(int id) {
        return exerciseRepository.getById(id);
    }

    public List<Exercise> getAll() {
        return exerciseRepository.findAll();
    }

    public void delete(int id) {
        if (exerciseRepository.existsById(id)) {
            exerciseRepository.deleteById(id);
        }
    }

}
