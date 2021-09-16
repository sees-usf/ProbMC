"""
    For storing nodes and edges and constructing the graph.
"""

class Graph: # Contains the edges and nodes to be printed to the user.
    def __init__(self):
        self.nodeList = []
        self.edgeList = []
        self.terminalNodeList = []
        self.terminalEdgeList = []
        
    def addVertex(self, name, values):          
        self.nodeList.append(node(name, values))
    
    def addEdge(self, Edge, probability):
        self.edgeList.append(edge(Edge, probability))
        
    def addTerminalEdge(self, Edge, probability):
        self.terminalEdgeList.append(edge(Edge, probability))
        
    def addTerminalVertex(self, name, values):          
        self.terminalNodeList.append(node(name, values))
        
    def searchEdges(self, edge, prob):
        for x in self.edgeList:
            if(x.getEdge() == edge): # x.getProb() == prob
                return -1
        return 0
        
    def returnGraphNodes(self): # Return a string that contains the contents of the nodes.
        if(len(self.nodeList) < 1):
            return ""
        result = ""
        result = result + "[" + self.nodeList[0].getValues() + "]" + "\n" + "#" + "\n"
        i = 1
        while(i < len(self.nodeList)):
            result = result + "[" + self.nodeList[i].getValues() + "]" + "\n"
            i = i + 1
        return result
        
    def returnTerminalGraphNodes(self): # Return a string that contains the contents of the nodes.
        result = ""
        i = 0
        while(i < len(self.terminalNodeList)):
            result = result + "[" + self.terminalNodeList[i].getValues() + "]" + "\n"
            i = i + 1
        return result
		
    def returnGraphEdges(self):
        result = ""
        i = 0
        while(i < len(self.edgeList)):
            result = result + self.edgeList[i].getEdge() + "\n"
            i = i + 1
        return result

    def returnTerminalGraphEdges(self):
        result = ""
        i = 0
        while(i < len(self.terminalEdgeList)):
            string = str(self.terminalEdgeList[i].getEdge()[self.terminalEdgeList[i].getEdge().find("T")+1:len(self.terminalEdgeList[i].getEdge())])
            value = int(string)
            result = result + self.terminalEdgeList[i].getEdge()[0:self.terminalEdgeList[i].getEdge().find("T")] + str(value + len(self.nodeList) - 1) + "\n"
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
        if(self.probability == str(-1)):
            return ""
        return self.probability