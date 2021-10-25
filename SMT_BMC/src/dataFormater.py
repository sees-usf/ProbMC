"""
    For storing nodes and edges and constructing the graph.
"""

class dataFormater: # Contains the edges and nodes to be printed to the user.
    def __init__(self):
        self.targetTrace = []
        self.nonTargetTrace = []
        
    def addTargetTrace(self, data):
        self.targetTrace.append(data)
        
    def addNonTargetTrace(self, data):
        self.nonTargetTrace.append(data)
        
    def addpercentagesTargetTrace(self, data):
        self.percentagesTargetTrace.append(data)
        
    def addpercentagesNonTargetTrace(self, data):
        self.percentagesNonTargetTrace.append(data)
        
    def returnTraces(self):
        result = "#\n\n"
        i = 0
        while(i < len(self.targetTrace)):
            result = result + self.targetTrace[i]
            i = i + 1
        result = result + "#\n\n"
        i = 0
        while(i < len(self.nonTargetTrace)):
            result = result + self.nonTargetTrace[i]
            i = i + 1
        return result