from z3 import *


class frame:
    def __init__(self, model, frameIndex):
        self.literals = [] ## initialize it using model and self.index
        self.clauses = model.getProperty(inputK)
        self.index = frameIndex
        self.T = model.getTransition(self.index)
        self.P = model.getProperty(self.index)
        self.PPrime = model.getProperty(self.index+1)
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
        
    # find and return a state 'curState' of this frame such that 'curState ^ T -> targetState' holds
    def findPredState(targetState, literals):
        # targetState is in prime format
        if self.sovler.check(targetState) == unsat:
            return Bool(0)
        
        curState = Bool(1)
        for i in range(0, len(literals)):
            tempBool = Bool((str(literals[i]) + "{0}").format(self.index))
            if model.eval(tempBool):
                curState = And(curState, tempBool)
            else:
                curState = And(curState, Not(tempBool))

        curState = simplify(curState)

        # Checks to make sure sk is valid
        #checker1 = Solver()
        #checker1.add(Or(Implies(Not(Fk.P), And(Fk.T, sk))), And(Fk.clauses, Fk.T, createPrimeVersion(sk, literals, Fk.index, 2)))
        #if checker1.check() == unsat:
        #    raise ValueError("Something wrong with sk")

        return curState
