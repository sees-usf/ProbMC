"""
dtmc

module die

        // local state
        s : [0..7] init 0;  //0<= s <= 7  -- r0(s) constraitn defining domain of variables, each variable should have its own constraints.
        // value of the die
        d : [0..6] init 0;  //0 <= d <= 6 --r1(d)  see r0
        
        //init stat constraint: s==0 && d==0 -- c0(s, d)
        
        
        [] s=0 -> 0.5 : (s'=1) + 0.5 : (s'=2);  //s==0 && (p==0.5 && d==d' && s'==1 || p==0.5 && d==d' &&s'==2) -- c1(s, s', d, d')
        [] s=1 -> 0.5 : (s'=3) + 0.5 : (s'=4);  //s==1 && (p==0.5 && d==d' && s'==3 || p==0.5 && d==d' &&s'==4)
        [] s=2 -> 0.5 : (s'=5) + 0.5 : (s'=6);
        [] s=3 -> 0.5 : (s'=1) + 0.5 : (s'=7) & (d'=1);
        [] s=4 -> 0.5 : (s'=7) & (d'=2) + 0.5 : (s'=7) & (d'=3);
        [] s=5 -> 0.5 : (s'=7) & (d'=4) + 0.5 : (s'=7) & (d'=5);
        [] s=6 -> 0.5 : (s'=2) + 0.5 : (s'=7) & (d'=6);
        [] s=7 -> (s'=7);
        
        // constraint on transitions of this module
        r0(s0) && r0(s1) && ...
&&  r1(d0) && r1(d1) && ...
&&  c0(s0, d0) &&
        // constraints for transitions of each step, it becomes large if the length of execution paths are considered
        c1(s0, s1, d0, d1) && c2(s0, s1, d0, d1) && c3(s0, s1, d0, d1) ... && 
&&  c1(s1, s2) && c2(s1, s2) && c3(s1, s2) ...
    // constraint for the negation of the property for verification.
&& !(s0==7 && d0>=0.1 || s1==7 && d1>=0.1 || ...)
        
endmodule


// properties to verify
const int x;

// Is probability of throwing x > 0.1?
#####P>0.1 [ F s=7 & d=x ] ... x = 1
[F s=7 & d=1]

!(s0 d0) s0 = 7, d0 = 1
!(s1 d1)  s1 = 7, d1 = 1
Counter ... Neither of these states satisfy s=7 & d=1

CREATE A FUNCTION THAT TAKES 3 ARGUMENTS
k = length of path
p = property
and the transitions needed for the model I guess


// Probability of throwing 6?
P=? [ F s=7 & d=6 ]

// Probability of throwing x?
P=? [ F s=7 & d=x ]

// Expected number of coin flips to complete?
R=? [ F s=7 ]



rewards "coin_flips"
        [] s<7 : 1;
endrewards
"""
# from z3 import *

# p = Real('p')
# rp = And(0 <= p, p <= 1)  # Probability: 0 <= p <= 1
# s0 = Int('s0')
# d0 = Int('d0')
# c0 = And(s0==0, d0==0)  # Initial values: s=0, d=0
# rs0 = And(0 <= s0, s0 <= 7)  # Range of s0
# rd0 = And(0 <= d0, d0 <= 6)  # Range of d0
# s1 = Int('s1')
# d1 = Int('d1')
# rs1 = And(0 <= s1, s1 <= 7)  # Range of s1
# rd1 = And(0 <= d1, d1 <= 6)  # Range of d1
# s2 = Int('s2')
# d2 = Int('d2')
# rs2 = And(0 <= s2, s2 <= 7)  # Range of s2
# rd2 = And(0 <= d2, d2 <= 6)  # Range of d2
# s3 = Int('s3')
# d3 = Int('d3')
# rs3 = And(0 <= s3, s3 <= 7)  # Range of s3
# rd3 = And(0 <= d3, d3 <= 6)  # Range of d3
# s4 = Int('s4')
# d4 = Int('d4')
# rs4 = And(0 <= s4, s4 <= 7)  # Range of s4
# rd4 = And(0 <= d4, d4 <= 6)  # Range of d4
# s5 = Int('s5')
# d5 = Int('d5')
# rs5 = And(0 <= s5, s5 <= 7)  # Range of s5
# rd5 = And(0 <= d5, d5 <= 6)  # Range of d5
# s6 = Int('s6')
# d6 = Int('d6')
# rs6 = And(0 <= s6, s6 <= 7)  # Range of s6
# rd6 = And(0 <= d6, d6 <= 6)  # Range of s6
# s7 = Int('s7')
# rs7 = And(0 <= s7, s7 <= 7)  # Range of s7

