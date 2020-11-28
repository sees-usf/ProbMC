""" Synch Example Module - Module to hold information regarding the synch example model.
    Contains information on the following:
    (1) Transition Relations
    (2) Property
    (3) Initial State
"""

from z3 import *

def GetStep(step):
    """ Transition Relations """
    probability = Real('p{0}'.format(step+1))
    current_r1 = Bool("r1.{0}".format(step))
    next_r1 = Bool("r1.{0}".format(step+1))
    current_r2 = Bool("r2.{0}".format(step))
    next_r2 = Bool("r2.{0}".format(step+1))
    x = Bool("x.{0}".format(step+1))

    # (current state <-> next state)
    # x xor r1 <-> (next state of..) r1  
    # (!x && r2) || (r1 && x) <-> (next state of...) r2

    ranges = And(0 <= probability, probability <= 1)
    choice1 = And(Implies(Xor(x, current_r1), next_r1), Implies(next_r1, Xor(x, current_r1)), probability==0.5)
    choice2 = And(Implies(Or(And(Not(x), current_r2), And(current_r1, x)), next_r2), Implies(next_r2, Or(And(Not(x), current_r2), And(current_r1, x))), probability==0.5) # (!x && r2) || (r1 && x) <-> r2

    step = And(ranges, choice1, choice2) # AND()ing choices together => Synchronous

    return step

def GetProperty(step):
    """ Property """
    next_r1 = Bool("r1.{0}".format(step+1))
    next_r2 = Bool("r2.{0}".format(step+1))
    x = Bool("x.{0}".format(step+1))
    current_r1 = Bool("r1.{0}".format(step))
    current_r2 = Bool("r2.{0}".format(step))

    property = And(current_r1==True, current_r2==False, x==True, next_r1==False, next_r2==True)  # As of right now, we have to negate the property ourselves
    # Never happens: And(next_r1==True, next_r2==True)
    # And(next_r1==False, next_r2==False) can happen, but only during initialization.
    # And(current_r1==True, current_r2==False, x==True, next_r1==False, next_r2==True) Can't happen at initialization.

    return property

def GetInitialStates():
    """ Initial States """
    current_r1 = Bool("r1.0")
    current_r2= Bool("r2.0")

    initial_states = And(current_r1==False, current_r2==False)

    return initial_states