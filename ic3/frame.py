from z3 import *


class frame:
    def __init__(self, model, frameIndex):
        # Data Members init
        self.variables = model.getVariables()
        self.index = frameIndex
        self.T = model.getTransition(frameIndex)
        self.P = model.getProperty(frameIndex)
        self.PPrime = model.getProperty(frameIndex + 1)

        # Mutually exclusive blocking clauses list
        self.clauses = self.P
        self.blockingCList = [self.P]  # blocking clauses
        self.inductiveCList = []  # blocking and inductive classes

        # Solver Init
        self.solver = Solver()  # Each solver contains the clauses and the transition
        self.solver.add(self.P)
        self.solver.add(self.T)

    # Updates the solver to work with new clauses
    def updateSolver(self, newClause):
        self.solver.add(newClause)

    # Prints the frame information
    def printFrame(self):
        print("Frame", self.index, "contains: ", self.clauses)

    # Finds and returns a state 'curState' of this frame such that 'curState ^ T -> targetState' holds
    def findPredState(self, targetState):
        # targetState is in prime format
        if self.solver.check(targetState) == unsat:
            return False

        # Creates a model
        model = self.solver.model()

        # Creates the curState to be returned
        curState = True
        for i in range(0, len(self.variables)):
            tempBool = Bool((str(self.variables[i]) + "{0}").format(self.index))
            if model[tempBool] is None:
                continue
            if model.eval(tempBool):
                curState = And(curState, tempBool)
            else:
                curState = And(curState, Not(tempBool))

        curState = simplify(curState)

        return curState

    # return a state 's' if 's ^ T ^ Not(PPrim)' holds; otherwise return Bool(0)
    def checkProperty(self):
        return self.findPredState(Not(self.PPrime))