# # Constraints for transitions of each step

# # First step
# c1_1 = And(s0==0, (Or(And(p==0.5, d0==d1, s1==1), And(p==0.5, d0==d1, s1==2))))
# c1_2 = And(s0==1, (Or(And(p==0.5, d0==d1, s1==3), And(p==0.5, d0==d1, s1==4))))
# c1_3 = And(s0==2, (Or(And(p==0.5, d0==d1, s1==5), And(p==0.5, d0==d1, s1==6))))
# c1_4 = And(s0==3, (Or(And(p==0.5, d0==d1, s1==1), And(p==0.5, d1==1, s1==7))))
# c1_5 = And(s0==4, (Or(And(p==0.5, d1==2, s1==7), And(p==0.5, d1==3, s1==7))))
# c1_6 = And(s0==5, (Or(And(p==0.5, d1==4, s1==7), And(p==0.5, d1==5, s1==7))))
# c1_7 = And(s0==6, (Or(And(p==0.5, d0==d1, s1==2), And(p==0.5, d1==6, s1==7))))
# c1_8 = And(s0==7, s1==7)

# solver1 = Solver()

# # Negation of the property for verification
# # verify = Not(Or(And(s0 == 7, d0 >= 0.1), And(s1 == 2, d1 >= 0.1)))
# # (s0==7 && d0 >= 0.1) || (s1==2 && d1 >= 0.1)
# verify = (s1==1)

# transition_constraints = And(rp, c0, p==0.5)
# solver1.add(transition_constraints)
# # print(solver.check())
# # print(solver.model())
# solver1.add(transition_constraints)
# transitions = Or(c1_1, c1_2, c1_3, c1_4, c1_5, c1_6, c1_7, c1_8)
# solver1.add(transitions)
# solver1.add(verify)
# print(solver1.check())
# print(solver1.model())

# model1 = solver1.model()

# for d in model1.decls():
#         if d.name() == "s1":
#                 current_state=model1[d]

# # Second step
# c2_1 = And(s1==0, (Or(And(p==0.5, d0==d1, s2==1), And(p==0.5, d0==d1, s2==2))))
# c2_2 = And(s1==1, (Or(And(p==0.5, d0==d1, s2==3), And(p==0.5, d0==d1, s2==4))))
# c2_3 = And(s1==2, (Or(And(p==0.5, d0==d1, s2==5), And(p==0.5, d0==d1, s2==6))))
# c2_4 = And(s1==3, (Or(And(p==0.5, d0==d1, s2==1), And(p==0.5, d1==1, s2==7))))
# c2_5 = And(s1==4, (Or(And(p==0.5, d1==2, s2==7), And(p==0.5, d1==3, s2==7))))
# c2_6 = And(s1==5, (Or(And(p==0.5, d1==4, s2==7), And(p==0.5, d1==5, s2==7))))
# c2_7 = And(s1==6, (Or(And(p==0.5, d0==d1, s2==2), And(p==0.5, d1==6, s2==7))))
# c2_8 = And(s1==7, s2==7)

# solver2 = Solver()
# transition_constraints = And(s1==current_state, p==0.5)
# verify = (s2==3)
# transitions = Or(c2_1, c2_2, c2_3, c2_4, c2_5, c2_6, c2_7, c2_8)
# solver2.add(transition_constraints)
# solver2.add(transitions)
# solver2.add(verify)
# print(solver2.check())
# print(solver2.model())

#-------------------------------------------------------------------------------------------------

from z3 import *
# class Choices:
#         current_s = Int("current_s")
#         probability = Real('probability')
#         dice_value = Int("dice_value")
#         next_s = Int("next_s")
#         testi = 0

