from z3 import *


def getProperty(k):
    # Defining variables
    a = Bool("a{0}".format(k))
    b = Bool("b{0}".format(k))
    c = Bool("c{0}".format(k))
    d = Bool("d{0}".format(k))
    P = Or(Not(b), Not(c), Not(d))
    return P


def getVariables():
    return ["a", "b", "c", "d"]


def getTransition(k):
    # Defining variables
    a = Bool("a{0}".format(k))
    b = Bool("b{0}".format(k))
    c = Bool("c{0}".format(k))
    d = Bool("d{0}".format(k))
    aPrime = Bool("a{0}".format(k + 1))
    bPrime = Bool("b{0}".format(k + 1))
    cPrime = Bool("c{0}".format(k + 1))
    dPrime = Bool("d{0}".format(k + 1))

    # Defining states - original
    st0 = And(Not(a), Not(b), Not(c), Not(d))
    st1 = And(Not(a), Not(b), Not(c), d)
    st2 = And(Not(a), Not(b), c, Not(d))
    st3 = And(Not(a), Not(b), c, d)
    st4 = And(Not(a), b, Not(c), Not(d))
    st5 = And(Not(a), b, Not(c), d)
    st6 = And(Not(a), b, c, Not(d))
    st7 = And(Not(a), b, c, d)

    # Defining states - prime
    st0_prime = And(Not(aPrime), Not(bPrime), Not(cPrime), Not(dPrime))
    st1_prime = And(Not(aPrime), Not(bPrime), Not(cPrime), dPrime)
    st2_prime = And(Not(aPrime), Not(bPrime), cPrime, Not(dPrime))
    st3_prime = And(Not(aPrime), Not(bPrime), cPrime, dPrime)
    st4_prime = And(Not(aPrime), bPrime, Not(cPrime), Not(dPrime))
    st5_prime = And(Not(aPrime), bPrime, Not(cPrime), dPrime)
    st6_prime = And(Not(aPrime), bPrime, cPrime, Not(dPrime))
    st7_prime = And(Not(aPrime), bPrime, cPrime, dPrime)

    # T = Or(And(st0, st1_prime),
    #        And(st1, st2_prime),
    #        And(st2, st3_prime),
    #        And(st3, st4_prime),
    #        And(st4, st5_prime),
    #        And(st5, st0_prime),
    #        And(st6, st7_prime),
    #        And(st7, st0_prime))

    T = And(Implies(st0, st1_prime),
            Implies(st1, st2_prime),
            Implies(st2, st3_prime),
            Implies(st3, st4_prime),
            Implies(st4, st5_prime),
            Implies(st5, st0_prime),
            Implies(st6, st7_prime),
            Implies(st7, st0_prime))

    return T


def getInit():
    # Defining variables
    a = Bool("a{0}".format(0))
    b = Bool("b{0}".format(0))
    c = Bool("c{0}".format(0))
    d = Bool("d{0}".format(0))

    init = And(Not(a), Not(b), Not(c), Not(d))
    return init