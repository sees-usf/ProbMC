from z3 import *

def ic3():
    # Input: Transition Relation, initial State, Property
    # Output: Boolean representing counter example or not
    # True = there is a counterexample; False =  There is not a counterexample

    # MODEL INFO - hardcoded
    aInit = Bool('a0')
    bInit = Bool('b0')
    init = And(Not(aInit), Not(bInit))
    # Create a new instance of solver
    T, P, PPrime = defineTransitionAndProperty(0)
    print(T)

    # INITIAL CHECKING
    # Checking Sat(init ^ -P)
    s0 = Solver()
    s0.add(init)
    s0.add(Not(P))
    if s0.check() == sat:
        print("First Check failed", s0.model())
        return True

    # Checking Sat(init ^ T ^ -P')
    print("Checking solver")
    s = Solver()
    s.add(init)
    print(init)
    #s.add(T)
    #print(T)
    s.add(Not(PPrime))
    temp = Not(PPrime)
    #simplify(Not(PPrime))
    print("Simplified not p prime", simplify(temp))
    #print(Not(PPrime))
    if s.check() == sat:  # TODO figure out issue
        print("Second check failed", s.model())
        return True
    pass

    # INITIALIZATION FOR BACKWARDS SEARCH
    Fpk = init  # Fpk = previous frame k
    Fk = P
    frames = [Fpk]  # Keeps track of frames
    k = 1

    # BACKWARDS SEARCH
    # Temp variables for testing
    i = 0
    while True:
        # Checks for bad state
        s = Solver()
        T, P, PPrime = defineTransitionAndProperty(k)
        s.add(Fk)
        s.add(T)
        s.add(Not(PPrime))
        frames.append(Fk)

        print("\nCurr:", Fk, ", Prev:", Fpk)
        # Checks and handles a bad state
        while s.check() == sat:
            sk = retrieveSk(T, P, s, k)
            result, frames = findCEX(sk, Fk, frames, k, init)
            if result:
                return True
            break


        if Fk == Fpk:  # Frames match so it's the end of IC3
            return False

        # CREATES NEXT FRAME & PREPS FOR NEXT ITERATION
        Fpk = frames[k-1]
        Fk = P
        k += 1

        # TODO remove because if none exists it must go into next iteration
        if i == 2:
            break
        i += 1
    pass


# Defines a new transition relation
def defineTransitionAndProperty(k):
    # New variables
    a = Bool("a{0}".format(k))
    b = Bool("b{0}".format(k))
    aPrime = Bool("a{0}".format(k + 1))
    bPrime = Bool("b{0}".format(k + 1))



    """T = And(Implies(And(Not(a), Not(b)), And(Not(aPrime), bPrime)),
            Implies(And(Not(a), Not(b)), And(aPrime, bPrime)),
            Implies(And(Not(a), Not(b)), And(aPrime, Not(bPrime))),
            Implies(And(Not(a), b), And(Not(aPrime), Not(bPrime))),
            Implies(And(Not(a), b), And(aPrime, bPrime)),
            Implies(And(a, Not(b)), And(Not(aPrime), Not(bPrime))),
            )"""

    st0 = And(Not(a), Not(b))

    st0_prime = And(Not(aPrime), Not(bPrime))

    st1 = And(Not(a), b)

    st1_prime = And(Not(aPrime), bPrime)

    st2 = And(a, b)

    st2_prime = And(aPrime, bPrime)

    st3 = And(a, Not(b))

    st3_prime = And(aPrime, Not(bPrime))

    T = And(Implies(st0, Or(st1_prime, st2_prime, st3_prime)),
            Implies(st1, Or(st0_prime, st2_prime)),
            Implies(st3, st0_prime)
            )

    PPrime = Or(Not(aPrime), Not(bPrime))
    P = Or(Not(a), Not(b))

    return T, P, PPrime


# Retrieves a bad state s from Fk
def retrieveSk(T, P, s, k):
    # Input: Transition relation ANDED w/ Not P, the current frame
    # Output: Returns a bad state

    model = s.model()
    print(s.model())

    a = Bool("a{0}".format(k))
    b = Bool("b{0}".format(k))

    # TODO needs to be reworked for larger models
    if not model.eval(a) and not model.eval(b):
        sk = And(Not(a), Not(b))
    elif not model.eval(a):
        sk = And(Not(a), b)
    elif not model.eval(b):
        sk = And(a, Not(b))
    else:
        sk = And(a, b)

    s = Solver()
    s.add(sk)
    s.add(T)
    s.add(Not(P))
    if s.check() != sat:
        raise ValueError("Something wrong with sk")

    return sk


