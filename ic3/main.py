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
        print("\nCurr frame", currFrame.index, ": ", currFrame.clauses, "\nPrev Frame", prevFrame.index, ": ", prevFrame.clauses)
        # Checks for a bad stat
        # Sat( Fk ^ T ^ -P')
        while currFrame.solver.check(Not(currFrame.PPrime)) == sat:
            badStateSolver = currFrame.solver  # Preps solver for being passed as a parameters
            badStateSolver.add(Not(currFrame.PPrime))
            sk = retrieveSk(currFrame, badStateSolver, model.getLiterals())
            print("There is a bad state in frame", currFrame.index , "(BS): ", sk)

            # Tries to find a counterexample
            result, frames, currFrame = findCEX(sk, currFrame, frames, model)
            print("PREV FRAME", prevFrame.index, "CURR", currFrame.index)
            if result:
                #printCEX()
                return True
        # If frames match, its the end of ic3
        equivChecker = Solver()
        tempCurrFrame = createPrimeVersion(currFrame.clauses, model.getLiterals(), currFrame.index, -1)
        equivChecker.add(Not(prevFrame.clauses == tempCurrFrame))
        if equivChecker.check() == unsat:
            return False

        # PREPS FOR NEXT ITERATION
        k += 1
        print("PREV FRAME", prevFrame.index, "CURR", currFrame.index)
        currFrame.updateSolver()
        frames.append(currFrame)
        print("PREV FRAME", prevFrame.index, "CURR", currFrame.index)
        prevFrame = currFrame
        print("PREV FRAME", prevFrame.index, "CURR", currFrame.index)
        currFrame = frame(model, k)
        print("PREV FRAME", prevFrame.index, "CURR", currFrame.index)

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
        tempBool = Bool((str(literals[i]) + "{0}").format(Fk.index))
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
    checker1.add(Or(Implies(Not(Fk.P), And(Fk.T, sk))), And(Fk.clauses, Fk.T, createPrimeVersion(sk, literals, Fk.index, 2)))
    if checker1.check() == unsat:
        raise ValueError("Something wrong with sk")

    return sk


def createPrimeVersion(express, literals, index, increment):
    for x in literals:
        tempStr = str(x) + "{0}"
        try:
            express = substitute(express, (Bool(tempStr.format(index)), Bool(tempStr.format(index + increment))))
        except:
            continue
    return express


# Tries to find a counterexample
def findCEX(sk, Fk, frames, model):
    currSolver = Solver()

    # We reached init
    if Fk.index == 0:
        if currSolver.check(Implies(sk, Fk.clauses)):
            return True, frames, Fk
        else:
            return False, frames, Fk
    else:
        # Finds a bad state in the previous frame
        # (Fk-1 ^ T ^ sk')
        #print(currSolver.check(And(frames[Fk.index - 1].clauses, sk, frames[Fk.index - 1].T)))
        #frames[Fk.index - 1].solver.check(sk)
        while currSolver.check(And(frames[Fk.index - 1].clauses, sk, frames[Fk.index - 1].T)) == sat:
            currSolver = frames[Fk.index - 1].solver
            currSolver.add(sk)
            sk_1 = retrieveSk(frames[Fk.index - 1], currSolver, model.getLiterals())

            print("In frame", Fk.index, ", we found that there is a bad state in frame", Fk.index - 1, "(FC): ", sk_1)
            result, frames, Fk = findCEX(sk_1, frames[Fk.index - 1], frames, model)
            if result:
                return result, frames, Fk
            frames[Fk.index - 1].updateSolver()

        # Blocks the clause
        #result, frames, Fk = pushForward(Not(sk), Fk, frames, model)
        Fk.clauses = And(Fk.clauses, Not(sk))
        #if result:
          #return True, frames, Fk
        #frames = pushBackward(Not(sk), Fk, frames)

    Fk.updateSolver() # Adjusts to new clauses
    for x in frames:
        x.updateSolver()
    return False, frames, Fk


def pushForward(c, Fk, frames, model):
    print("pushForward")
    if Fk.k > len(frames):  # Reached the most recent frame
        return False, frames

    currChecker = Fk.solver
    currChecker.add(c)
    currChecker.add(createPrimeVersion(Not(c), model.getLiterals(), Fk.index, 1))
    if currChecker.check() == sat:  # Has a proof obligation
        sj = retrieveSk(Fk, currChecker, model.getLiterals())
        print("I have enter proof obligation")
        result, frames, Fk = findCEX(sj, Fk, frames, model)

        if result:
            return True, frames, Fk
    else:
        Fk.clauses = simplify(And(Fk.clauses, c))
        #print(Fk.k, "Len:", len(frames), Fk.clauses)
        if Fk.index + 1 >= len(frames):
            return False, frames, Fk
        else:
            #print("In else statement", Fk.k, len(frames))
            return pushForward(c, frames[Fk.index + 1], frames, model)


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

#def printCEX(stateList):
#    pass


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

print("\n**MODEL 7**")
printResult(ic3(importlib.import_module("sampleModel7")))
