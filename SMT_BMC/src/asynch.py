"""
B1, b2: boolean
x: integer[1..2]

Initial: pc1=n1, pc2=n2, b1=b2=false, x either 1 or 2

# Only execute 1 transition

tr1 ... add additional constraints (Ex. pc2 should be the same in current and next state (pc2 = pc2'))
    (b2 = b2'... etc.)
tr2 .. etc., then OR them together

Pc1 = n1 -> b1:=true; x:=2; pc1 := w1
Pc1 = w1 && (x=1 || !b2) -> pc1 := c1 // I switched this b2 with b1 in the transition relations
pc1 = c1 -> b1 := false; pc1 := n1 
Pc2 = n2 -> b2:=true; x:=1; pc2 := w2
Pc2 = w2 && (x=2 || !b1) -> pc2 := c2 // I switched this b1 with b2 in the transition relations
Pc2 = c2 -> b2 := false; pc2 := n2

b1, b2 = Boolean -> Change
x = Int
pc1, pc2 = Int() -> Change
w = 1
c = 2
n = 0

Property: p1=c1 && pc2 = c2 (this is not supposed to happen)

Running through a path...
    # 1st... Hits choice 1... Now... pc1.1=w, x.1=2, b1.1=True
    # 2nd... Hits choice 2... x.1!=1 and !b1 are both False, making the OR statement false, causing the transition to fail.
    #        Hits choice 4... Now... pc2.2=w, x.2=1, b2.2=True
    # 3rd... Hits choice 2... x.2==1, making the OR statement true... Now... pc1.3=c, b1.3=True, x.3=1
    # 4th... Hits choice 5... 

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
    current_pc1 = Int("pc1.{0}".format(step))
    current_pc2 = Int("pc2.{0}".format(step))
    current_x = Int("x.{0}".format(step))
    next_b1 = Bool("b1.{0}".format(step+1))
    next_b2 = Bool("b2.{0}".format(step+1))
    next_pc1 = Int("pc1.{0}".format(step+1))
    next_pc2 = Int("pc2.{0}".format(step+1))
    next_x = Int("x.{0}".format(step+1))
    n = Int("n")
    w = Int("w")
    c = Int("c")
    probability = Real('p{0}'.format(step+1))

    ranges = And(And(1 <= current_x, current_x <= 2), And(1 <= next_x, next_x <= 2), And(0 <= probability, probability <= 1), n==0, w==1, c==2)
    keep_1 = And(current_pc1==next_pc1, current_b1==next_b1)
    keep_2 = And(current_pc2==next_pc2, current_b2==next_b2)
    choice1 = And(current_pc1==n, next_b1==True, next_x==2, next_pc1==w, probability==0.5)
    choice2 = And(current_pc1==w, Or(current_x==1, Not(current_b1)), next_pc1==c, probability==0.5, next_x==current_x)
    choice3 = And(current_pc1==c, next_b1==False, next_pc1==n, probability==0.5, next_x==current_x)
    choice4 = And(current_pc2==n, next_b2==True, next_x==1, next_pc2==w, probability==0.5)
    choice5 = And(current_pc2==w, Or(current_x==2, Not(current_b2)), next_pc2==c, probability==0.5, next_x==current_x)
    choice6 = And(current_pc2==c, next_b2==False, next_pc2==n, probability==0.5, next_x==current_x)

    step = And(ranges, Or(And(Or(choice1, choice2, choice3), keep_2), And(Or(choice4, choice5, choice6), keep_1)))

    return step

def GetProperty(path_length):
    """ Property """
    next_pc1 = Int("pc1.{0}".format(path_length))
    next_pc2 = Int("pc2.{0}".format(path_length))
    c = Int("c")

    property = And(next_pc1==c, next_pc2==c) # Shouldn't happen
    # property = And(next_pc1==c, next_pc2==1) # Should happen on the third step

    return property

def GetInitialStates():
    """ Initial States """
    current_b1 = Bool("b1.0")
    current_b2 = Bool("b2.0")
    current_pc1 = Int("pc1.0")
    current_pc2 = Int("pc2.0")
    n = Int("n")

    initial_states = And(current_pc1==n, current_pc2==n, current_b1==False, current_b2==False)

    return initial_states