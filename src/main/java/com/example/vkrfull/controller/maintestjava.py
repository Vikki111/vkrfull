import sys
from collections import deque

class FSM:

    def __init__(self):
        self.__edges = dict()
        self.__start_state = ""
        self.__final_states = set()
        self.__state_number = 0

    @property
    def start_state(self):
        return self.__start_state

    @start_state.setter
    def start_state(self, name):
        self.__start_state = name

    @property
    def final_states(self):
        return self.__final_states

    @final_states.setter
    def final_states(self, nodes: set):
        self.__final_states = nodes

    def edge(self, src_state: str, dst_state: str):
        return self.__edges[(src_state, dst_state)]

    def get_new_state(self):
        self.__state_number += 1
        return str(self.__state_number)

    def add_edge(self, src_state: str, dst_state: str, symbol: str):
        if (src_state, dst_state) in self.__edges:
            self.__edges[(src_state, dst_state)].add(symbol)
        else:
            self.__edges[(src_state, dst_state)] = set([symbol])

    def del_edge(self, src_state: str, dst_name: str, symbol: str):
        if (src_state, dst_name) in self.__edges:
            self.__edges[(src_state, dst_name)].discard(symbol)
            if len(self.__edges[(src_state, dst_name)]) == 0:
                self.__edges.pop((src_state, dst_name), None)

    def del_state(self, state: str):
        if state != self.__start_state:
            for (src, dst) in self.__edges:
                if src == state or dst == state:
                    self.__edges.pop((src, dst), None)

    def get_output_edges(self, src_state: str):
        edges = dict()
        for ((src, dst), symbols) in self.__edges.items():
            if src == src_state:
                for symbol in symbols:
                    if symbol in edges:
                        edges[symbol].add(dst)
                    else:
                        edges[symbol] = set([dst])
        return edges

    def get_input_edges(self, dst_state: str):
        edges = dict()
        for ((src, dst), symbols) in self.__edges.items():
            if dst == dst_state:
                for symbol in symbols:
                    if symbol in edges:
                        edges[symbol].add(src)
                    else:
                        edges[symbol] = set([src])
        return edges

    def get_children(self, src_states: set):
        children = set()
        for src_state in src_states:
            for ((src, dst), symbols) in self.__edges.items():
                if src == src_state:
                    children.add(dst)
        return children

    def get_children_by(self, src_state: str, symbol: str):
        children = set()
        edges = self.get_output_edges(src_state)
        if symbol in edges:
            children = edges[symbol]
        return children

    def get_parents(self, dst_states: set):
        parents = set()
        for dst_state in dst_states:
            for ((src, dst), symbols) in self.__edges.items():
                if dst == dst_state:
                    parents.add(dst)
        return parents

    def get_parents_by(self, dst_state: str, symbol: str):
        parents = set()
        edges = self.get_input_edges(dst_state)
        if symbol in edges:
            parents = edges[symbol]
        return parents

    def copy_input_edges(self, from_state: str, to_state: str):
        edges = self.get_input_edges(from_state)
        for symbol, src_states in edges.items():
            for src_state in src_states:
                self.add_edge(src_state, to_state, symbol)

    def copy_output_edges(self, from_state: str, to_state: str):
        edges = self.get_output_edges(from_state)
        for symbol, dst_states in edges.items():
            for dst_state in dst_states:
                self.add_edge(to_state, dst_states, symbol)

    def get_cycle_presence(self, state: str):
        return (state, state) in self.__edges

    def connect_to(self, from_states, to_states):
        add_edges = dict()
        for from_state in from_states:
            for to_state in to_states:
                for ((src, dst), symbols) in self.__edges.items():
                    if dst == to_state:
                        add_edges[(from_state, dst)] = symbols
        for ((src, dst), symbols) in add_edges.items():
            for symbol in symbols:
                self.add_edge(src, dst, symbol)

    def replace_node(self, replacement_state: str, new_state: str):
        del_edges = set()
        add_edges = set()
        for ((src, dst), symbols) in self.__edges.items():
            if src == replacement_state:
                if dst == replacement_state:
                    for symbol in symbols:
                        del_edges.add((src, dst, symbol))
                        add_edges.add((new_state, new_state, symbol))
                else:
                    for symbol in symbols:
                        del_edges.add((src, dst, symbol))
                        add_edges.add((new_state, dst, symbol))
            elif dst == replacement_state:
                for symbol in symbols:
                    del_edges.add((src, dst, symbol))
                    add_edges.add((src, new_state, symbol))
        for (src, dst, symbol) in del_edges:
            self.del_edge(src, dst, symbol)
        for (src, dst, symbol) in add_edges:
            self.add_edge(src, dst, symbol)

    def get_graph(self):
        edges = dict()
        for (src, dst), symbols in self.__edges.items():
            edges[(src, dst)] = symbols.copy()
        return edges

    def automata_determination(self):
        state = frozenset([self.__start_state])
        queue = set([state])
        new_fsm = FSM()
        index = 1
        new_names = {state: str(index)}
        while len(queue) > 0:
            state = queue.pop()
            new_edges = dict()
            for src in state:
                edges = self.get_output_edges(src)
                for (symbol, dsts) in edges.items():
                    new_edges[symbol] = new_edges.get(symbol, set()) | dsts
            for (symbol, dsts) in new_edges.items():
                tmp_dsts = frozenset(dsts)
                if not tmp_dsts in new_names:
                    index += 1
                    new_names[tmp_dsts] = str(index)
                    queue.add(tmp_dsts)
                new_fsm.add_edge(new_names[state], new_names[tmp_dsts], symbol)

        self.__edges = new_fsm.__edges
        self.__start_state = new_names[frozenset([self.__start_state])]
        final_nodes = self.__final_states.copy()
        self.__final_states.clear()
        for old_names, new_name in new_names.items():
            if len(old_names & final_nodes) > 0:
                self.__final_states.add(new_name)
        self.__state_number = index

    def __get_info(self):
        nodes = set()
        alphabet = set()
        for ((src, dst), symbols) in self.__edges.items():
            nodes.add(src)
            nodes.add(dst)
            alphabet |= symbols
        return (nodes, alphabet)

    def __get_x(self, c: str, A: set):
        X = set()
        for a in A:
            for (src, dst), symbols in self.__edges.items():
                if a == dst and c in symbols:
                    X.add(src)
        return X

    def automata_minimize(self):
        (nodes, alphabet) = self.__get_info()
        p = {frozenset(self.__final_states), frozenset(nodes - self.__final_states)}
        w = {frozenset(self.__final_states), frozenset(nodes - self.__final_states)}
        while len(w) > 0:
            a = w.pop()
            for c in alphabet:
                x = self.__get_x(c, a)
                tmp_p = p.copy()
                for y in p:
                    x_and_y = frozenset(x & y)
                    x_sub_y = frozenset(y - x)
                    if len(x_and_y) > 0 and len(x_sub_y) > 0:
                        tmp_p.discard(y)
                        tmp_p.add(x_and_y)
                        tmp_p.add(x_sub_y)
                        if y in w:
                            w.discard(y)
                            w.add(x_and_y)
                            w.add(x_sub_y)
                        elif len(x_and_y) <= len(x_sub_y):
                            w.add(x_and_y)
                        else:
                            w.add(x_sub_y)
                p = tmp_p
        for nodes in p:
            if len(nodes) > 0:
                nodes = set(nodes)
                if self.__start_state in nodes:
                    nodes.remove(self.__start_state)
                    first = self.__start_state
                    for node in nodes:
                        self.replace_node(node, first)
                        self.__final_states.discard(node)
                else:
                    first = nodes.pop()
                    for node in nodes:
                        self.replace_node(node, first)
                        self.__final_states.discard(node)

    def automata_renumerate(self):
        mapping = dict()
        edges = dict()
        final_states = set()

        self.__state_number = 0
        queue = deque([self.__start_state])

        while len(queue) > 0:
            state = queue.popleft()
            if not state in mapping:
                mapping[state] = self.get_new_state()
                for child in self.get_children(set([state])):
                    queue.append(child)

        self.__start_state = mapping[self.__start_state]
        for final_state in self.__final_states:
            final_states.add(mapping[final_state])
        self.__final_states = final_states
        for (src, dst), symbols in self.__edges.items():
            edges[(mapping[src], mapping[dst])] = self.__edges[(src, dst)]
        self.__edges = edges
        print(self.__edges)

