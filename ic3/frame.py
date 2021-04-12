from z3 import *


class frame:
    def __init__(self, model, frameIndex):
        self.literals = model.getLiterals()
        
        initBlockingCl = model.getProperty(frameIndex)
        
        # the two lists below are mutual exclusive
        self.blockingCList = [] # blocking clauses
        self.inductiveClist = [] # blocking and inductive clasues
        
        self.blockingClist.append(initBlockingCl)
        self.index = frameIndex
        self.T = model.getTransition(self.index)
        self.P = model.getProperty(self.index)
        self.PPrime = model.getProperty(self.index + 1)
        self.solver = Solver()  # Each solver contains the clauses and the transition
        self.solver.add(initBlockingCl)
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
        for i in range(0, len(self.literals)):
            tempBool = Bool((str(self.literals[i]) + "{0}").format(self.index))
            if not isinstance(model.eval(tempBool), bool):
                print("ISSUE WITH TYPE\n", model)
                print(tempBool, model.eval(tempBool), type(model.eval(tempBool)))
            if model.eval(tempBool):
                curState = And(curState, tempBool)
            else:
                curState = And(curState, Not(tempBool))

        curState = simplify(curState)

        # Checks to make sure sk is valid
        # checker1 = Solver()
        # checker1.add(Or(Implies(Not(Fk.P), And(Fk.T, sk))), And(Fk.clauses, Fk.T, createPrimeVersion(sk, literals, Fk.index, 2)))
        # if checker1.check() == unsat:
        #    raise ValueError("Something wrong with sk")

        return curState

    # return a state 's' if 's ^ T ^ Not(PPrim)' holds; otherwise return Bool(0)
    def checkProperty(self):
        return self.findPredState(Not(self.PPrime))
