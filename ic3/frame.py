from z3 import *


class frame:
    def __init__(self, model, inputK):
        self.clauses = model.getProperty(inputK)
        self.index = inputK
        self.T = model.getTransition(inputK)
        self.P = model.getProperty(inputK)
        self.PPrime = model.getProperty(inputK+1)
        self.solver = Solver()  # Each solver contains the clauses and the transition
        self.solver.add(self.clauses)
        self.solver.add(self.T)

    # Updates the solver to work with new clauses
    def updateSolver(self):
        self.solver = Solver()
        self.solver.add(self.clauses)
        self.solver.add(self.T)

    def printFrame(self):
        print("Frame", self.index, "contains: ", self.clauses)