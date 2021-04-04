import importlib
from ic3 import *


# Prints Result
def printResult(result):
    if isinstance(result, bool):
        if result:
            print("There is a counterexample")
        else:
            print("P is verified")
    else:
        print("Inconclusive")


# Starts IC3/ Runs models
print("**MODEL 1**")
printResult(ic3(importlib.import_module("sampleModel1")))

print("\n**MODEL 2**")
printResult(ic3(importlib.import_module("sampleModel2")))

print("\n**MODEL 3**")
printResult(ic3(importlib.import_module("sampleModel3")))

print("\n**MODEL 4**")
printResult(ic3(importlib.import_module("sampleModel4")))

print("\n**MODEL 5**")
printResult(ic3(importlib.import_module("sampleModel5")))

print("\n**MODEL 6**")
printResult(ic3(importlib.import_module("sampleModel6")))

print("\n**MODEL 7**")
printResult(ic3(importlib.import_module("sampleModel7")))
