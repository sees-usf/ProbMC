from z3 import *


def getProperty(k):
    # Defining variables
    a = Bool("a{0}".format(k))
    b = Bool("b{0}".format(k))
    P = Or(Not(a), Not(b))
    return P


def getLiterals():
    return ["a", "b"]


def getTransition(k):
    # Defining variables
    a = Bool("a{0}".format(k))
    b = Bool("b{0}".format(k))
    aPrime = Bool("a{0}".format(k + 1))
    bPrime = Bool("b{0}".format(k + 1))

    # Defining states - original
    st0 = And(Not(a), Not(b))
    st1 = And(Not(a), b)
    st2 = And(a, b)
    st3 = And(a, Not(b))

    # Defining states - prime
    st0_prime = And(Not(aPrime), Not(bPrime))
    st1_prime = And(Not(aPrime), bPrime)
    st2_prime = And(aPrime, bPrime)
    st3_prime = And(aPrime, Not(bPrime))

    T = And(Implies(st0, Or(st1_prime, st2_prime, st3_prime)),
            Implies(st1, Or(st0_prime, st2_prime)),
            Implies(st3, st0_prime)
            )

    # T = Or(And(st0, st1_prime), And(st0, st2_prime), And(st0, st3_prime),
    #        And(st1, st0_prime), And(st1, st2_prime),
    #        And(st3, st0_prime))

    return T


def getInit():
    # Defining variables
    a = Bool("a{0}".format(0))
    b = Bool("b{0}".format(0))

    init = And(Not(a), Not(b))
    return init
