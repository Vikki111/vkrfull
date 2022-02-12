package com.example.vkrfull.model;

import javafx.util.Pair;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Character.codePointBefore;
import static java.lang.Character.isLetter;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FSM {

    //вершина(откуда), вершина(куда) -> список символов в лейбле
    Map<Pair<String, String>, List<String>> edges;
    String start_state;
    List<String> final_states;
    int state_number = 0;

    public String get_new_state() {
        this.state_number++;
        return String.valueOf(this.state_number);
    }

    public void add_edge(String src_state, String dst_state, String symbol) {
        if (edges.keySet().contains(new Pair<>(src_state, dst_state))) {
            edges.get(new Pair<>(src_state, dst_state)).add(symbol);
        } else {
            ArrayList arrayList = new ArrayList();
            arrayList.add(symbol);
            edges.put(new Pair<>(src_state, dst_state), arrayList);
        }
    }

    public void del_edge(String src_state, String dst_state, String symbol) {
        if (edges.keySet().contains(new Pair<>(src_state, dst_state))) {
            edges.remove(new Pair<>(src_state, dst_state));
        }
    }

    public void del_state(String state) {
        if (!start_state.equals(state)) {
            for (Pair<String, String> pair : edges.keySet()) {
                if (pair.getKey().equals(state) || pair.getValue().equals(state)) {
                    edges.remove(pair);
                }
            }
        }
    }

    //выходящие ребра из данной вершины
    //Map<symbol, List<dsts>>
    public Map<String, List<String>> get_output_edges(String src_state) {
        Map<String, List<String>> output_edges = new HashMap<>();
        for (Pair<String, String> pair : edges.keySet()) {
            if (pair.getKey().equals(src_state)) {
                for (String symbol : edges.get(pair)) {
                    if (output_edges.containsKey(symbol)) {
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
        for (Pair<String, String> pair : edges.keySet()) {
            if (pair.getValue().equals(dst_state)) {
                for (String symbol : edges.get(pair)) {
                    if (input_edges.containsKey(symbol)) {
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
            for (Pair<String, String> pair : edges.keySet()) {
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
            for (Pair<String, String> pair : edges.keySet()) {
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
        for (String symbol : keyset) {
            for (String src_state : input_edges.get(symbol)) {
                add_edge(src_state, to_state, symbol);
            }
        }
    }

    public void copy_output_edges(String from_state, String to_state) {
        Map<String, List<String>> output_edges = get_output_edges(from_state);
        Set<String> keyset = output_edges.keySet();
        for (String symbol : keyset) {
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
        for (Pair<String, String> pair : edges.keySet()) {
            if (pair.getKey().equals(replacement_state)) {
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
            } else if (pair.getValue().equals(replacement_state)) {
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

    public Map<Pair<String, String>, List<String>> get_graph() { ////////////?
        return this.edges;
    }

    public void automata_determination() {
        List<String> state = new ArrayList<>();
        state.add(this.start_state);
        List<String> queue = new ArrayList<>();
        queue.addAll(state); //?
        FSM new_fsm = new FSM();
        int index = 1;
        Map<List<String>, String> new_names = new HashMap<>();
        new_names.put(state, String.valueOf(index));
        while (queue.size() > 0) {
            state.add(queue.get(0));
            queue.remove(0);
            Map<String, List<String>> new_egdes = new HashMap<>();
            for (String src : state) {
                Map<String, List<String>> edges = get_output_edges(src);
                for (String symbol : edges.keySet()) { ///?
                    if (!new_egdes.containsKey(symbol)) {
                        new_egdes.put(symbol, edges.get(symbol));
                    } else {
                        List<String> tmp = new_egdes.get(symbol);
                        //edges.get(symbol) = dsts
                        for (String str : edges.get(symbol)) {
                            if (!tmp.contains(str)) {
                                tmp.add(str);
                            }
                        }
                        new_egdes.put(symbol, tmp);
                    }
                }
            }
            for (String symbol : new_egdes.keySet()) {
                List<String> tmp_dsts = new_egdes.get(symbol);
                if (!new_names.containsKey(tmp_dsts)) {
                    index++;
                    new_names.put(tmp_dsts, String.valueOf(index));
                    queue.addAll(tmp_dsts);
                }
                new_fsm.add_edge(new_names.get(state), new_names.get(tmp_dsts), symbol);
            }
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class CustomInfo {
        List<String> nodes;
        List<String> alphabet;
    }

    public CustomInfo get_info() {
        List<String> nodes = new ArrayList<>();
        List<String> alphabet = new ArrayList<>();
        for (Pair<String, String> pair : this.edges.keySet()) {
            nodes.add(pair.getKey());
            nodes.add(pair.getValue());
            for (String str : this.edges.get(pair)) {
                if (!alphabet.contains(str)) {
                    alphabet.add(str);
                }
            }
        }
        return new CustomInfo(nodes, alphabet);
    }

    public List<String> get_x(String c, List<String> a) {
        List<String> x = new ArrayList<>();
        for (String ma : a) {
            for (Pair<String, String> pair : this.edges.keySet()) {
                if (ma.equals(pair.getValue()) && this.edges.get(pair).contains(c)) {
                    x.add(pair.getKey());
                }
            }
        }
        return x;
    }

    public void automata_minimize() {
        CustomInfo customInfo = get_info();
        List<List<String>> p = new ArrayList<>();
        List<List<String>> w = new ArrayList<>();
        customInfo.getNodes().removeAll(this.final_states);
        p.add(this.final_states);
        p.add(customInfo.getNodes());
        w.add(this.final_states);
        w.add(customInfo.getNodes());
        while (w.size() > 0) {
            List<String> a = w.get(0); //?
            w.remove(0);
            for (String c : customInfo.getAlphabet()) {
                List<String> x = get_x(c, a);
                List<List<String>> tmp_p = new ArrayList<>(p);
                for (List<String> y : p) {
                    List<String> x_and_y = y.stream()
                            .distinct()
                            .filter(x::contains)
                            .collect(Collectors.toList());
                    List<String> x_sub_y = new ArrayList<>(y);
                    x_sub_y.removeAll(x);
                    if (x_and_y.size() > 0 && x_sub_y.size() > 0) {
                        tmp_p.remove(y);
                        tmp_p.add(x_and_y);
                        tmp_p.add(x_sub_y);
                        if (w.contains(y)) {
                            w.remove(y);
                            w.add(x_and_y);
                            w.add(x_sub_y);
                        } else if (x_and_y.size() <= x_sub_y.size()) {
                            w.add(x_and_y);
                        } else {
                            w.add(x_sub_y);
                        }
                    }
                }
                p = new ArrayList<>(tmp_p);
            }
        }
        for (List<String> nodes : p) {
            if (nodes.size() > 0) {
                if (nodes.contains(this.start_state)) {
                    nodes.remove(this.start_state);
                    String first = this.start_state;
                    for (String node : nodes) {
                        replace_node(node, first);
                        this.final_states.remove(node);
                    }
                } else {
                    String fisrt = nodes.get(0); //?
                    nodes.remove(0);
                    for (String node : nodes) {
                        replace_node(node, fisrt);
                        this.final_states.remove(node);
                    }
                }
            }
        }
    }

    public void automata_renumerate() {
        Map<String, String> mapping = new HashMap<>();
        Map<Pair<String, String>, List<String>> edges = new HashMap<>();
        List<String> final_states = new ArrayList<>();
        this.state_number = 0;
        LinkedList<String> queue = new LinkedList();
        queue.add(this.start_state);
        while (queue.size() > 0) {
            String state = queue.poll(); //?
            if (!mapping.containsKey(state)) { //?
                mapping.put(state, getStart_state());
                List<String> toGetChildren = new ArrayList<>();
                toGetChildren.add(state);
                for (String child : get_children(toGetChildren)) {
                    queue.add(child);
                }
            }
        }
        this.start_state = mapping.get(this.start_state);
        for (String final_state : this.final_states) {
            final_states.add(mapping.get(final_state));
        }
        this.final_states = final_states;
        for (Pair<String, String> pair : this.edges.keySet()) {
            edges.put(new Pair(mapping.get(pair.getKey()), mapping.get(pair.getValue())), this.edges.get(pair));
        }
        this.edges = edges;

    }

    class Worker {

        String start_nonterm;
        String grammar_str;
        Map<String, String> grammar;
        String[] set_alpha_array = {"_", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N",
                "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
        String[] set_digit_array = {"1", "2", "3", "4", "5", "6", "7", "8", "9"};
        String[] set_digitzero_array = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
        List<String> set_alpha = Arrays.asList(set_alpha_array);
        List<String> set_digit = Arrays.asList(set_alpha_array);
        List<String> set_digitzero = Arrays.asList(set_alpha_array);
        FSM fsm;

        public Worker(String start_nonterm, String grammar) {
            this.start_nonterm = start_nonterm;
            this.grammar_str = grammar;
            FSM fsm = new FSM();
            this.get_grammar_dict();
            this.create_automata();
        }

        public void get_grammar_dict() {
            this.grammar = new HashMap<>();
            List<String> lines = Arrays.asList(this.grammar_str.split("\n"));////////??
            for (String line : lines) {
                int index = line.indexOf("=");
                if(index==-1){
                    try {
                        throw new Exception("Rule error");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    String nonterm = line.substring(0, index); //strip
                    String rules = line.substring(index+1, line.length());
                    this.grammar.put(nonterm, rules);
                }
            }
        }

        public void create_automata() {
            String state = this.fsm.get_new_state();
            this.fsm.start_state = state;
            List<String> list = new ArrayList<>();
            list.add(state);
            this.fsm.final_states = this.expr(list, grammar.get(this.start_nonterm), 0);
        }

        public List<String> add_edges(List<String> parent_states, String symbol) {
            String new_state = this.fsm.get_new_state();
            for (String parent_state : parent_states) {
                this.fsm.add_edge(parent_state, new_state, symbol);
            }
            List<String> list = new ArrayList<>();
            list.add(new_state);
            return list;
        }

        public List<String> add_space(List<String> parent_states) {
            String new_state = this.fsm.get_new_state();
            for (String parent_state : parent_states) {
                this.fsm.add_edge(parent_state, new_state, " ");
                this.fsm.add_edge(new_state, new_state, " ");
            }
            List<String> list = new ArrayList<>();
            list.add(new_state);
            return list;
        }

        public List<String> add_optional_space(List<String> parent_states) {
            List<String> end_states = new ArrayList<>(parent_states);
            for (String parent_state : parent_states) {
                String new_state = this.fsm.get_new_state();
                this.fsm.add_edge(parent_state, new_state, " ");
                this.fsm.add_edge(new_state, new_state, " ");
                end_states.add(new_state);
            }
            return end_states;
        }

        public void expr(List<String> parent_states, String rules, int index) {
            List<String> end_states = new ArrayList<>();
            List<String> begin_states = new ArrayList<>(parent_states);
            int state = 1;
            String nonterm ="";
            while( state != 1000 && state != 666 && rules.length() > index) {
                if (state == 1) {
                    if (String.valueOf(rules.charAt(index)).equals(" ")) {
                        index++;
                        state = 1;
                    } else if(String.valueOf(rules.charAt(index)).equals("\'")) {
                        index++;
                        state = 2;
                    } else if (isLetter(rules.charAt(index))) {
                        nonterm = String.valueOf(rules.charAt(index));
                        index++;
                        state = 5;
                    } else if(String.valueOf(rules.charAt(index)).equals("(")) {
                        index++;
                        state = 6;
                    } else if (String.valueOf(rules.charAt(index)).equals("{")) {
                        index++;
                        state = 8;
                    } else if (String.valueOf(rules.charAt(index)).equals("[")) {
                        index++;
                        state = 10;
                    } else {
                        state = 666;
                    }
                } else if( state == 2) {
                    if(String.valueOf(rules.charAt(index)).equals("\'")) {
                        index++;
                        state = 4;
                    } else if(String.valueOf(rules.charAt(index)).equals("_")){
                        begin_states = this.add_optional_space(begin_states);
                        index++;
                        state = 3;
                    } else if(String.valueOf(rules.charAt(index)).equals(" ")) {
                       begin_states = this.add_space(begin_states);
                       index++;
                       state = 3;
                    } else {
                        begin_states = this.add_edges(begin_states, String.valueOf(rules.charAt(index)));
                        index++;
                        state = 3;
                    }
                } else if(state == 3){
                    if (String.valueOf(rules.charAt(index)).equals("\'")) {
                        index++;
                        state = 4;
                    } else if(String.valueOf(rules.charAt(index)).equals("_")){
                        begin_states = this.add_optional_space(begin_states);
                        index++;
                        state =3;
                    } else if(String.valueOf(rules.charAt(index)).equals(" ")) {
                        begin_states = this.add_space(begin_states);
                        index++;
                        state = 3;
                    } else {
                        begin_states = this.add_edges(begin_states, String.valueOf(rules.charAt(index)));
                        index++;
                        state =4;
                    }
                } else if(state == 4) {
                    if (String.valueOf(rules.charAt(index)).equals(" ")) {
                        index++;
                        state = 12;
                    } else if(String.valueOf(rules.charAt(index)).equals("|")) {
                        for (String begin_state : begin_states) {
                            if(!end_states.contains(begin_state)) {
                                end_states.add(begin_state);
                            }
                        }
                        begin_states = new ArrayList<>(parent_states);
                        index++;
                        state = 1;
                    } else if(String.valueOf(rules.charAt(index)).equals("\'")) {
                        begin_states = this.add_edges(begin_states, String.valueOf(rules.charAt(index)));
                        index++;
                        state = 3;
                    } else if(isLetter(rules.charAt(index))) {
                        nonterm = String.valueOf(rules.charAt(index));
                        index++;
                        state = 5;
                    } else if(String.valueOf(rules.charAt(index)).equals("(")) {
                        index++;
                        state = 6;
                    } else if (String.valueOf(rules.charAt(index)).equals("{")) {
                        index++;
                        state = 8;
                    } else if (String.valueOf(rules.charAt(index)).equals("[")) {
                        index++;
                        state = 10;
                    } else {
                        state = 1000;
                    }
                } else if(state == 5){
                    if(String.valueOf(rules.charAt(index)).equals(" ")) {
                        if (nonterm.equals("alpha")) {
                            begin_states = this.alpha(begin_states);
                        } else if (nonterm.equals("digit")) {
                            begin_states = this.digit(begin_states);
                        } else if (nonterm.equals("digitzero")) {
                            begin_states = this.digitzero(begin_states);
                        } else {
                            begin_states = this.expr(begin_states, this.grammar.get(nonterm), 0);
                        }
                        index++;
                        state = 12;
                    } else if(String.valueOf(rules.charAt(index)).equals("|")) {
                        if (nonterm.equals("alpha")) {
                            begin_states = this.alpha(begin_states);
                        } else if (nonterm.equals("digit")) {
                            begin_states = this.digit(begin_states);
                        } else if (nonterm.equals("digitzero")) {
                            begin_states = this.digitzero(begin_states);
                        } else {
                            begin_states = this.expr(begin_states, this.grammar.get(nonterm), 0);
                        }
                        for (String begin_state : begin_states) {
                            if(!end_states.contains(begin_state)) {
                                end_states.add(begin_state);
                            }
                        }
                        begin_states = new ArrayList<>(parent_states);
                        index++;
                        state = 1;
                    } else if (String.valueOf(rules.charAt(index)).equals("\'")) {
                        if (nonterm.equals("alpha")) {
                            begin_states = this.alpha(begin_states);
                        } else if (nonterm.equals("digit")) {
                            begin_states = this.digit(begin_states);
                        } else if (nonterm.equals("digitzero")) {
                            begin_states = this.digitzero(begin_states);
                        } else {
                            begin_states = this.expr(begin_states, this.grammar.get(nonterm), 0);
                        }
                        index++;
                        state = 2;
                    } else if(isLetter(rules.charAt(index))) {
                        nonterm.concat(String.valueOf(rules.charAt(index)));
                        index++;
                        state = 5;
                    } else if (String.valueOf(rules.charAt(index)).equals("(")) {
                        if (nonterm.equals("alpha")) {
                            begin_states = this.alpha(begin_states);
                        } else if (nonterm.equals("digit")) {
                            begin_states = this.digit(begin_states);
                        } else if (nonterm.equals("digitzero")) {
                            begin_states = this.digitzero(begin_states);
                        } else {
                            begin_states = this.expr(begin_states, this.grammar.get(nonterm), 0);
                        }
                        index++;
                        state = 6;
                    } else if (String.valueOf(rules.charAt(index)).equals("{")) {
                        if (nonterm.equals("alpha")) {
                            begin_states = this.alpha(begin_states);
                        } else if (nonterm.equals("digit")) {
                            begin_states = this.digit(begin_states);
                        } else if (nonterm.equals("digitzero")) {
                            begin_states = this.digitzero(begin_states);
                        } else {
                            begin_states = this.expr(begin_states, this.grammar.get(nonterm), 0);
                        }
                        index++;
                        state = 8;
                    } else if (String.valueOf(rules.charAt(index)).equals("[")) {
                        if (nonterm.equals("alpha")) {
                            begin_states = this.alpha(begin_states);
                        } else if (nonterm.equals("digit")) {
                            begin_states = this.digit(begin_states);
                        } else if (nonterm.equals("digitzero")) {
                            begin_states = this.digitzero(begin_states);
                        } else {
                            begin_states = this.expr(begin_states, this.grammar.get(nonterm), 0);
                        }
                        index++;
                        state = 10;
                    } else {
                        if (nonterm.equals("alpha")) {
                            begin_states = this.alpha(begin_states);
                        } else if (nonterm.equals("digit")) {
                            begin_states = this.digit(begin_states);
                        } else if (nonterm.equals("digitzero")) {
                            begin_states = this.digitzero(begin_states);
                        } else {
                            begin_states = this.expr(begin_states, this.grammar.get(nonterm), 0);
                        }
                        state = 1000;
                    }
                }
            }
        }


    }

//    __add_edges
//    __add_space
//    __add_optional_space
//    __expr
//    __alpha
//    __digit
//    __digitzero

}
