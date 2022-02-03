package com.example.vkrfull.model;

import javafx.util.Pair;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FSM {

    //вершина(откуда), вершина(куда) -> список символов в лейбле
    Map<Pair<String, String>, List<String>> edges;
    String start_state;
    List<String> final_states;
    int state_number = 0;

    public int get_new_state() {
        this.state_number++;
        return this.state_number;
    }

    public void add_edge(String src_state, String dst_state, String symbol) {
        if(edges.keySet().contains(new Pair<>(src_state, dst_state))) {
            edges.get(new Pair<>(src_state, dst_state)).add(symbol);
        } else {
            ArrayList arrayList = new ArrayList();
            arrayList.add(symbol);
            edges.put(new Pair<>(src_state, dst_state), arrayList);
        }
    }

    public void del_edge(String src_state, String dst_state, String symbol) {
        if(edges.keySet().contains(new Pair<>(src_state, dst_state))) {
            edges.remove(new Pair<>(src_state, dst_state));
        }
    }

    public void del_state(String state) {
        if(!start_state.equals(state)) {
            for (Pair<String, String> pair: edges.keySet()) {
                if(pair.getKey().equals(state) || pair.getValue().equals(state)) {
                    edges.remove(pair);
                }
            }
        }
    }

    //выходящие ребра из данной вершины
    public Map<String, List<String>> get_output_edges(String src_state) {
        Map<String, List<String>> output_edges = new HashMap<>();
        for (Pair<String, String> pair: edges.keySet()) {
            if(pair.getKey().equals(src_state)) {
                for (String symbol : edges.get(pair)) {
                    if(output_edges.containsKey(symbol)) {
                        output_edges.get(symbol).add(pair.getValue());
                    } else {
                        ArrayList arrayList = new ArrayList();
                        arrayList.add(pair.getValue());
                        output_edges.put(symbol, arrayList);
                    }
                }
            }
        }
        return output_edges;
    }

    //входящие ребра в данную вершину
    public Map<String, List<String>> get_input_edges(String dst_state) {
        Map<String, List<String>> input_edges = new HashMap<>();
        for (Pair<String, String> pair: edges.keySet()) {
            if(pair.getValue().equals(dst_state)) {
                for (String symbol : edges.get(pair)) {
                    if(input_edges.containsKey(symbol)) {
                        input_edges.get(symbol).add(pair.getKey());
                    } else {
                        ArrayList arrayList = new ArrayList();
                        arrayList.add(pair.getKey());
                        input_edges.put(symbol, arrayList);
                    }
                }
            }
        }
        return input_edges;
    }

    public List<String> get_children(List<String> src_states) {
        List<String> children = new ArrayList<>();
        for (String src_state : src_states) {
            for (Pair<String, String> pair: edges.keySet()) {
                if (pair.getKey().equals(src_state)) {
                    children.add(pair.getValue());
                }
            }
        }
        return children;
    }

    public List<String> get_children_by(String src_state, String symbol) {
        List<String> children = new ArrayList<>();
        Map<String, List<String>> output_edges = get_output_edges(src_state);
        if (output_edges.containsKey(symbol)) {
           children = output_edges.get(symbol);
        }
        return children;
    }

    public List<String> get_parents(List<String> dst_states) {
        List<String> parents = new ArrayList<>();
        for (String dst_state : dst_states) {
            for (Pair<String, String> pair: edges.keySet()) {
                if (pair.getValue().equals(dst_states)) {
                    parents.add(pair.getKey()); ////////////?
                }
            }
        }
        return parents;
    }

    public List<String> get_parents_by(String dst_state, String symbol) {
        List<String> parents = new ArrayList<>();
        Map<String, List<String>> input_edges = get_input_edges(dst_state);
        if (input_edges.containsKey(symbol)) {
            parents = input_edges.get(symbol);
        }
        return parents;
    }

    public void copy_input_edges(String from_state, String to_state) {
        Map<String, List<String>> input_edges = get_input_edges(from_state);
        Set<String> keyset = input_edges.keySet();
        for (String symbol: keyset) {
            for (String src_state : input_edges.get(symbol)) {
                add_edge(src_state, to_state, symbol);
            }
        }
    }

    public void copy_output_edges(String from_state, String to_state) {
        Map<String, List<String>> output_edges = get_output_edges(from_state);
        Set<String> keyset = output_edges.keySet();
        for (String symbol: keyset) {
            for (String dst_state : output_edges.get(symbol)) {
                add_edge(to_state, dst_state, symbol);/////////////////?
            }
        }
    }

//    get_cycle_presence
//    connect_to

    @AllArgsConstructor
    @Data
    class CustomEdge {
        String src;
        String dst;
        String symbol;
    }

    public void replace_node(String replacement_state, String new_state) {
        List<CustomEdge> del_edges = new ArrayList<>();
        List<CustomEdge> add_edges = new ArrayList<>();
        for (Pair<String, String> pair: edges.keySet()) {
            if(pair.getKey().equals(replacement_state)) {
                if (pair.getValue().equals(replacement_state)) {
                    for (String symbol : edges.get(pair)) {
                        del_edges.add(new CustomEdge(pair.getKey(), pair.getValue(), symbol));
                        add_edges.add(new CustomEdge(new_state, new_state, symbol));
                    }
                } else {
                    for (String symbol : edges.get(pair)) {
                        del_edges.add(new CustomEdge(pair.getKey(), pair.getValue(), symbol));
                        add_edges.add(new CustomEdge(new_state, pair.getValue(), symbol));
                    }
                }
            } else if(pair.getValue().equals(replacement_state)) {
                for (String symbol : edges.get(pair)) {
                    del_edges.add(new CustomEdge(pair.getKey(), pair.getValue(), symbol));
                    add_edges.add(new CustomEdge(pair.getKey(), new_state, symbol));
                }
            }
        }
        for (CustomEdge customEdge : del_edges) {
            del_edge(customEdge.getSrc(), customEdge.getDst(), customEdge.getSymbol());
        }
        for (CustomEdge customEdge : add_edges) {
            add_edge(customEdge.getSrc(), customEdge.getDst(), customEdge.getSymbol());
        }
    }

}
