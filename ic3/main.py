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
            sk = retrieveSk(currFrame, badStateSolver)
            print("There is a bad state: ", sk)
            result, frames = findCEX(sk, currFrame, frames, model)
            if result:
                return True
            break

        # If frames match, its the end of ic3
        # TODO mention this issue
        equivChecker = Solver()
        tempCurrFrame = createPrimeVersion(currFrame.clauses, currFrame.k - 2)
        equivChecker.add(Not(prevFrame.clauses == tempCurrFrame))

        # equivChecker.add(Not(prevFrame.clauses == currFrame.clauses))
        # print(prevFrame.clauses, currFrame.clauses)
        if equivChecker.check() == unsat:
            return False
        # else:
        #    print("Not the same", equivChecker.model())

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
def retrieveSk(Fk, skSolver):
    # Input: Transition relation ANDED w/ Not P, the current frame
    # Output: Returns a bad state

    model = skSolver.model()
    print(skSolver.model())

    a = Bool("a{0}".format(Fk.k))
    b = Bool("b{0}".format(Fk.k))

    # TODO needs to be reworked for larger models
    if not model.eval(a) and not model.eval(b):
        sk = And(Not(a), Not(b))
    elif not model.eval(a):
        sk = And(Not(a), b)
    elif not model.eval(b):
        sk = And(a, Not(b))
    else:
        sk = And(a, b)

    checker1 = Solver()
    checker1.add(Implies(Not(Fk.P), And(Fk.T, sk)))
    checker2 = Solver()
    checker2.add(And(Fk.clauses, Fk.T, createPrimeVersion(sk, Fk.k+1)))

    if checker1.check() == unsat or checker2.check() == unsat:
        raise ValueError("Something wrong with sk")

    return sk


# Tries to find a counterexample
def findCEX(sk, Fk, frames, model):
    currSolver = Solver()

    # We reached init
    if Fk.k == 0:
        currSolver.add(Implies(sk, Fk.clauses))
        if currSolver.check():
            return True, frames
        else:
            return False, frames
    else:
        # Finds a bad state in the previous frame
        # (Fk-1 ^ T ^ sk')
        currSolver.add(frames[Fk.k - 1].clauses)
        currSolver.add(frames[Fk.k - 1].T)
        currSolver.add(createPrimeVersion(sk, Fk.k))

        # TODO figure out model 2
        while currSolver.check() == sat:
            sk_1 = retrieveSk(frames[Fk.k - 1], currSolver)
            print("The previous bad state is", sk_1)
            result, frames = findCEX(sk_1, frames[Fk.k-1], frames, model)
            if result:
                return result, frames

        # Blocks the clause
        #frames = pushForward(Not(sk), Fk, frames)


    return False, frames


def pushForward(c, Fk, frames):
    currChecker = Solver()



def createPrimeVersion(express, k):
    expressSolver = Solver()
    expressSolver.add(express)
    expressSolver.check()
    modelExpress = expressSolver.model()
    modelExpress = list(modelExpress)

    tempExpress = substitute(express, (Bool(str(modelExpress[0])), Bool("a{0}".format(k + 1))))
    tempExpress = substitute(tempExpress, (Bool(str(modelExpress[1])), Bool("b{0}".format(k + 1))))

    return tempExpress


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