""" Use this template to build your own model! """

""" {Model_Name} Module - Module to hold information regarding the {model_name} model.
    Contains information on the following:
    (1) Transition Relations
    (2) Property
    (3) Initial State
"""

from z3 import *

def GetStep(step):
    """ Transition Relations """
    # Here, you can define your own transition relations.
    # First, initialize your z3 variables.
    # [variable name 1] = [Int, Bool, or Real]('[variable name 2]{0}'.format([step or step+1]))
    #   [variable name 1]: The name used while you are building the transition relations here.
    #                      Note that if the variable has a current and a next state, two variables will
    #                      have to be named with unique variable names.
    #   [variable name 2]: The name shown if the model/transition relations are printed out.
    #                      If there exists a current and next state for a variable, ensure that
    #                      this name is the same for both the current state and the next state variables.
    #   [Int, Bool, or Real]: Choose whether this variable is of integer, boolean, or real type.
    #   [step or step+1]: Use 'step' if the variable is meant to act as the current state of the variable.
    #                     Use 'step+1' if the variable is meant to act as the next state of the variable.
    #                     For variables that do not have a current/next state, choose either step or step+1 to represent
    #                         all variables without a current/next state.  As an example, if step is chosen, then
    #                         all values without a current/next state will use .format(step).
    #   All models need a probability variable, since this program calculates counterexamples based
    #       on whether or not a counterexample can meet a given probability.
    #   See the variables used below as an example:
    current_state = Int('s{0}'.format(step))
    probability = Real('p{0}'.format(step+1))
    dice_value = Int("dv{0}".format(step+1))
    next_state = Int("s{0}".format(step+1))

    # Next, set up the model's transition relations.
    # Begin by setting the values your variables can range from.
    # ranges = And(And([lower bound] [< or <=] [variable1], [variable1] [< or <=] [upper bound]), [variable2] == [value])
    # The example above illustrates how to set an upper and lower bound for a variable, variable1, and how to set a variable equal to a value.
    # Now, create the transition relations through z3 functions:
    #   Examples: And([condition1], [condition2]) | Or([condition1], [condition2]) | Xor([condition1], [condition2]) | Implies([condition1], [condition2]) | Not([condition1]) ... and more!
    #
    # Compare the below example with the die model's transition relation:
	#   s : [0..7] init 0;
	#   d : [0..6] init 0;
    #
    # 	[] s=0 -> 0.5 : (s'=1) + 0.5 : (s'=2);
	#   [] s=1 -> 0.5 : (s'=3) + 0.5 : (s'=4);
	#   [] s=2 -> 0.5 : (s'=5) + 0.5 : (s'=6);
	#   [] s=3 -> 0.5 : (s'=1) + 0.5 : (s'=7) & (d'=1);
	#   [] s=4 -> 0.5 : (s'=7) & (d'=2) + 0.5 : (s'=7) & (d'=3);
	#   [] s=5 -> 0.5 : (s'=7) & (d'=4) + 0.5 : (s'=7) & (d'=5);
	#   [] s=6 -> 0.5 : (s'=2) + 0.5 : (s'=7) & (d'=6);
	#   [] s=7 -> (s'=7);
    # 
    # Once all the transition relations are defined, with the range in its own variable and the transitions other, unique variables
    # Set a variable, 'step', equal to either: ranges && (choice1 || choice2 || choice3 || etc.) for asynchronous models, or 
    #                                          ranges && choice1 && choice2 && choice3 && etc. for synchronous models
    # Here's how the two would look like in code, in respective order:
    #   step = And(ranges, Or([1st choice], [2nd choice], [continue until all your choices are in this Or()]))
    #   step = And(ranges, [1st choice], [2nd choice], [continue until all your choices are in this And()])
    # The below example is an asynchronous model.
    # After setting step equal to its condition, add a line, "return step" to finish off this GetStep() function.
    ranges = And(And(0 <= current_state, current_state <= 7), And(0 <= next_state, next_state <= 7), And(0 <= dice_value, dice_value <= 6), And(0 <= probability, probability <= 1))
    choice1 = And(current_state==0, (Or(And(probability==0.5, dice_value==0, next_state==1), And(probability==0.5, dice_value==0, next_state==2))))
    choice2 = And(current_state==1, (Or(And(probability==0.5, dice_value==0, next_state==3), And(probability==0.5, dice_value==0, next_state==4))))
    choice3 = And(current_state==2, (Or(And(probability==0.5, dice_value==0, next_state==5), And(probability==0.5, dice_value==0, next_state==6))))
    choice4 = And(current_state==3, (Or(And(probability==0.5, dice_value==0, next_state==1), And(probability==0.5, dice_value==1, next_state==7))))
    choice5 = And(current_state==4, (Or(And(probability==0.5, dice_value==2, next_state==7), And(probability==0.5, dice_value==3, next_state==7))))
    choice6 = And(current_state==5, (Or(And(probability==0.5, dice_value==4, next_state==7), And(probability==0.5, dice_value==5, next_state==7))))
    choice7 = And(current_state==6, (Or(And(probability==0.5, next_state==2), And(probability==0.5, dice_value==6, next_state==7))))
    choice8 = And(current_state==7, next_state==7, dice_value==0)

    step = And(ranges, Or(choice1, choice2, choice3, choice4, choice5, choice6, choice7, choice8)) # OR()ing choices together = Asynchrounous

    return step

