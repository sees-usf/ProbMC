from z3 import *


def getProperty(k):
    # Defining variables
    a = Bool("a{0}".format(k))
    b = Bool("b{0}".format(k))
    c = Bool("c{0}".format(k))
    P = Or(Not(a), Not(b), Not(c))
    return P


def getLiterals():
    return ["a", "b", "c"]


def getTransition(k):
    # Defining variables
    a = Bool("a{0}".format(k))
    b = Bool("b{0}".format(k))
    c = Bool("c{0}".format(k))
    aPrime = Bool("a{0}".format(k + 1))
    bPrime = Bool("b{0}".format(k + 1))
    cPrime = Bool("c{0}".format(k + 1))

    # Defining states - original
    st0 = And(Not(a), Not(b), Not(c))
    st1 = And(Not(a), Not(b), c)
    st2 = And(Not(a), b, Not(c))
    st3 = And(Not(a), b, c)
    st4 = And(a, Not(b), Not(c))
    st5 = And(a, Not(b), c)
    st6 = And(a, b, Not(c))
    st7 = And(a, b, c)

    # Defining states - prime
    st0_prime = And(Not(aPrime), Not(bPrime), Not(cPrime))
    st1_prime = And(Not(aPrime), Not(bPrime), cPrime)
    st2_prime = And(Not(aPrime), bPrime, Not(cPrime))
    st3_prime = And(Not(aPrime), bPrime, cPrime)
    st4_prime = And(aPrime, Not(bPrime), Not(cPrime))
    st5_prime = And(aPrime, Not(bPrime), cPrime)
    st6_prime = And(aPrime, bPrime, Not(cPrime))
    st7_prime = And(aPrime, bPrime, cPrime)

    T = Or(And(st0, st1_prime),
           And(st1, st0_prime), And(st1, st2_prime), And(st1, st4_prime),
           And(st2, st3_prime),
           And(st3, st4_prime),
           And(st5, st4_prime), And(st5, st7_prime),
           And(st6, st7_prime))

    return T


def getInit():
    # Defining variables
    a = Bool("a{0}".format(0))
    b = Bool("b{0}".format(0))
    c = Bool("c{0}".format(0))

    init = And(Not(a), Not(b), Not(c))
    return init