class Worker:

    def __init__(self, start_nonterm: str, grammar: str):
        self.__start_nonterm = start_nonterm
        self.__grammar_str = grammar
        self.__grammar = dict()
        self.__set_alpha = {'_', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
                            'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'}
        self.__set_digit = {'1', '2', '3', '4', '5', '6', '7', '8', '9' }
        self.__set_digitzero = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'}
        self.__fsm = FSM()
        self.__get_grammar_dict()
        self.__create_automata()

    def __get_grammar_dict(self):
        self.__grammar = dict()
        lines = self.__grammar_str.splitlines()
        for line in lines:
            index = line.find('=')
            if index == -1:
                raise Exception('Rule error')
            else:
                nonterm = line[:index].strip()
                rules = line[index+1:]
                self.__grammar[nonterm] = rules

    def __create_automata(self):
        state = self.__fsm.get_new_state()
        self.__fsm.start_state = state
        self.__fsm.final_states, _ = self.__expr(set([state]), self.__grammar[self.__start_nonterm], 0)

    def __add_edges(self, parent_states: set, symbol: str):
        new_state = self.__fsm.get_new_state()
        for parent_state in parent_states:
            self.__fsm.add_edge(parent_state, new_state, symbol)
        return set([new_state])

    def __add_space(self, parent_states: set):
        new_state = self.__fsm.get_new_state()
        for parent_state in parent_states:
            self.__fsm.add_edge(parent_state, new_state, ' ')
            self.__fsm.add_edge(new_state, new_state, ' ')
        return set([new_state])

    def __add_optional_space(self, parent_states: set):
        end_states = parent_states.copy()
        for parent_state in parent_states:
            new_state = self.__fsm.get_new_state()
            self.__fsm.add_edge(parent_state, new_state, ' ')
            self.__fsm.add_edge(new_state, new_state, ' ')
            end_states.add(new_state)
        return end_states

    def __expr(self, parent_states: set, rules: str, index: int):
        end_states = set()
        begin_states = parent_states.copy()
        state = 1
        while state != 1000 and state != 666 and len(rules) > index:
            if state == 1:
                if rules[index] == ' ':
                    index += 1
                    state = 1
                elif rules[index] == '\'':
                    index += 1
                    state = 2
                elif rules[index].isalpha():
                    nonterm = rules[index]
                    index += 1
                    state = 5
                elif rules[index] == '(':
                    index += 1
                    state = 6
                elif rules[index] == '{':
                    index += 1
                    state = 8
                elif rules[index] == '[':
                    index += 1
                    state = 10
                else:
                    state = 666
            elif state == 2:
                if rules[index] == '\'':
                    index += 1
                    state = 4
                elif rules[index] == '_':
                    begin_states = self.__add_optional_space(begin_states)
                    index += 1
                    state = 3
                elif rules[index] == ' ':
                    begin_states = self.__add_space(begin_states)
                    index += 1
                    state = 3
                else:
                    begin_states = self.__add_edges(begin_states, rules[index])
                    index += 1
                    state = 3
            elif state == 3:
                if rules[index] == '\'':
                    index += 1
                    state = 4
                elif rules[index] == '_':
                    begin_states = self.__add_optional_space(begin_states)
                    index += 1
                    state = 3
                elif rules[index] == ' ':
                    begin_states = self.__add_space(begin_states)
                    index += 1
                    state = 3
                else:
                    begin_states = self.__add_edges(begin_states, rules[index])
                    index += 1
                    state = 3
            elif state == 4:
                if rules[index] == ' ':
                    index += 1
                    state = 12
                elif rules[index] == '|':
                    end_states |= begin_states
                    begin_states = parent_states.copy()
                    index += 1
                    state = 1
                elif rules[index] == '\'':
                    begin_states = self.__add_edges(begin_states, rules[index])
                    index += 1
                    state = 3
                elif rules[index].isalpha():
                    nonterm = rules[index]
                    index += 1
                    state = 5
                elif rules[index] == '(':
                    index += 1
                    state = 6
                elif rules[index] == '{':
                    index += 1
                    state = 8
                elif rules[index] == '[':
                    index += 1
                    state = 10
                else:
                    state = 1000
            elif state == 5:
                if rules[index] == ' ':
                    if nonterm == 'alpha':
                        begin_states = self.__alpha(begin_states)
                    elif nonterm == 'digit':
                        begin_states = self.__digit(begin_states)
                    elif nonterm == 'digitzero':
                        begin_states = self.__digitzero(begin_states)
                    else:
                        begin_states, _ = self.__expr(begin_states, self.__grammar[nonterm], 0)
                    index += 1
                    state = 12
                elif rules[index] == '|':
                    if nonterm == 'alpha':
                        begin_states = self.__alpha(begin_states)
                    elif nonterm == 'digit':
                        begin_states = self.__digit(begin_states)
                    elif nonterm == 'digitzero':
                        begin_states = self.__digitzero(begin_states)
                    else:
                        begin_states, _ = self.__expr(begin_states, self.__grammar[nonterm], 0)
                    end_states |= begin_states
                    begin_states = parent_states.copy()
                    index += 1
                    state = 1
                elif rules[index] == '\'':
                    if nonterm == 'alpha':
                        begin_states = self.__alpha(begin_states)
                    elif nonterm == 'digit':
                        begin_states = self.__digit(begin_states)
                    elif nonterm == 'digitzero':
                        begin_states = self.__digitzero(begin_states)
                    else:
                        begin_states, _ = self.__expr(begin_states, self.__grammar[nonterm], 0)
                    index += 1
                    state = 2
                elif rules[index].isalpha():
                    nonterm += rules[index]
                    index += 1
                    state = 5
                elif rules[index] == '(':
                    if nonterm == 'alpha':
                        begin_states = self.__alpha(begin_states)
                    elif nonterm == 'digit':
                        begin_states = self.__digit(begin_states)
                    elif nonterm == 'digitzero':
                        begin_states = self.__digitzero(begin_states)
                    else:
                        begin_states, _ = self.__expr(begin_states, self.__grammar[nonterm], 0)
                    index += 1
                    state = 6
                elif rules[index] == '{':
                    if nonterm == 'alpha':
                        begin_states = self.__alpha(begin_states)
                    elif nonterm == 'digit':
                        begin_states = self.__digit(begin_states)
                    elif nonterm == 'digitzero':
                        begin_states = self.__digitzero(begin_states)
                    else:
                        begin_states, _ = self.__expr(begin_states, self.__grammar[nonterm], 0)
                    index += 1
                    state = 8
                elif rules[index] == '[':
                    if nonterm == 'alpha':
                        begin_states = self.__alpha(begin_states)
                    elif nonterm == 'digit':
                        begin_states = self.__digit(begin_states)
                    elif nonterm == 'digitzero':
                        begin_states = self.__digitzero(begin_states)
                    else:
                        begin_states, _ = self.__expr(begin_states, self.__grammar[nonterm], 0)
                    index += 1
                    state = 10
                else:
                    if nonterm == 'alpha':
                        begin_states = self.__alpha(begin_states)
                    elif nonterm == 'digit':
                        begin_states = self.__digit(begin_states)
                    elif nonterm == 'digitzero':
                        begin_states = self.__digitzero(begin_states)
                    else:
                        begin_states, _ = self.__expr(begin_states, self.__grammar[nonterm], 0)
                    state = 1000
            elif state == 6:
                begin_states, index = self.__expr(begin_states, rules, index)
                state = 7
            elif state == 7:
                if rules[index] == ')':
                    index += 1
                    state = 12
                else:
                    state = 666
            elif state == 8:
                tmp_begin_states = begin_states.copy()
                tmp_states = self.__fsm.get_children(tmp_begin_states)
                begin_states, index = self.__expr(begin_states, rules, index)
                second_states = list(self.__fsm.get_children(tmp_begin_states) - tmp_states)
                for tmp_state in second_states:
                    edges = self.__fsm.get_input_edges(tmp_state)
                    for end_state in begin_states:
                        for symbol in edges:
                            self.__fsm.add_edge(end_state, tmp_state, symbol)
                begin_states |= tmp_begin_states
                state = 9
            elif state == 9:
                if rules[index] == '}':
                    index += 1
                    state = 12
                else:
                    state = 666
            elif state == 10:
                tmp_states, index = self.__expr(begin_states, rules, index)
                begin_states |= tmp_states
                state = 11
            elif state == 11:
                if rules[index] == ']':
                    index += 1
                    state = 12
                else:
                    state = 666
            elif state == 12:
                if rules[index] == ' ':
                    index += 1
                    state = 12
                elif rules[index] == '|':
                    end_states |= begin_states
                    begin_states = parent_states.copy()
                    index += 1
                    state = 1
                elif rules[index] == '\'':
                    index += 1
                    state = 2
                elif rules[index].isalpha():
                    nonterm = rules[index]
                    index += 1
                    state = 5
                elif rules[index] == '(':
                    index += 1
                    state = 6
                elif rules[index] == '{':
                    index += 1
                    state = 8
                elif rules[index] == '[':
                    index += 1
                    state = 10
                else:
                    state = 1000
        end_states |= begin_states
        return end_states, index

    def __alpha(self, parent_states: set):
        new_state = self.__fsm.get_new_state()
        for parent_state in parent_states:
            self.__fsm.add_edge(parent_state, new_state, '_')
            for i in range(65, 91):
                self.__fsm.add_edge(parent_state, new_state, chr(i))
        return set([new_state])

    def __digit(self, parent_states: set):
        new_state = self.__fsm.get_new_state()
        for parent_state in parent_states:
            for i in range(1, 10):
                self.__fsm.add_edge(parent_state, new_state, str(i))
        return set([new_state])

    def __digitzero(self, parent_states: set):
        new_state = self.__fsm.get_new_state()
        for parent_state in parent_states:
            for i in range(10):
                self.__fsm.add_edge(parent_state, new_state, str(i))
        return set([new_state])

