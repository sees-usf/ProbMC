class model:
    states = []
    # list of list. each item : [source, destination, rate]
    transitions = []
    initial_state = 0
    r_matrix = list()

    def __init__(self, states=None, transitions=None, initial_state=None):
        if states is None:
            self.states = [0, 1, 2]
        else:
            self.states = states
        if transitions is None:
            self.transitions = [[0, 1, 1], [0, 2, 999], [2, 0, 5]]
        else:
            self.transitions = transitions
        if initial_state is None:
            self.initial_state = 0
        else:
            self.initial_state = initial_state
        for i in range(len(self.states)):
            temp = list()
            for j in range(len(self.states)):
                temp.append(0)
            self.r_matrix.append(temp)
        for e in self.transitions:
            s = e[0]
            d = e[1]
            r = e[2]
            self.r_matrix[s][d] = r