# Tries to find a counterexample
def findCEX(sk, Fk, frames, k, init):
    # Input: bad state, current frame, list of previous frames
    # Output: a boolean representing if there was a CEX or not
    print("Finding a counterexample/blocking for frame", k)
    if k == 0:
        s = Solver()
        s.add(Implies(sk, frames[0]))
        if s.check() == sat:
            print("There is a counterexample as found by the findCEX function")
            return True, frames
        else:
            print("There wasn't counterexample as found by the findCEX function")
            return False, frames
    else:
        # Find a bad state in previous
        s = Solver()
        print("Finding a bad state in previous frame...")
        # (Fk-1 ^ T ^ sk')
        T, P, PPrime = defineTransitionAndProperty(k-1)  # Trying to find a predecessor
        s.add(frames[k-1])
        s.add(T)
        s.add(sk)

        # If no predecessor state
        # Lets block
        if s.check() == unsat:
            print("There isn't a predecessor in frame", k, "\nNow blocking with", Not(sk),"...")
            c = Not(sk)
            frames[k-1] = And(Fk, c)
            frames, result = pushForward(c, k-1, frames, init)
            if result:
                return True, frames
            return False, frames


        while s.check() == sat:   # While there is a predecessor
            print("There is a predecessor in frame", k-1)
            newSk = retrieveSk(T, P, s, k-1) # We get that predecessor from the previous frame
            result, frames = findCEX(newSk, frames[k-1], frames, k-1, init)  # The recurr on the predecessor
            if result:
                return True, frames  # We found a counterexample
        return False, frames  # Solved all our problems
    pass


def pushForward(c, k , frames, init):
    # Input: c - a blocking clause, k - the current frame, frames -  a list of all frames
    # Output: boolean - indicating a counterexample or not frames - a list of frames where the blocking clause is applied
    # Does Fj ^ c ^ T -> c'
    if k == len(frames):
        return frames

    s = Solver()
    s.add(frames[k])
    s.add(c)
    T = defineTransitionAndProperty(k)
    s.add(T)
    cPrime = createCPrime(c, k)
    s.add(Not(cPrime))  # TODO fix it its not right

    if s.check() == sat:
        print("We have a proof obligation for IC3")
        # Find sk
        sj = findsj(T, s, c, cPrime, k)
        # if findCEX(sk, frames[k-1], frames, k, init):
        #   return True, frames
        return frames, False
    else:
        frames[k] = And(frames[k], c)
        pushForward(c, k+1, frames, init)
        return frames, False


# Creates cPrime
def createCPrime(c, k):
    print("Creating cPrime...")
    cSolver = Solver()
    cSolver.add(c)
    cSolver.check()
    modelC = cSolver.model()
    modelC = list(modelC)
    tempC = c

    tempC = substitute(c, (Bool(str(modelC[0])), Bool("a{0}".format(k + 1))))
    tempC = substitute(tempC, (Bool(str(modelC[1])), Bool("b{0}".format(k + 1))))


    print("cPrime is", tempC)
    return tempC


def findsj(T, s, c, cPrime, k):
    # Input:
    # Output: Returns a bad state

    model = s.model()
    print("Finding sj...\n", s.model())

    a = Bool("a{0}".format(k))
    b = Bool("b{0}".format(k))

    # TODO needs to be reworked for larger models
    if not model.eval(a) and not model.eval(b):
        sk = And(Not(a), Not(b))
    elif not model.eval(a):
        sk = And(Not(a), b)
    elif not model.eval(b):
        sk = And(a, Not(b))
    else:
        sk = And(a, b)

    s = Solver()
    s.add(sk)
    s.add(T)
    s.add(c)
    s.add(Not(cPrime))
    if s.check() != sat:
        raise ValueError("Something wrong with sj")

    return sk

# Model Information - BackUp
"""a = Bool('a')
b = Bool('b')
aPrime = Bool('ap')
bPrime = Bool('bPrime')
T = And(Implies(And(Not(a), Not(b)), And(Not(aPrime), bPrime)),
            Implies(And(Not(a), Not(b)), And(aPrime, bPrime)),
            Implies(And(Not(a), Not(b)), And(aPrime, Not(bPrime))),
            Implies(And(Not(a), b), And(Not(aPrime), Not(bPrime))),
            Implies(And(Not(a), b), And(aPrime, bPrime)),
            Implies(And(a, Not(b)), And(Not(aPrime), Not(bPrime))),
            )
initialState = And(Not(a), Not(b))
prop = Or(Not(a), Not(b))"""

result = ic3()

# Prints Result
if isinstance(result, bool):
    if result:
        print("There is a counterexample")
    else:
        print("P is verified")
else:
    print("Inconclusive")