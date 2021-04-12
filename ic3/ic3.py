from frame import *
from z3 import *
import importlib


# Runs IC3
def ic3(model):
    # Input: model
    # Output: Boolean representing counter example or not
    # True = there is a counterexample; False =  There is not a counterexample

    # INITIAL CHECKING
    # Checking Sat(init ^ -P)
    frame0 = frame(model, 0)
    frame0.clauses = model.getInit()
    frame0.solver = Solver()
    frame0.updateSolver(And(frame0.clauses, frame0.T))
    if frame0.solver.check(Not(frame0.P)) == sat:
        print("First Check failed")
        return True
    else:
        print('F0 -> P holds')

    # Checking Sat(init ^ T ^ -P')
    if frame0.solver.check(Not(frame0.PPrime)) == sat:
        print("Second check failed")
        return True
    else:
        print('F0 /\ T -> P holds')

    # INITIALIZATION FOR BACKWARDS SEARCH
    equivChecker = Solver()
    prevFrame = frame0
    currFrame = frame(model, 1)
    frames = [frame0]
    k = 1

    # BACKWARDS SEARCH
    i = 0  # Var for testing
    cex = []
    while True:
        print("\nIteration", prevFrame.index, "\nCurr frame", currFrame.index, ": ", currFrame.clauses, "\nPrev Frame", prevFrame.index, ": ",
              prevFrame.clauses)

        # Checks for a bad state
        # Sat( Fk ^ T ^ -P')
        while currFrame.solver.check(Not(currFrame.PPrime)) == sat:
            sk = currFrame.findPredState(Not(currFrame.PPrime))
            print("There is a bad state in frame", currFrame.index, "(BS): ", sk)

            # Tries to find a counterexample
            cex.append(sk)
            result, frames, currFrame = findCEX(sk, currFrame, frames, cex)
            if result:
                printCEX(cex, currFrame)
                return True
            else:
                cex.pop()

        # If frames match, its the end of ic3
        tempCurrFrame = createPrimeVersion(currFrame.clauses, model.getLiterals(), currFrame.index, -1)
        if equivChecker.check(Not(prevFrame.clauses == tempCurrFrame)) == unsat:
            print("P is verified")
            return False

        # PREPS FOR NEXT ITERATION
        k += 1
        frames.append(currFrame)
        prevFrame = currFrame
        currFrame = frame(model, k)

        # Control loop for testing
        #i += 1
        #if i == 5:
        #    break
    pass


# Tries to find a counterexample
def findCEX(sk, Fk, frames, cex):
    # We reached init
    if Fk.index == 0:
        return True, frames, Fk
    else:
        # Finds a bad state in the previous frame
        # (Fk-1 ^ T ^ sk')
        while frames[Fk.index - 1].solver.check(sk) == sat:
            sk_1 = frames[Fk.index - 1].findPredState(sk)

            print("In frame", Fk.index, ", we found that there is a bad state in frame", Fk.index - 1, "(FC): ", sk_1)
            cex.append(sk_1)
            result,frames, tempFk = findCEX(sk_1, frames[Fk.index - 1], frames, cex)
            if result:
                return result, frames, Fk  # add cex here
            else:
                cex.pop()  # remove sk_1 from cex

        # upon termination, none of states in frame k-1, sk_1, is added into cex

        # Blocks the clause
        Fk.clauses = And(Fk.clauses, Not(sk))
        Fk.updateSolver(Not(sk))
        #frames = pushBackward(Not(sk), frames)
        return False, frames, Fk  # add cex here


def createPrimeVersion(express, literals, index, increment):
    for x in literals:
        tempStr = str(x) + "{0}"
        try:
            express = substitute(express, (Bool(tempStr.format(index)), Bool(tempStr.format(index + increment))))
        except:
            continue
    return express


# TO be done
def pushForward(c, Fk, frames, model):
    pass


def pushBackward(c, frames):
    for f in frames:
        f.clauses = And(f.clauses, c)
        f.updateSolver(c)
    return frames


def generalization(c, k):
    pass


def printCEX(stateList, frame):
    print("\nCOUNTEREXAMPLE\n", simplify(Not(frame.P)))
    for x in stateList:
        print(x)


