""" Example Model
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

def GetStep(step):
    """ Transition Relations """
    current_R1 = Int('R1.{0}'.format(step-1))
    current_R2 = Int('R2.{0}'.format(step-1))
    R1_nxt = Int("R1.{0}".format(step))
    R2_nxt = Int("R2.{0}".format(step))
    s2 = Int('s2.{0}'.format(step-1))
    s2_nxt = Int("s2.{0}".format(step))
    trp = Real('p.{0}'.format(step))

    ranges = And(And(0 <= current_R1, current_R1 <= 1), And(0 <= current_R2, current_R2 <= 1), And(0 <= trp, trp <= 1))
    transition_1 = And((trp * (1 + 0.025 * ToReal(s2)) == 1), s2_nxt==(s2 + 1), R1_nxt == 1, R2_nxt == 0)
    transition_2 = And(trp==((0.025 * ToReal(s2)) / (1 + 0.025 * ToReal(s2))), s2_nxt==(s2 - 1), R1_nxt == 0, R2_nxt == 1)
	
    step = And(ranges, Or(transition_1, transition_2))
	
    return step

def GetProperty(step):
    """ Property """
    s2_nxt = Int('s2.{0}'.format(step))

    property = (s2_nxt == 65)

    return property

def GetInitialStates():
    """ Initial States """
    current_R1 = Int("R1.{0}".format(0))
    current_R2 = Int("R2.{0}".format(0))
    trp = Real('p.{0}'.format(0))
    s2 = Int('s2.{0}'.format(0))

    initial_states = And(s2 == 40,current_R1 == 0, current_R2 == 0, trp == 1)

    return initial_states