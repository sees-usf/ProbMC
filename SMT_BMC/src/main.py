"""
SMT Bounded Model Checker

Asks the user for a model file, property file, and initial state file.
Returns whether or not there exists enough counterexamples to meet,
or go over, a specified probability.

Please refer to the following for examples on how the files should be built:
    Model File: die.txt
    Property File: die_property.txt
    Initial State File: die_init.txt

Note: If a file cannot be found, ensure the program is being ran in the correct directory.
"""

from z3 import *
from BMC import *

# Obtain information about the model to check and what to check for.
model_file_name = property_file_name = init_states_file_name = "benchmark/"
model_file_name += input("Model file name (include .txt): ")
property_file_name += input("Property file name (include .txt): ")
init_states_file_name += input("Initial states file name (include .txt): ")
path_length = int(input("Provide a path length: "))

# Get the property probability threshold
with open(property_file_name, 'r') as property_file:
    last_line = property_file.readlines()[-1]
property_prob = (float(last_line.split("=", 1)[1]))

# Increase the path_length until a counterexample that meets the probability is found
# or until the designated path_length is reached
solver = Solver()
total_probability = 0
bmc = BMC(solver, path_length, model_file_name, property_file_name, init_states_file_name)
for i in range(1, (path_length+1)):
    bmc.path_length = i
    bmc.PathEncoding()
    prob_from_step = bmc.BMC()
    print(i, prob_from_step)
    total_probability += prob_from_step
    if total_probability >= property_prob:
        print("Yes, a counterexample was found at a probability greater than {}.".format(property_prob))
        break
if total_probability < property_prob:
    print("No, a counterexample was not found at a probability greater than {}.".format(property_prob))

print("Total probability: {}".format(total_probability))