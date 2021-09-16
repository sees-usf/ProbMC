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

# Obtain information about the model to check and what to check for.
""" # Temporarily commenting out.
example = "?"
while example != "y" and example != "n":
  debugMode = input("Would you like to print your counter examples? (y/n): ")
  if debugMode == "y":
    debug = 1
  else:
    debug = 0
    
  example = input("Is your model in the examples folder or in the main src directory? (y/n): ")
  if example == "y":
    model = "examples." + input("Model name (exclude .py): ")
  elif example == "n":
    model = input("Model name (exclude .py): ")
"""

debug = 1 # Will always print output to file.
inputString = input("Model name (exclude .py) and length : ")
model = "examples." + str(inputString[0:inputString.find(" ")]) # Will only accept model in examples.

# path_length = int(input("Provide a path length: "))
#property_prob = float(input("Provide a probability to reach: "))

path_length = int(inputString[inputString.find(" ")+1:len(inputString)])
property_prob = 1 # Run indefinitely

bmc = BMC(path_length, model, property_prob, debug, str(model)) # BMC initialization runs the SMT-BMC solver
