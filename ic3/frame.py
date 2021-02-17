from z3 import *


class frame:
    def __init__(self, model, inputK):
        self.clauses = model.getProperty(inputK)
        self.k = inputK
        self.T = model.getTransition(inputK)
        self.P = model.getProperty(inputK)
        self.PPrime = model.getProperty(inputK+1)
        self.solver = Solver()
