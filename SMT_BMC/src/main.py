"""
SMT Bounded Model Checker

Asks the user for the name of the module containing their model,
the path lengh to test for counterexamples, and the probability
the counterexamples must meet or go over to be considered a problem.
Returns whether or not there exists enough counterexamples to meet
or go over the given probability.

Please refer to module_template.py to learn how to create your own module file to represent your model.

Note: If a file cannot be found, ensure the program is being ran in the correct directory.
"""

from z3 import *
from BMC import *
import time

start = time.time()

# Obtain information about the model to check and what to check for.
model = input("Model name (exclude .py): ")
path_length = int(input("Provide a path length: "))
property_prob = float(input("Provide a probability to reach: "))

bmc = BMC(path_length, model, property_prob) # BMC initialization runs the SMT-BMC solver

end = time.time()
print(end - start)