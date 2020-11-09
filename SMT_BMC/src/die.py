""" Die Module - Module to hold information regarding the die model.
    Contains information on the following:
    (1) Transition Relations
    (2) Property
    (3) Initial State
"""

from z3 import *

def GetStep(step):
    """ Transition Relations """
    current_state = Int('s{0}'.format(step))
    probability = Real('p{0}'.format(step+1))
    dice_value = Int("dv{0}".format(step+1))
    next_state = Int("s{0}".format(step+1))

    ranges = And(And(0 <= current_state, current_state <= 7), And(0 <= next_state, next_state <= 7), And(0 <= dice_value, dice_value <= 6), And(0 <= probability, probability <= 1))
    choice1 = And(current_state==0, (Or(And(probability==0.5, dice_value==0, next_state==1), And(probability==0.5, dice_value==0, next_state==2))))
    choice2 = And(current_state==1, (Or(And(probability==0.5, dice_value==0, next_state==3), And(probability==0.5, dice_value==0, next_state==4))))
    choice3 = And(current_state==2, (Or(And(probability==0.5, dice_value==0, next_state==5), And(probability==0.5, dice_value==0, next_state==6))))
    choice4 = And(current_state==3, (Or(And(probability==0.5, dice_value==0, next_state==1), And(probability==0.5, dice_value==1, next_state==7))))
    choice5 = And(current_state==4, (Or(And(probability==0.5, dice_value==2, next_state==7), And(probability==0.5, dice_value==3, next_state==7))))
    choice6 = And(current_state==5, (Or(And(probability==0.5, dice_value==4, next_state==7), And(probability==0.5, dice_value==5, next_state==7))))
    choice7 = And(current_state==6, (Or(And(probability==0.5, next_state==2), And(probability==0.5, dice_value==6, next_state==7))))
    choice8 = And(current_state==7, next_state==7, dice_value==0)

    step = And(ranges, Or(choice1, choice2, choice3, choice4, choice5, choice6, choice7, choice8)) # OR()ing choices together = Asynchrounous

    return step

def GetProperty(path_length):
    """ Property """
    dice_value = Int("dv{0}".format(path_length))
    next_state = Int("s{0}".format(path_length))

    property = (And(next_state==7, dice_value==1))  # As of right now, we have to negate the property ourselves

    return property

def GetInitialStates():
    """ Initial States """
    dice_value = Int("dv1")
    current_state = Int("s0")

    initial_states = And(current_state==0, dice_value==0)

    return initial_states