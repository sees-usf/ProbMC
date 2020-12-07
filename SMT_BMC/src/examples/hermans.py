""" Hermans Module - Module to hold information regarding the hermans model.
    Contains information on the following:
    (1) Transition Relations
    (2) Property
    (3) Initial State

    This model represents Herman's self-stabilization algorithm. In this example, 3 processes, x1,
    x2, and x3, are represented in a ring formation. If the processes' value does not matches that 
    of its neighbor, the algorithm will force the neighbors to match. If the neighbors do match,
    then the algorithm will let the process choose to be any boolean value.

    See this link for more information:
        https://github.com/prismmodelchecker/prism-benchmarks/blob/master/models/dtmcs/herman/herman3.pm
"""

from z3 import *

def GetStep(step):
    """ Transition Relations """
    current_x1 = Bool("x1.{0}".format(step))
    next_x1 = Bool("x1.{0}".format(step+1))
    current_x2 = Bool("x2.{0}".format(step))
    next_x2 = Bool("x2.{0}".format(step+1))
    current_x3 = Bool("x3.{0}".format(step))
    next_x3 = Bool("x3.{0}".format(step+1))
    probability = Real("p{0}".format(step+1))

    ranges = And(0 <= probability, probability <= 1)
    choice1 = And(current_x1==current_x3, (Or(And(probability==0.5, next_x1==False), And(probability==0.5, next_x1==True))))
    choice2 = And(current_x1!=current_x3, next_x1==current_x3, probability==0.5)
    choice3 = And(current_x2==current_x1, (Or(And(probability==0.5, next_x2==False), And(probability==0.5, next_x2==True))))
    choice4 = And(current_x2!=current_x1, next_x2==current_x1, probability==0.5)
    choice5 = And(current_x3==current_x2, (Or(And(probability==0.5, next_x3==False), And(probability==0.5, next_x3==True))))
    choice6 = And(current_x3!=current_x2, next_x3==current_x2, probability==0.5)

    step = And(ranges, Or(choice1, choice2), Or(choice3, choice4), Or(choice5, choice6))

    return step

def GetProperty(step):
    """ Property """
    current_x1 = Bool("x1.{0}".format(step))
    next_x1 = Bool("x1.{0}".format(step+1))
    current_x2 = Bool("x2.{0}".format(step))
    next_x2 = Bool("x2.{0}".format(step+1))
    current_x3 = Bool("x3.{0}".format(step))
    next_x3 = Bool("x3.{0}".format(step+1))

    property = (And(next_x1==False, next_x2==True, next_x3==False))  # As of right now, we have to negate the property ourselves
    # Note: Probability returned 1st on step 2 given property next_x1==True, next_x2==False, and next_x3==True and init of (1 1 0)
    #       Probability returned 1st on step 3 given property next_x1==False, next_x2==True, and next_x3==False and init of (1 1 0)

    return property

def GetInitialStates():
    """ Initial States """
    current_x1 = Bool("x1.0")
    next_x1 = Bool("x1.1")
    current_x2 = Bool("x2.0")
    next_x2 = Bool("x2.1")
    current_x3 = Bool("x3.0")
    next_x3 = Bool("x3.1")

    # initial_states = And(current_x1==True, current_x2==True, current_x3==True) # This is what hermans should be initialized to
    # We don't use this for testing because if all the x's are True, the new x's can be any value rather than 1 strict value,
    # making testing difficult.
    initial_states = And(current_x1==True, current_x2==True, current_x3==False)
    # FIXME: Issue: initial_states = True returns total probability of 2.0 at k = 1

    return initial_states