def GetProperty(path_length):
    """ Property """
    # This is where the property the program is attempting to find a counterexample for is defined.
    # First, initiate any variables that are needed for the property.
    # [variable name 1] = [Int, Bool, or Real]('[variable name 2]{0}'.format([path_length or path_length-1]))
    #   [variable name 1]: The name used while you are building the property in this file.
    #                      Note that if the variable has a current and a next state, two variables will
    #                      have to be named with unique variable names.
    #   [variable name 2]: The name shown if the model is printed out.
    #                      If there exists a current and next state for a variable, ensure that
    #                      this name is the same for both the current state and the next state variables.
    #                      This name must also match the ones from the transition relations defined above.
    #   [Int, Bool, or Real]: Choose whether this variable is of integer, boolean, or real type.
    #                         This must match the typing the variable is assigned to in GetStep()
    #   [path_length or path_length-1]: Use 'path_length-1' if the variable is defined in GetStep() through 'step'
    #                                   Use 'path_length' if the variable is defined in GetStep() through 'step+1'
    # After this, set a variable named 'property' equal to the model's property condition. Build this condition
    # through the z3 functions described in GetStep(). This solver currently does not negate the property on its own,
    # so you will have to negate the property manually. Once your conditions are set, wrap around the whole condition
    # like so: Not([property we want to find counter examples for]).
    # View the example below for a property in which, (next_state != 7) || (dice_value != 1).
    # Once a property is set, add a line, "return property" to finish this function, GetProperty().
    dice_value = Int("dv{0}".format(path_length))
    next_state = Int("s{0}".format(path_length))

    property = Not(Or(next_state!=7, dice_value!=1))

    return property

def GetInitialStates():
    """ Initial States """
    # Set initial states for the model here.
    # Define the variables with initial states here.
    # [variable name 1] = [Int, Bool, or Real]('[variable name 2][1 or 0]')
    #   [variable name 1]: The name used while you are building the initial states in this function.
    #                      Note that if the variable has a current and a next state, two variables will
    #                      have to be named with unique variable names.
    #   [variable name 2]: The name shown if the model is printed out.
    #                      The name must match the ones from the transition relations defined in GetStep().
    #   [Int, Bool, or Real]: Choose whether this variable is of integer, boolean, or real type.
    #                         This must match the typing the variable is assigned to in GetStep().
    #   [1 or 0]: Use '0' if the variable is defined in GetStep() through 'step'
    #             Use '1' if the variable is defined in GetStep() through 'step+1'
    # Create a variable named 'initial_states' and set it equal to And([variable1]==[initial value of variable1], [variable2]==[initial value of variable2], etc.)
    #    or, if there is only one initial state, set 'initial_states' equal to [variable1]==[initial value of variable1].
    # Here are how the two examples would look like in code, in respective order:
    #   initial_states = And(current_state1==0, current_state2==0)
    #   initial_states = current_state1==0
    # Below shows how the initial states for the die model are set (see GetStep() to view the die model's Prism representation).
    # Now that the initial_states are defined, add a line, "return initial_states" to complete this GetInitialStates() function.
    dice_value = Int("dv1")
    current_state = Int("s0")

    initial_states = And(current_state==0, dice_value==0)

    return initial_states