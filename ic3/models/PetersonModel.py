from z3 import *


def getProperty(k):
    # Defining variables
    x = Int("x{0}".format(k))
    st1 = Int("st1{0}".format(k))
    st2 = Int("st2{0}".format(k))
    b1 = Bool("b1{0}".format(k))
    b2 = Bool("b2{0}".format(k))
    P = Not(And(st1 == 3, st2 == 3))
    return P


def getLiterals():
    return ["x", "st1", "st2", "b1", "b2"]


def getTransition(k):
    # Defining variables
    x = Int("x{0}".format(k))
    b1 = Bool("b1{0}".format(k))
    b2 = Bool("b2{0}".format(k))
    st1 = Int("st1{0}".format(k))
    st2 = Int("st2{0}".format(k))

    xPrime = Int("x{0}".format(k + 1))
    b1Prime = Bool("b1{0}".format(k + 1))
    b2Prime = Bool("b2{0}".format(k + 1))
    st1_next = Int("st1{0}".format(k))
    st2_next = Int("st2{0}".format(k))

    # Defining states - original
    # Defining states - prime

    T1 = And(Implies(st1 == 0, st1_next == 1),
             Implies(st1 == 1, And(b1 == True, x == 2, st1_next == 2)),
             Implies(And(st1 == 2, Or(x == 1, b2 == False)), st1_next == 3),
             Implies(And(st1 == 2, Not(Or(x == 1, b2 == False))), st1_next == 2),
             Implies(st1 == 3, And(b1 == False, st1_next == 0))
             )

    T2 = And(Implies(st2 == 0, st2_next == 1),
             Implies(st2 == 1, And(b2 == True, x == 2, st2_next == 2)),
             Implies(And(st2 == 2, Or(x == 1, b1 == False)), st2_next == 3),
             Implies(And(st2 == 2, Not(Or(x == 1, b1 == False))), st2_next == 2),
             Implies(st2 == 3, And(b2 == False, st2_next == 0))
             )

    T = Or(And(T1, st2 == st2_next),
           And(T2, st1 == st1_next))

    return T


def getInit():
    # Defining variables
    st1 = Int("st1{0}".format(0))
    st2 = Int("st2{0}".format(0))
    b1 = Bool("b1{0}".format(0))
    b2 = Bool("b2{0}".format(0))

    init = And(st1 == 1, b1 == False, st2 == 0, b2 == False)
    return init
