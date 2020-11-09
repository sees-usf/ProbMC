""" Hermans Module - Module to hold information regarding the hermans model.
    Contains information on the following:
    (1) Transition Relations
    (2) Property
    (3) Initial State
"""

from z3 import *

def GetStep(step):
    """ Transition Relations """
    current_x1 = Int("current_x1")
    next_x1 = Int("next_x1")
    current_x2 = Int("current_x2")
    next_x2 = Int("next_x2")
    current_x3 = Int("current_x3")
    next_x3 = Int("next_x3")
    probability = Real("probability")

    ranges = And(And(0 <= current_x1, current_x1 <= 1), And(0 <= next_x1, next_x1 <= 1), And(0 <= current_x2, current_x2 <= 1), And(0 <= next_x2, next_x2 <= 1), And(0 <= current_x3, current_x3 <= 1), And(0 <= next_x3, next_x3 <= 1), And(0 <= probability, probability <= 1))
    choice1 = And(current_x1==current_x3, (Or(And(probability==0.5, next_x1==0), And(probability==0.5, next_x1==1))))
    choice2 = And(current_x1!=current_x3, next_x1==current_x3)
    choice3 = And(current_x2==current_x1, (Or(And(probability==0.5, next_x2==0), And(probability==0.5, next_x2==1))))
    choice4 = And(current_x2!=current_x1, next_x2==current_x1)
    choice5 = And(current_x3==current_x2, (Or(And(probability==0.5, next_x3==0), And(probability==0.5, next_x3==1))))
    choice6 = And(current_x3!=current_x2, next_x3==current_x2)

    step = And(ranges, Or(choice1, choice2, choice3, choice4, choice5, choice6))

    return step

def GetProperty(path_length):
    """ Property """
    current_x1 = Int("current_x1")
    next_x1 = Int("next_x1")
    current_x2 = Int("current_x2")
    next_x2 = Int("next_x2")
    current_x3 = Int("current_x3")
    next_x3 = Int("next_x3")

    property = (And(next_x1==1, next_x2==0, next_x3==1))  # As of right now, we have to negate the property ourselves
    # Note: next_x1==True, next_x2==False, and next_x3==True on step 4

    property_probability = 1

    return property

def GetInitialStates():
    """ Initial States """
    current_x1 = Int("current_x1")
    next_x1 = Int("next_x1")
    current_x2 = Int("current_x2")
    next_x2 = Int("next_x2")
    current_x3 = Int("current_x3")
    next_x3 = Int("next_x3")

    initial_states = And(current_x1==1, current_x2==1, current_x3==1)

    return initial_states