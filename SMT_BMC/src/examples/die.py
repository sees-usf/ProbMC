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

"""
    Model for generating transitions represented by integers to model stocastic simulation.
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
transition_list = []

def GetStep(i, j):
    """ Transition Relations """
    current_state = Int(s.format(i))
    probability = Real(trprb.format(j))
    dice_value = Int(dicevalue.format(j))
    next_state = Int(s.format(j))
    trIndex = Int('trIndex.{0}'.format(j))

    ranges = And(And(0 <= current_state, current_state <= 7), And(0 <= next_state, next_state <= 7), And(0 <= dice_value, dice_value <= 6), And(0 <= probability, probability <= 1))
    
    choice1 = And(current_state==0, (Or(And(probability==0.5, dice_value==0, next_state==1, trIndex==0), And(probability==0.5, dice_value==0, next_state==2, trIndex==1))))
    choice2 = And(current_state==1, (Or(And(probability==0.5, dice_value==0, next_state==3, trIndex==2), And(probability==0.5, dice_value==0, next_state==4, trIndex==3))))
    choice3 = And(current_state==2, (Or(And(probability==0.5, dice_value==0, next_state==5, trIndex==4), And(probability==0.5, dice_value==0, next_state==6, trIndex==5))))
    choice4 = And(current_state==3, (Or(And(probability==0.5, dice_value==0, next_state==1, trIndex==6), And(probability==0.5, dice_value==1, next_state==7, trIndex==7))))
    choice5 = And(current_state==4, (Or(And(probability==0.5, dice_value==2, next_state==7, trIndex==8), And(probability==0.5, dice_value==3, next_state==7, trIndex==9))))
    choice6 = And(current_state==5, (Or(And(probability==0.5, dice_value==4, next_state==7, trIndex==10), And(probability==0.5, dice_value==5, next_state==7, trIndex==11))))
    choice7 = And(current_state==6, (Or(And(probability==0.5, dice_value==0, next_state==2, trIndex==12), And(probability==0.5, dice_value==6, next_state==7, trIndex==13))))
    #choice8 = (And(current_state==7, next_state==7, dice_value==0, trIndex==14))

    step = And(ranges, Or(choice1, choice2, choice3, choice4, choice5, choice6, choice7)) # OR()ing choices together = Asynchrounous
    
    probabilties.append(probability) # Keeping track of previous states (necessary).
    state_variable_list.append(next_state)
    state_variable_list.append(dice_value)
    transition_list.append(trIndex)

    return step
    
def GetTransition():
    return transition_list

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
    #temp.append(current_state)
    #temp.append(dice_value)
    #state_variable_list.append(temp)

    return initial_states
    
def GetStateVaribleStrings(): # Return all the states not including probabilties.
    return state_variable_list

def GetTransitionProbStrings(): # Return all the states including probabilties
    return probabilties