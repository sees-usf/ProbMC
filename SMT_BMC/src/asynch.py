"""
B1, b2: boolean
x: integer[1..2]

Initial: pc1=n1, pc2=n2, b1=b2=false, x either 1 or 2

# Only execute 1 transition

tr1 ... add additional constraints (Ex. pc2 should be the same in current and next state (pc2 = pc2'))
    (b2 = b2'... etc.)
tr2 .. etc., then OR them together

Pc1 = n1 -> b1:=true; x:=2; pc1 := w1
Pc1 = w1 && (x=1 || !b2) -> pc1 := c1
pc1 = c1 -> b1 := false; pc1 := n1 
Pc2 = n2 -> b2:=true; x:=1; pc2 := w2
Pc2 = w2 && (x=2 || !b1) -> pc2 := c2
Pc2 = c2 -> b2 := false; pc2 := n2

Property: p1=c1 && pc2 = c2 (this is not supposed to happen)
"""
#####################################################################
""" Asynch Module - Module to hold information regarding the asynchronous model.
    Contains information on the following:
    (1) Transition Relations
    (2) Property
    (3) Initial State
"""

from z3 import *

def GetStep(step):
    """ Transition Relations """
    current_b1 = Bool("b1.{0}".format(step))
    current_b2 = Bool("b2.{0}".format(step))
    current_c1 = Bool("c1.{0}".format(step))
    current_c2 = Bool("c2.{0}".format(step))
    current_pc1 = Bool("pc1.{0}".format(step))
    current_pc2 = Bool("pc2.{0}".format(step))
    current_n1 = Bool("n1.{0}".format(step))
    current_n2 = Bool("n2.{0}".format(step))
    current_w1 = Bool("w1.{0}".format(step))
    current_w2 = Bool("w2.{0}".format(step))
    current_x = Int("x{0}".format(step))

    next_b1 = Bool("b1.{0}".format(step+1))
    next_b2 = Bool("b2.{0}".format(step+1))
    next_c1 = Bool("c1.{0}".format(step+1))
    next_c2 = Bool("c2.{0}".format(step+1))
    next_pc1 = Bool("pc1.{0}".format(step+1))
    next_pc2 = Bool("pc2.{0}".format(step+1))
    next_n1 = Bool("n1.{0}".format(step+1))
    next_n2 = Bool("n2.{0}".format(step+1))
    next_w1 = Bool("w1.{0}".format(step+1))
    next_w2 = Bool("w2.{0}".format(step+1))
    next_x = Int("x{0}".format(step+1))

    probability = Real('p{0}'.format(step+1))

    ranges = And(And(1 <= current_x, current_x <= 2), And(1 <= next_x, next_x <= 2), And(0 <= probability, probability <= 1))
    choice1 = And(Implies(current_pc1==current_n1, And(next_b1==True, next_x==2, next_pc1==current_w1)), And(next_b2==current_b2, next_pc2==current_pc2, next_n1==current_n1, next_n2==current_n2, current_w1==next_w1, current_w2==next_w2, current_c1==next_c1, current_c2==next_c2, probability==0.5))
    choice2 = And(Implies(current_pc1==And(current_w1, Or(current_x==1, Not(current_b2))), next_pc1==current_c1), And(next_b1==current_b1, next_x==current_x, next_b2==current_b2, next_pc2==current_pc2, next_n1==current_n1, next_n2==current_n2, current_w1==next_w1, current_w2==next_w2, current_c1==next_c1, current_c2==next_c2, probability==0.5))
    choice3 = And(Implies(current_pc1==current_c1, And(next_b1==False, next_pc1==current_n1)), And(next_x==current_x, next_b2==current_b2, next_pc2==current_pc2, next_n1==current_n1, next_n2==current_n2, current_w1==next_w1, current_w2==next_w2, current_c1==next_c1, current_c2==next_c2, probability==0.5))
    choice4 = And(Implies(current_pc2==current_n2, And(next_b2==True, next_x==1, next_pc2==current_w2)), And(next_b1==current_b1, next_pc1==current_pc1, next_n1==current_n1, next_n2==current_n2, current_w1==next_w1, current_w2==next_w2, current_c1==next_c1, current_c2==next_c2, probability==0.5))
    choice5 = And(Implies(current_pc2==And(current_w2, Or(current_x==2, Not(current_b1))), next_pc2==current_c2), And(next_b1==current_b1, next_x==current_x, next_b2==current_b2, next_pc1==current_pc1, next_n1==current_n1, next_n2==current_n2, current_w1==next_w1, current_w2==next_w2, current_c1==next_c1, current_c2==next_c2, probability==0.5))
    choice6 = And(Implies(current_pc2==current_c2, And(next_b2==False, next_pc2==current_n2)), And(next_b1==current_b1, next_x==current_x, next_pc1==current_pc1, next_n1==current_n1, next_n2==current_n2, current_w1==next_w1, current_w2==next_w2, current_c1==next_c1, current_c2==next_c2, probability==0.5))

    # ranges = And(And(1 <= current_x, current_x <= 2), And(1 <= next_x, next_x <= 2), And(0 <= probability, probability <= 1))
    # choice1 = Implies(current_pc1==current_n1, And(next_b1==True, next_x==2, next_pc1==current_w1))
    # choice2 = Implies(current_pc1==And(current_w1, Or(current_x==1, Not(current_b2))), next_pc1==current_c1)
    # choice3 = Implies(current_pc1==current_c1, And(next_b1==False, next_pc1==current_n1))
    # choice4 = Implies(current_pc2==current_n2, And(next_b2==True, next_x==1, next_pc2==current_w2))
    # choice5 = Implies(current_pc2==And(current_w2, Or(current_x==2, Not(current_b1))), next_pc2==current_c2)
    # choice6 = Implies(current_pc2==current_c2, And(next_b2==False, next_pc2==current_n2))

    step = And(ranges, Or(choice1, choice2, choice3, choice4, choice5, choice6))

    return step

def GetProperty(path_length):
    """ Property """
    next_pc1 = Bool("pc1.{0}".format(path_length))
    next_pc2 = Bool("pc2.{0}".format(path_length))
    next_c1 = Bool("c1.{0}".format(path_length))
    next_c2 = Bool("c2.{0}".format(path_length))

    property = And(next_pc1==next_c1, next_pc2==next_c2) # Shouldn't happen

    return property

def GetInitialStates():
    """ Initial States """
    current_b1 = Bool("b1.0")
    current_b2 = Bool("b2.0")
    current_pc1 = Bool("pc1.0")
    current_pc2 = Bool("pc2.0")
    current_n1 = Bool("n1.0")
    current_n2 = Bool("n2.0")

    initial_states = And(current_pc1==current_n1, current_pc2==current_n2, current_b1==False, current_b2==False)

    return initial_states