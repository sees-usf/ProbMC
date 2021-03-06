from z3 import *

def GetProperty():
    dice_value = Int("dice_value")
    next_s = Int("next_s")

    property = (And(next_s==7, dice_value==1))  # As of right now, we have to negate the property ourselves

    property_probability = 1
   
    return (property, property_probability)