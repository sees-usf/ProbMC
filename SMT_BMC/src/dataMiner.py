from dataFormater import*
from dataStored import*
import os

class dataMiner:

    def returnSubArray(self ,x ,y):
        subArray = []
        subArray.append(x)
        subArray.append(y)
        return subArray

    def returnSubArrayOne(self ,x):
        subArray = []
        subArray.append(x)
        return subArray
        
    def searchArray(self ,x ,y ,array):
        if(x == y):
            return False
        result = 0
        for i in range(0, len(array)):
            for j in range(0, len(array[i])):
                if(array[i][j] == x):
                    result += 1
                if(array[i][j] == y):
                    result += 1
            if(result > 1):
                return False
            else:
                result = 0
        return True
        
    def findFrequency(self, array, value):
        try:
            result = 0
            for i in range(0, len(array)):
                conditional = 0
                for j in range(0, len(value)):
                    if(array[i] == value[j]):
                        conditional += 1
                if(conditional == len(value)):
                    result += 1
        except:
            result = 0
            for i in range(0, len(array)):
                conditional = 0
                if(array[i] == value):
                    conditional += 1
                if(conditional == 1):
                    result += 1
        return result
        
    def mineTarget(self, stringArray):
        array = []
        index = stringArray.find(",")
        while(index != -1):
            tempVariable = stringArray[0:index]
            stringArray = stringArray[index+1:len(stringArray)]
            index = stringArray.find(",")
            if(tempVariable.find(' ') != -1):
                tempVariable = tempVariable[1:len(tempVariable)]
            array.append(int(tempVariable))
        tempVariable = stringArray[0:index]
        if(tempVariable.find(' ') != -1):
                tempVariable = tempVariable[1:len(tempVariable)]
        array.append(int(tempVariable))
        
        #Build redundancy array
        redundantFlag = 0
        redundancyArray = []
        for i in range(0, len(array)):
            for j in range(0, len(redundancyArray)):
                if(array[i] == redundancyArray[j]):
                    redundantFlag = 1
            if(redundantFlag == 0):
                redundancyArray.append(array[i])
            redundantFlag = 0

        #Build array of data to analysised
        valuesToSearch = []
        for x in range (0, len(redundancyArray)):
            valuesToSearch.append(self.returnSubArrayOne(redundancyArray[x]))
        for x in range (0, len(redundancyArray)):
            for y in range (0, len(redundancyArray)):
                if(self.searchArray(redundancyArray[x], redundancyArray[y], valuesToSearch)):
                    valuesToSearch.append(self.returnSubArray(redundancyArray[x], redundancyArray[y]))
        
        #Build output
        stringFrequency = ""
        for i in range(0, len(valuesToSearch)):
            if(len(valuesToSearch[i]) == 1):
                occuranceValue = self.findFrequency(array, valuesToSearch[i])
                stringFrequency = stringFrequency + str(valuesToSearch[i]) + " : " + str(occuranceValue) + "\n"
                self.data.addTransition(str(valuesToSearch[i]),occuranceValue)
            elif(len(valuesToSearch[i]) == 2):
                occuranceValue = int(abs(int(self.findFrequency(array, valuesToSearch[i][0])) - int(self.findFrequency(array, valuesToSearch[i][1]))))
                stringFrequency = stringFrequency + str(valuesToSearch[i]) + " : " + str(occuranceValue) + "\n"
                self.data.addTransition(str(valuesToSearch[i]),occuranceValue)
            
        self.outputData.addTargetTrace(stringFrequency + "\n")
        
    def mineNonTarget(self, stringArray):
        array = []
        index = stringArray.find(",")
        while(index != -1):
            tempVariable = stringArray[0:index]
            stringArray = stringArray[index+1:len(stringArray)]
            index = stringArray.find(",")
            if(tempVariable.find(' ') != -1):
                tempVariable = tempVariable[1:len(tempVariable)]
            array.append(int(tempVariable))
        tempVariable = stringArray[0:index]
        if(tempVariable.find(' ') != -1):
                tempVariable = tempVariable[1:len(tempVariable)]
        array.append(int(tempVariable))
        
        #Build redundancy array
        redundantFlag = 0
        redundancyArray = []
        for i in range(0, len(array)):
            for j in range(0, len(redundancyArray)):
                if(array[i] == redundancyArray[j]):
                    redundantFlag = 1
            if(redundantFlag == 0):
                redundancyArray.append(array[i])
            redundantFlag = 0

        #Build array of data to analysised
        valuesToSearch = []
        for x in range (0, len(redundancyArray)):
            valuesToSearch.append(self.returnSubArrayOne(redundancyArray[x]))
        for x in range (0, len(redundancyArray)):
            for y in range (0, len(redundancyArray)):
                if(self.searchArray(redundancyArray[x], redundancyArray[y], valuesToSearch)):
                    valuesToSearch.append(self.returnSubArray(redundancyArray[x], redundancyArray[y]))
        
        #Build output
        stringFrequency = ""
        for i in range(0, len(valuesToSearch)):
            if(len(valuesToSearch[i]) == 1):
                occuranceValue = self.findFrequency(array, valuesToSearch[i])
                stringFrequency = stringFrequency + str(valuesToSearch[i]) + " : " + str(occuranceValue) + "\n"
                self.dataNonTargets.addTransition(str(valuesToSearch[i]),occuranceValue)
            elif(len(valuesToSearch[i]) == 2):
                occuranceValue = int(abs(int(self.findFrequency(array, valuesToSearch[i][0])) - int(self.findFrequency(array, valuesToSearch[i][1]))))
                stringFrequency = stringFrequency + str(valuesToSearch[i]) + " : " + str(occuranceValue) + "\n"
                self.dataNonTargets.addTransition(str(valuesToSearch[i]),occuranceValue)
            
        self.outputData.addNonTargetTrace(stringFrequency + "\n")

    def __init__(self, inputModel):
        f = open(os.getcwd() + "\\programData\\" + inputModel + ".output", "r")
        tempString = f.read()
        f.close()
        
        self.outputData = dataFormater()
        self.data = dataStored()
        self.dataNonTargets = dataStored()
        
        index = tempString.find("\n")
        minnerFlag = -1
        while(index != -1):
            if(tempString[0:tempString.find("\n")].find("#") != -1):
                minnerFlag += 1
            else:
                if(minnerFlag == 0):
                    self.mineTarget(tempString[0:index] + '\0')
                elif(minnerFlag == 1):
                    self.mineNonTarget(tempString[0:index] + '\0')
            tempString = tempString[index+1:len(tempString)]
            index = tempString.find("\n")
        
        fOut = open(inputModel + ".MinnedData", "a")
        fOut.truncate(0)
        fOut.write(str(self.outputData.returnTraces()))
        fOut.write("----------------------------------------------------------------------------------------------------\n\n")
        fOut.write(str(self.data.returnData()))
        fOut.write("\n#\n\n")
        fOut.write(str(self.dataNonTargets.returnData()))
        fOut.close()