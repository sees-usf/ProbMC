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

# State variable strings.
x1 = "x1.{0}"
x2 = "x2.{0}"
x3 = "x3.{0}"

# Transition probability strings.
trprb = 'p.{0}'

# List of variable and probability strings.
probabilties = []
state_variable_list = []

def GetStep(step):
    """ Transition Relations """
    current_x1 = Bool(x1.format(step-1))
    next_x1 = Bool(x1.format(step))
    current_x2 = Bool(x2.format(step-1))
    next_x2 = Bool(x2.format(step))
    current_x3 = Bool(x3.format(step-1))
    next_x3 = Bool(x3.format(step))
    probability = Real(trprb.format(step))

    ranges = And(0 <= probability, probability <= 1)
    choice1 = And(current_x1==current_x3, (Or(And(probability==0.5, next_x1==False), And(probability==0.5, next_x1==True))))
    choice2 = And(current_x1!=current_x3, next_x1==current_x3, probability==0.5)
    choice3 = And(current_x2==current_x1, (Or(And(probability==0.5, next_x2==False), And(probability==0.5, next_x2==True))))
    choice4 = And(current_x2!=current_x1, next_x2==current_x1, probability==0.5)
    choice5 = And(current_x3==current_x2, (Or(And(probability==0.5, next_x3==False), And(probability==0.5, next_x3==True))))
    choice6 = And(current_x3!=current_x2, next_x3==current_x2, probability==0.5)

    step = And(ranges, Or(choice1, choice2), Or(choice3, choice4), Or(choice5, choice6))
    
    probabilties.append(probability) # Keeping track of previous states (necessary).
    state_variable_list.append(next_x1)
    state_variable_list.append(next_x2)
    state_variable_list.append(next_x3)

    return step

def GetProperty(step):
    """ Property """
    current_x1 = Bool(x1.format(step-1))
    next_x1 = Bool(x1.format(step))
    current_x2 = Bool(x2.format(step-1))
    next_x2 = Bool(x2.format(step))
    current_x3 = Bool(x3.format(step-1))
    next_x3 = Bool(x3.format(step))

    property = (And(next_x1==False, next_x2==True, next_x3==False))  # As of right now, we have to negate the property ourselves
    # Note: Probability returned 1st on step 2 given property next_x1==True, next_x2==False, and next_x3==True and init of (1 1 0)
    #       Probability returned 1st on step 3 given property next_x1==False, next_x2==True, and next_x3==False and init of (1 1 0)

    return property

def GetInitialStates():
    """ Initial States """
    current_x1 = Bool(x1.format(0))
    next_x1 = Bool(x1.format(0))
    current_x2 = Bool(x2.format(0))
    next_x2 = Bool(x2.format(0))
    current_x3 = Bool(x3.format(0))
    next_x3 = Bool(x3.format(0))

    # initial_states = And(current_x1==True, current_x2==True, current_x3==True) # This is what hermans should be initialized to
    # We don't use this for testing because if all the x's are True, the new x's can be any value rather than 1 strict value,
    # making testing difficult.
    initial_states = And(current_x1==True, current_x2==True, current_x3==False)
    # FIXME: Issue: initial_states = True returns total probability of 2.0 at k = 1
    
    state_variable_list.append(current_x1)
    state_variable_list.append(current_x2)
    state_variable_list.append(current_x3)

    return initial_states
    
def GetStateVaribleStrings(): # Return all the states not including probabilties.
    return state_variable_list

def GetTransitionProbStrings(): # Return all the states including probabilties
    return probabilties