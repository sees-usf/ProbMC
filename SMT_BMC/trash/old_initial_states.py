from z3 import *

def GetInitialStates():
    dice_value = Int("dice_value")
    current_s = Int("current_s")

    initial_states = And(current_s==0, dice_value==0)
   
    return initial_states