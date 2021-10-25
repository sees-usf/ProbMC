"""
    
"""

class dataStored:
    def __init__(self):
        self.transition = []
        self.transitionValue = []
        
        self.transitionRedundancy = []
        
    def addTransition(self, transition, value):
        self.transition.append(transition)
        self.transitionValue.append(value)
        
    def returnData(self):
        self.transitionRedundancy.clear()
        adjacentRedundancyValues = []
        adjacentRedundancyLengths = []
        Values = []
        
        for i in range(0, len(self.transition)):
            try:
                index = self.transitionRedundancy.index(self.transition[i])
            except:
                self.transitionRedundancy.append(self.transition[i])
                adjacentRedundancyValues.append(self.transitionValue[i])
                adjacentRedundancyLengths.append(0)
                Values.append(0)
        
        result = ""
        for i in range(0 , len(self.transitionRedundancy)):
            for j in range(0, len(self.transition)):
                if(self.transitionRedundancy[i] == self.transition[j] and adjacentRedundancyValues[i] == self.transitionValue[j]):
                    Values[i] += 1
                adjacentRedundancyLengths[i] += 1
                    
            result = result + "(" + self.transitionRedundancy[i] + ":" + str(adjacentRedundancyValues[i]) + ") " + str(float(Values[i]/adjacentRedundancyLengths[i])) + "\n"
            
        return result