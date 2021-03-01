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
    initCheckSolver1 = Solver()
    initCheckSolver1.add(Not(frame0.P))
    initCheckSolver1.add(model.getInit())
    if initCheckSolver1.check() == sat:
        print("First Check failed", initCheckSolver1.model())
        return True
    else:
        print('F0 -> P holds')

    # Checking Sat(init ^ T ^ -P')
    initCheckSolver2 = Solver()
    initCheckSolver2.add(model.getInit())
    initCheckSolver2.add(frame0.T)
    initCheckSolver2.add(Not(frame0.PPrime))
    if initCheckSolver2.check() == sat:
        print("Second check failed", initCheckSolver2.model())
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
        print("\nCurr frame:", currFrame.k, "Prev Frame", prevFrame.k)
        # Checks for a bad state
        # Sat( Fk ^ T ^ -P')
        badStateSolver = Solver()
        badStateSolver.add(currFrame.clauses)
        badStateSolver.add(currFrame.T)
        badStateSolver.add(Not(currFrame.PPrime))

        while badStateSolver.check() == sat:
            sk = retrieveSk(currFrame, badStateSolver, model.getLiterals())
            print("There is a bad state: ", sk)
            result, frames, currFrame = findCEX(sk, currFrame, frames, model)
            if result:
                return True
            break

        # If frames match, its the end of ic3
        equivChecker = Solver()
        tempCurrFrame = createPrimeVersion(currFrame.clauses, model.getLiterals(), currFrame.k, -1)
        equivChecker.add(Not(prevFrame.clauses == tempCurrFrame))
        if equivChecker.check() == unsat:
            return False

        # PREPS FOR NEXT ITERATION
        k += 1
        frames.append(prevFrame)
        prevFrame = currFrame
        currFrame = frame(model, k)

        # Controls loop for testing
        i += 1
        if i == 3:
            break
    pass


# Retrieves a bad state s from Fk
def retrieveSk(Fk, skSolver, literals):
    # Input: Transition relation ANDED w/ Not P, the current frame
    # Output: Returns a bad state

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
    checker1 = Solver()
    checker1.add(Implies(Not(Fk.P), And(Fk.T, sk)))
    checker2 = Solver()
    checker2.add(And(Fk.clauses, Fk.T, createPrimeVersion(sk, literals, Fk.k, 2)))

    if checker1.check() == unsat or checker2.check() == unsat:
        raise ValueError("Something wrong with sk")

    return sk


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
        # (Fk-1 ^ T ^ sk')
        currSolver.add(frames[Fk.k - 1].clauses)
        #currSolver.add(frames[Fk.k - 1].T)
        currSolver.add(Fk.T)
        currSolver.add(createPrimeVersion(sk, model.getLiterals(), Fk.k, 1))

        while currSolver.check() == sat:
            print(Fk.k, frames[Fk.k-1].clauses)
            sk_1 = retrieveSk(frames[Fk.k - 1], currSolver, model.getLiterals())
            print("The previous bad state is", sk_1)
            result, frames, Fk = findCEX(sk_1, frames[Fk.k - 1], frames, model)
            if result:
                return result, frames, Fk

        # Blocks the clause
        result, frames, Fk = pushForward(Not(sk), Fk, frames, model)
        if result:
            return True, frames, Fk
        #frames = pushBackward(Not(sk), Fk, frames)

    return False, frames, Fk


def pushForward(c, Fk, frames, model):
    if Fk.k > len(frames): # Reached the most recent frame
        return False, frames

    currChecker = Solver()
    currChecker.add(c)
    currChecker.add(createPrimeVersion(Not(c), model.getLiterals(), Fk.k, 1))
    currChecker.add(Fk.clauses)
    currChecker.add(Fk.T)
    if currChecker.check() == sat:
        sj = retrieveSk(Fk, currChecker, model.getLiterals())
        result, frames = findCEX(sj, Fk, frames, model)
        if result:
            return True, frames
    else:
        Fk.clauses = simplify(And(Fk.clauses, c))
        if Fk.k == len(frames):
            return False, frames, Fk
        return pushForward(c, frames[Fk.k+1], frames, model)



def pushBackward(c, Fk, frames):
    currChecker = Solver()

def createPrimeVersion(express, literals, k, increment):
    for x in literals:
        tempStr = str(x) + "{0}"
        try:
            express = substitute(express, (Bool(tempStr.format(k)), Bool(tempStr.format(k + increment))))
        except:
            continue
    return express


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
