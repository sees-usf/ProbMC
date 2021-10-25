import sys
import os
from dataMiner import*
from findTrace import *

model = ("examples." + str(sys.argv[1])) # Will only accept model in examples.
modelName = str(sys.argv[1])
bmc = findTrace(int(sys.argv[2]), model, 1, str(sys.argv[1]))

outputFile = open(os.getcwd() + "\\programData\\" + modelName + ".output", "a") 
outputFile.truncate(0)   
f = open(os.getcwd() + "\\programData\\" + modelName + ".Target" + ".output", "r")
tempString = "#\n" + f.read()
outputFile.write(tempString)
f.close()
f = open(os.getcwd() + "\\programData\\" + modelName + ".nonTarget" + ".output", "r")
tempString = "#\n" + f.read()
outputFile.write(tempString)
f.close()
outputFile.close()

data = dataMiner(modelName)