#     def view_graph(self, name: str):
#         uber_dict = self.__fsm.get_graph()
#         g = Digraph('G', filename=name)
#         g.attr('node', shape='Msquare')
#         g.node(self.__fsm.start_state)
#         g.attr('node', shape='doublecircle')
#         for final_state in self.__fsm.final_states:
#             g.node(final_state)
#         g.attr('node', shape='circle')
#         for e, p in uber_dict.items():
#             tmp = p.copy()
#             s1 = ""
#             s2 = ""
#             s3 = ""
#             if self.__set_alpha.issubset(tmp):
#                 s1 = '_|A|...|Z'
#                 tmp -= self.__set_alpha
#             elif len(self.__set_alpha & tmp) > 15:
#                 s1 = '(_|A|...|Z\\\\' + '|'.join(str(i) for i in (self.__set_alpha - tmp)) + ")"
#                 tmp -= self.__set_alpha
#
#             if self.__set_digitzero.issubset(tmp):
#                 s2 = '0|...|9'
#                 tmp -= self.__set_digitzero
#             if self.__set_digit.issubset(tmp):
#                 s3 = '1|...|9'
#                 tmp -= self.__set_digit
#             if ' ' in tmp:
#                 tmp.discard(' ')
#                 tmp.add('space')
#             if '$' in tmp:
#                 tmp.discard('$')
#                 tmp.add('\\\\n')
#             s4 = '|'.join(str(i) for i in tmp)
#             g.edge(e[0], e[1], label='|'.join(filter(None, [s1, s2, s3, s4])))
#         g.view()

    def automata_determination(self):
        self.__fsm.automata_determination()
        self.__fsm.automata_minimize()
        self.__fsm.automata_renumerate()

def replace(str1):
    str1 = str1.replace('///', ' \n')
    str1 = str1.replace('/', ' ')
    return str1

def run():
    number = '17'
#     print(replace(str(sys.argv[1])))
    gr = Worker('start', replace(str(sys.argv[1])))

    gr.automata_determination()


if __name__ == '__main__':
    run()