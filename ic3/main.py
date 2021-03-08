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
    frame0.updateSolver()
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
    prevFrame = frame0
    currFrame = frame(model, 1)
    frames = [frame0]
    k = 1

    # BACKWARDS SEARCH
    i = 0  # Var for testing
    while True:
        print("\nCurr frame", currFrame.k, ": ", currFrame.clauses, "\nPrev Frame", prevFrame.k, ": ", prevFrame.clauses)
        # Checks for a bad state
        # Sat( Fk ^ T ^ -P')
        while currFrame.solver.check(Not(currFrame.PPrime)) == sat:
            badStateSolver = currFrame.solver  # Preps solver for being passed as a parameters
            badStateSolver.add(Not(currFrame.PPrime))
            sk = retrieveSk(currFrame, badStateSolver, model.getLiterals())
            print("There is a bad state in frame", currFrame.k , "(BS): ", sk)

            # Tries to find a counterexample
            result, frames, currFrame = findCEX(sk, currFrame, frames, model)
            if result:
                return True

        # If frames match, its the end of ic3
        equivChecker = Solver()
        tempCurrFrame = createPrimeVersion(currFrame.clauses, model.getLiterals(), currFrame.k, -1)
        equivChecker.add(Not(prevFrame.clauses == tempCurrFrame))
        if equivChecker.check() == unsat:
            return False

        # PREPS FOR NEXT ITERATION
        k += 1
        currFrame.updateSolver()
        frames.append(currFrame)
        prevFrame = currFrame
        currFrame = frame(model, k)

        # Controls loop for testing
        i += 1
        if i == 5:
            break
    pass


# Retrieves a bad state s from Fk
def retrieveSk(Fk, skSolver, literals):
    # Input: Transition relation ANDED w/ Not P, the current frame
    # Output: Returns a bad state
    if skSolver.check() == unsat:
        raise ValueError("Passed model is unsatisfiable")
    model = skSolver.model()
    #print(skSolver.model())

    sk = Bool(1)
    for i in range(0, len(literals)):
        tempBool = Bool((str(literals[i]) + "{0}").format(Fk.k))
        if i == 0:
            if model.eval(tempBool):
                sk = tempBool
            else:
                sk = Not(tempBool)
        else:
            if model.eval(tempBool):
                sk = And(sk, tempBool)
            else:
                sk = And(sk, Not(tempBool))

    sk = simplify(sk)

    # Checks to make sure sk is valid
    checker1 = Solver()
    checker1.add(Or(Implies(Not(Fk.P), And(Fk.T, sk))), And(Fk.clauses, Fk.T, createPrimeVersion(sk, literals, Fk.k, 2)))
    if checker1.check() == unsat:
        raise ValueError("Something wrong with sk")

    return sk


def createPrimeVersion(express, literals, k, increment):
    for x in literals:
        tempStr = str(x) + "{0}"
        try:
            express = substitute(express, (Bool(tempStr.format(k)), Bool(tempStr.format(k + increment))))
        except:
            continue
    return express


# Tries to find a counterexample
def findCEX(sk, Fk, frames, model):
    currSolver = Solver()

    # We reached init
    if Fk.k == 0:
        currSolver.add(Implies(sk, model.getInit()))
        if currSolver.check():
            return True, frames, Fk
        else:
            return False, frames, Fk
    else:
        # Finds a bad state in the previous frame
        # (Fk-1 ^ T ^ sk')`
        frames[Fk.k - 1].updateSolver()

        while frames[Fk.k - 1].solver.check(sk) == sat:
            currSolver = frames[Fk.k - 1].solver
            currSolver.add(sk)
            sk_1 = retrieveSk(frames[Fk.k - 1], currSolver, model.getLiterals())

            print("There is a bad state in frame", Fk.k - 1, "(FC): ", sk_1)
            result, frames, Fk = findCEX(sk_1, frames[Fk.k - 1], frames, model)
            if result:
                return result, frames, Fk
            frames[Fk.k - 1].updateSolver()

        # Blocks the clause
        result, frames, Fk = pushForward(Not(sk), Fk, frames, model)
        if result:
            return True, frames, Fk
        #frames = pushBackward(Not(sk), Fk, frames)

    Fk.updateSolver() # Adjusts to new clauses
    for x in frames:
        x.updateSolver()
    return False, frames, Fk


def pushForward(c, Fk, frames, model):
    if Fk.k > len(frames):  # Reached the most recent frame
        return False, frames

    currChecker = Fk.solver
    currChecker.add(c)
    currChecker.add(createPrimeVersion(Not(c), model.getLiterals(), Fk.k, 1))
    if currChecker.check() == sat:  # Has a proof obligation
        sj = retrieveSk(Fk, currChecker, model.getLiterals())
        result, frames, Fk = findCEX(sj, Fk, frames, model)
        if result:
            return True, frames, Fk
    else:
        Fk.clauses = simplify(And(Fk.clauses, c))
        if Fk.k == len(frames):
            return False, frames, Fk
        return pushForward(c, frames[Fk.k+1], frames, model)


# TO be done
def pushBackward(c, Fk, frames):
    currChecker = Solver()


def generalization(c, k):
    pass


# Prints Result
def printResult(result):
    if isinstance(result, bool):
        if result:
            print("There is a counterexample")
        else:
            print("P is verified")
    else:
        print("Inconclusive")


# Starts IC3
print("**MODEL 1**")
printResult(ic3(importlib.import_module("sampleModel1")))

print("\n**MODEL 2**")
printResult(ic3(importlib.import_module("sampleModel2")))

print("\n**MODEL 3**")
printResult(ic3(importlib.import_module("sampleModel3")))

print("\n**MODEL 4**")
printResult(ic3(importlib.import_module("sampleModel4")))

print("\n**MODEL 5**")
printResult(ic3(importlib.import_module("sampleModel5")))

print("\n**MODEL 6**")
printResult(ic3(importlib.import_module("sampleModel6")))