#         def find_choices(self):
#                 choice1 = And(self.current_s==0, (Or(And(self.probability==0.5, self.dice_value==0, self.next_s==1), And(self.probability==0.5, self.dice_value==0, self.next_s==2))))
#                 choice2 = And(self.current_s==1, (Or(And(self.probability==0.5, self.dice_value==0, self.next_s==3), And(self.probability==0.5, self.dice_value==0, self.next_s==4))))
#                 choice3 = And(self.current_s==2, (Or(And(self.probability==0.5, self.dice_value==0, self.next_s==5), And(self.probability==0.5, self.dice_value==0, self.next_s==6))))
#                 choice4 = And(self.current_s==3, (Or(And(self.probability==0.5, self.dice_value==0, self.next_s==1), And(self.probability==0.5, self.dice_value==1, self.next_s==7))))
#                 choice5 = And(self.current_s==4, (Or(And(self.probability==0.5, self.dice_value==2, self.next_s==7), And(self.probability==0.5, self.dice_value==3, self.next_s==7))))
#                 choice6 = And(self.current_s==5, (Or(And(self.probability==0.5, self.dice_value==4, self.next_s==7), And(self.probability==0.5, self.dice_value==5, self.next_s==7))))
#                 choice7 = And(self.current_s==6, (Or(And(self.probability==0.5, self.next_s==2), And(self.probability==0.5, dice_value==6, next_s==7))))
#                 choice8 = And(self.current_s==7, self.next_s==7)

#                 choices = Or(choice1, choice2, choice3, choice4, choice5, choice6, choice7, choice8)
#                 return choices
        



"""

Path-2: Choices(s0, p1, d1, s1) AND
        Choices(s1, p2, d2, s2)

Choice.current_s = 0
Choice.probability_value = 0.5
Choice.dice_value = 0
Choice.next_s = 0
choice_list

for k:
        Choices.find_choices(cur_s, pv, dv, next_s) // sets values according to the choices stuff
        choice_list.add(Choices.choices)
        cur_s = next_s (points to)

total_choices
for item in choice_list:
        total_choices.add(item)

All the restraints will be in total_choices, And-ed together 

"""

def GetChoices(current_s, probability, dice_value, next_s):
        choice1 = And(current_s==0, (Or(And(probability==0.5, dice_value==0, next_s==1), And(probability==0.5, dice_value==0, next_s==2))))
        choice2 = And(current_s==1, (Or(And(probability==0.5, dice_value==0, next_s==3), And(probability==0.5, dice_value==0, next_s==4))))
        choice3 = And(current_s==2, (Or(And(probability==0.5, dice_value==0, next_s==5), And(probability==0.5, dice_value==0, next_s==6))))
        choice4 = And(current_s==3, (Or(And(probability==0.5, dice_value==0, next_s==1), And(probability==0.5, dice_value==1, next_s==7))))
        choice5 = And(current_s==4, (Or(And(probability==0.5, dice_value==2, next_s==7), And(probability==0.5, dice_value==3, next_s==7))))
        choice6 = And(current_s==5, (Or(And(probability==0.5, dice_value==4, next_s==7), And(probability==0.5, dice_value==5, next_s==7))))
        choice7 = And(current_s==6, (Or(And(probability==0.5, next_s==2), And(probability==0.5, dice_value==6, next_s==7))))
        choice8 = And(current_s==7, next_s==7)

        choices = Or(choice1, choice2, choice3, choice4, choice5, choice6, choice7, choice8)
        return choices

def GetProperties(current_s, dice_value):
        initial_state = And(current_s==0, dice_value==0)



def PathEncoding(path_length, model_to_check, initial_state, property):
        """
        Returns an encoding of ALL paths with length up to path_length
        This path, starting from initial state and ending at the negation of the property 
        """
        global current_s, next_s, probability, dice_value

        choices_list = []
        properties_list = []
        for k in range(path_length):
                choices_list.append(GetChoices(current_s, probability, dice_value, next_s))
                # properties_list.append()
                print(id(current_s))
                print(id(next_s))
                current_s = next_s # Need to have this pointing to next_s
                print(id(next_s))
        

current_s = Int("current_s")
probability = Real('probability')
dice_value = Int("dice_value")
next_s = Int("next_s")
print("Test:")
path_length = 5
model_to_check = 0
initial_state = And(current_s==0, dice_value==0)
property = And(next_s==7, dice_value==1)
PathEncoding(path_length, model_to_check, initial_state, property)

list1 = []
list1.append(Int("s0"))
list1.append(Int("s1"))
list2 = []
list2.append(Int("d1"))
list2.append(Int("d2"))
constraintsss = And(list1[1] == 0, list2[0] == 6)
print(constraintsss)

