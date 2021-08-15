"""
    For storing nodes and edges and constructing the graph.
"""

class Graph: # Contains the edges and nodes to be printed to the user.
    def __init__(self):
        self.nodeList = []
        self.edgeList = []
        
    def addVertex(self, name, values):          
        self.nodeList.append(node(name, values))
    
    def addEdge(self, Edge, probability):
        self.edgeList.append(edge(Edge, probability))
        
    def returnGraph(self): # Return a string that contains the contents of the nodes and edges.
        result = ""
        i = 0
        while(i < len(self.nodeList)):
            result = result + self.nodeList[i].getName() + " = " + "[" + self.nodeList[i].getValues() + "]" + "\n"
            i = i + 1
        i = 0
        while(i < len(self.edgeList)):
            result = result + self.edgeList[i].getEdge() + " " + self.edgeList[i].getProb() + "\n"
            i = i + 1
        return result
        
        
class node: # Node data structure.
    def __init__(self, name, values):
        self.nodeName = name
        self.nodeValues = values
        
    def getName(self):
        return self.nodeName
    
    def getValues(self):
        return self.nodeValues
        
class edge: # Edge data structure.
    def __init__(self, edge, probability):
        self.edge = edge
        self.probability = probability
        
    def getEdge(self):
        return self.edge
        
    def getProb(self):
        if(self.probability == -1):
            return ""
        return self.probability