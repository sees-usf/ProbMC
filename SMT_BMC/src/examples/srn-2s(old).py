""" srn-2s ("syntheic biology model")
    variables:
    R1, R2 : int
    s2, s2_nxt : int
    trp : real
 
    initial state: s2==40 && R1== 0 && R2==0
 
    state transitions:
    transition 1: s2_nxt = s2+1, trp = 1/(1 + 0.025*s2), R1 = 1, R2 = 0
    transition 2: s2_nxt = s2-1, trp = 0.025*s2/(1 + 0.025*s2), R1= 0, R2 = 1
 
    Property:
            s2==65
"""

from z3 import *

# State variable strings.
s2 = 's2.{0}'

# Transition probability strings.
trprb = 'p.{0}'

# List of variable and probability strings.
probabilties = []
state_variable_list = []

def GetStep(step):
    """ Transition Relations """
    s2 = Int('s2.{0}'.format(step-1))
    s2_nxt = Int("s2.{0}".format(step))
    trp = Real('p.{0}'.format(step))

    transition_1 = And((trp * (1 + 0.025 * ToReal(s2)) == 1), s2_nxt==(s2 + 1))
    transition_2 = And(trp==((0.025 * ToReal(s2)) / (1 + 0.025 * ToReal(s2))), s2_nxt==(s2 - 1))
	
    step = Or(transition_1, transition_2)
    
    probabilties.append(trp)
    state_variable_list.append(s2_nxt)
	
    return step

def GetProperty(step):
    """ Property """
    s2_nxt = Int('s2.{0}'.format(step))

    property = (s2_nxt == 65)

    return property

def GetInitialStates():
    """ Initial States """
    trp = Real('p.{0}'.format(0))
    s2 = Int('s2.{0}'.format(0))

    initial_states = And(s2 == 40, trp == 1)
    
    state_variable_list.append(s2)

    return initial_states
    
def GetStateVaribleStrings(): # Return all the states not including probabilties.
    return state_variable_list

def GetTransitionProbStrings(): # Return all the states including probabilties
    return probabilties