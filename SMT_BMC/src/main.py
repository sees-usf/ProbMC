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
import sys

model = ("examples." + str(sys.argv[1])) # Will only accept model in examples.
bmc = BMC(int(sys.argv[2]), model, 1, 1, str(sys.argv[1])) 