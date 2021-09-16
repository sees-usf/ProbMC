""" Die Module - Module to hold information regarding the die model.
    Contains information on the following:
    (1) Transition Relations
    (2) Property
    (3) Initial State

    This model emulates a 6-sided die through coin tosses (50% probability per state transition).
    SMT_BMC/references has a .jpg image of the model for better visualization.

    See this link for more information:
        https://www.prismmodelchecker.org/tutorial/die.php
"""

from z3 import *

# State variable strings.
s = 's.{0}'
dicevalue = 'dv.{0}'

# Transition probability strings.
trprb = 'p.{0}'

# List of variable and probability strings.
probabilties = []
state_variable_list = []

def GetStep(step):
    """ Transition Relations """
    current_state = Int(s.format(step - 1))
    probability = Real(trprb.format(step))
    dice_value = Int(dicevalue.format(step))
    next_state = Int(s.format(step))

    ranges = And(And(0 <= current_state, current_state <= 7), And(0 <= next_state, next_state <= 7), And(0 <= dice_value, dice_value <= 6), And(0 <= probability, probability <= 1))
    choice1 = And(current_state==0, (Or(And(probability==0.5, dice_value==0, next_state==1), And(probability==0.5, dice_value==0, next_state==2))))
    choice2 = And(current_state==1, (Or(And(probability==0.5, dice_value==0, next_state==3), And(probability==0.5, dice_value==0, next_state==4))))
    choice3 = And(current_state==2, (Or(And(probability==0.5, dice_value==0, next_state==5), And(probability==0.5, dice_value==0, next_state==6))))
    choice4 = And(current_state==3, (Or(And(probability==0.5, dice_value==0, next_state==1), And(probability==0.5, dice_value==1, next_state==7))))
    choice5 = And(current_state==4, (Or(And(probability==0.5, dice_value==2, next_state==7), And(probability==0.5, dice_value==3, next_state==7))))
    choice6 = And(current_state==5, (Or(And(probability==0.5, dice_value==4, next_state==7), And(probability==0.5, dice_value==5, next_state==7))))
    choice7 = And(current_state==6, (Or(And(probability==0.5, dice_value==0, next_state==2), And(probability==0.5, dice_value==6, next_state==7))))
    choice8 = And(current_state==7, next_state==7, dice_value==0)

    step = And(ranges, Or(choice1, choice2, choice3, choice4, choice5, choice6, choice7, choice8)) # OR()ing choices together = Asynchrounous

    probabilties.append(probability) # Keeping track of previous states (necessary).
    state_variable_list.append(next_state)
    state_variable_list.append(dice_value)

    return step

def GetProperty(step):
    """ Property """
    dice_value = Int(dicevalue.format(step))
    next_state = Int(s.format(step))

    property = (And(next_state==7, dice_value==1))  # As of right now, we have to negate the property ourselves

    return property

def GetInitialStates():
    """ Initial States """
    dice_value = Int(dicevalue.format(0))
    current_state = Int(s.format(0))

    initial_states = And(current_state==0, dice_value==0)
    
    state_variable_list.append(current_state) # Keeping track of previous states (necessary).
    state_variable_list.append(dice_value)

    return initial_states
    
def GetStateVaribleStrings(): # Return all the states not including probabilties.
    return state_variable_list

def GetTransitionProbStrings(): # Return all the states including probabilties
    return probabilties