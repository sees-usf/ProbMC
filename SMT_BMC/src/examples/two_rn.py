""" srn-2s ("two reaction network model")
    variables:
    s2, s2_nxt : int
 
    initial state: s2==40
 
    state transitions:
    transition 1: s2_nxt = s2+1
    transition 2: s2_nxt = s2-1
 
    Property:
            s2==65
"""

from z3 import *


def get_encoding(i):
    #nodes = []
    s2 = Int('s2.{0}'.format(i-1))
    s2_nxt = Int("s2.{0}".format(i))
    #nodes.append('s2.{0}'.format(i))
    #nodes.append('s2.{0}'.format(i))
    transition_1 = (s2_nxt==(s2 + 1))
    transition_2 = (s2_nxt==(s2 - 1))
    encoding = Or(transition_1, transition_2)
    return encoding
    #return [encoding, nodes]

def get_property(i):
    
    s2 = Int('s2.{0}'.format(i))
    property = (s2 == 41)
    return property

def get_initial_state():
    
    s2 = Int('s2.{0}'.format(0))
    #node = Node('s2.{0}'.format(i))
    state = (s2 == 40)
    return state
    #return [state, node]
    