# print("Test 1: ")
# steps = 0
# property = And(next_s==7, dice_value==1)
# path_length = 10
# BMCencode(property, path_length, choices, 0)

# print("\n\nTest 2:")
# steps = 0
# property = And(next_s==7, dice_value==6)
# path_length = 10

# BMCencode(property, path_length, choices, 0)

# def BMC():

# def BMCencode(property, path_length, model_to_check, current_s_val):  # Note: Maybe make current_s_val a global variable instead of a parameter?
#         global steps, probability, current_s, next_s, dice_value

#         # Stop stepping through once given path length is reached.
#         if path_length == steps:
#                 return

#         solver = Solver()

#         # Ensures current state is set properly
#         transition_constraints = And(current_s==current_s_val, probability==0.5)

#         # Build Solver through constraints
#         solver.add(property)
#         solver.add(transition_constraints)
#         solver.add(choices)

#         # Must use this command before solver.model()
#         solver.check()

#         try:  # The property is satisfiable
#                 print(solver.model())

#                 # Potential: Stop running when an example is found that proves the property?

#                 # Get the value of the next state
#                 model = solver.model()
#                 for d in model.decls():
#                         if d.name() == "next_s":
#                                 next_s_val = model[d]

#                 if next_s_val == 7:  # 7 is the last state the model can enter.
#                         return
                
#                 steps += 1

#                 # Take another step through the model, using the next state as the new current state.
#                 test(property, path_length, choices, next_s_val)

#         except:  # The property is not satisfiable
#                 print("Step {}: Unsatisfactory".format(steps + 1))
#                 next_s_val = current_s_val + 1
#                 steps += 1
#                 test(property, path_length, choices, next_s_val)


# probability = Real('probability')
# current_s = Int("current_s")
# next_s = Int("next_s")
# dice_value = Int("dice_value")
# steps = 0

# choice1 = And(current_s==0, (Or(And(probability==0.5, dice_value==0, next_s==1), And(probability==0.5, dice_value==0, next_s==2))))
# choice2 = And(current_s==1, (Or(And(probability==0.5, dice_value==0, next_s==3), And(probability==0.5, dice_value==0, next_s==4))))
# choice3 = And(current_s==2, (Or(And(probability==0.5, dice_value==0, next_s==5), And(probability==0.5, dice_value==0, next_s==6))))
# choice4 = And(current_s==3, (Or(And(probability==0.5, dice_value==0,next_s==1), And(probability==0.5, dice_value==1, next_s==7))))
# choice5 = And(current_s==4, (Or(And(probability==0.5, dice_value==2, next_s==7), And(probability==0.5, dice_value==3, next_s==7))))
# choice6 = And(current_s==5, (Or(And(probability==0.5, dice_value==4, next_s==7), And(probability==0.5, dice_value==5, next_s==7))))
# choice7 = And(current_s==6, (Or(And(probability==0.5, next_s==2), And(probability==0.5, dice_value==6, next_s==7))))
# choice8 = And(current_s==7, next_s==7)

# choices = Or(choice1, choice2, choice3, choice4, choice5, choice6, choice7, choice8)


# print("Test 1: ")
# steps = 0
# property = And(next_s==7, dice_value==1)
# path_length = 10
# BMCencode(property, path_length, choices, 0)

# print("\n\nTest 2:")
# steps = 0
# property = And(next_s==7, dice_value==6)
# path_length = 10

# BMCencode(property, path_length, choices, 0)



"""
Notes:

Keep track of all the states that have been visited.
If we visit a state that has already been visited, we back up
all the way to the state prior to the first time we visited the original state.
Try again, but banning moving to a specific state?

OR

Add transition1=True, transition2=True for the 2 transitions in a choice
Try the first transition, and if that doesn't work try the 2nd one?

OR 

// Doesn't deal with the looping problem
if property = satisfiable: return
else, if property = unsatisfiable:
	Try again without the property
	Then move according to the correct 
Repeat

*** Might have to figure out what solver.model() does or how it determines the model. ***


Encoding:

Choices(cur_s, pv, dv, next_s)

Path-1: Choices(s0, p1, d1, s1)

Path-2: Choices(s0, p1, d1, s1) AND
        Choices(s1, p2, d2, s2)







"""