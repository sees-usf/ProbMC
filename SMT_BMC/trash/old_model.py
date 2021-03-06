from z3 import *
#Rename this to die.py

def GetStep():
    current_s = Int("current_s")
    probability = Real("probability")
    dice_value = Int("dice_value")
    next_s = Int("next_s")

    ranges = And(And(0 <= current_s, current_s <= 7), And(0 <= next_s, next_s <= 7), And(0 <= dice_value, dice_value <= 6), And(0 <= probability, probability <= 1))
    choice1 = And(current_s==0, (Or(And(probability==0.5, dice_value==0, next_s==1), And(probability==0.5, dice_value==0, next_s==2))))
    choice2 = And(current_s==1, (Or(And(probability==0.5, dice_value==0, next_s==3), And(probability==0.5, dice_value==0, next_s==4))))
    choice3 = And(current_s==2, (Or(And(probability==0.5, dice_value==0, next_s==5), And(probability==0.5, dice_value==0, next_s==6))))
    choice4 = And(current_s==3, (Or(And(probability==0.5, dice_value==0, next_s==1), And(probability==0.5, dice_value==1, next_s==7))))
    choice5 = And(current_s==4, (Or(And(probability==0.5, dice_value==2, next_s==7), And(probability==0.5, dice_value==3, next_s==7))))
    choice6 = And(current_s==5, (Or(And(probability==0.5, dice_value==4, next_s==7), And(probability==0.5, dice_value==5, next_s==7))))
    choice7 = And(current_s==6, (Or(And(probability==0.5, next_s==2), And(probability==0.5, dice_value==6, next_s==7))))
    choice8 = And(current_s==7, next_s==7)

    step = And(ranges, Or(choice1, choice2, choice3, choice4, choice5, choice6, choice7, choice8))
   
    return step