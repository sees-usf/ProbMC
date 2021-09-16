"""SMT Bounded Model Checker Class"""

from z3 import *
from Graph import *
import importlib

class BMC:

    def __init__(self, path_length, model, property_prob, debug, modelName):
        """
        Initializes variables and finds the total probability of the property by looping through all possible steps
        and calling various functions within this class. Prints out whether or not a counterexample was found or not.
        """
        self.path_length = path_length # Max number of steps that can be taken to find the criteria-fitting cx
        self.model = importlib.import_module(model) # import the model's .py file
        self.property_prob = property_prob # The probability that must be met to consider a cx as found
        self.reached_cx_list = [] # List of cx's already reached, including prior steps.
        self.cx_list_asStrings = [] # List of cx's in the form of strings.(Only used when printing)
        self.cx_list_probablities = [] # List of only the probabilities.(Only used when printing)
        self.nodeList = [] # List of nodes.(Only used when printing)
        self.priorValues = [] # Keeps track of prior counter examples.(Only used when printing)
        self.priorTerminalValues = []
        self.T = -1 # Keeps track of T. (Only used when printing)
        self.graph = Graph()
        self.booleanToCreateKey = 1
        self.nodeCounter = 0 # Couting nodes for printing. (Only used when printing)
        self.solver = Solver() # Holds all the constraints that define a bounded model
        self.solver.add(self.model.GetInitialStates()) # Add initial states to the bounded model definition
        # Increase the path_length until a counterexample that meets property_prob is found
        # or until the designated path_length is reached
        total_probability = 0
        for i in range(1, (self.path_length+1)):
            self.path_length = i
            self.PathEncoding(i)
            prob_from_step = self.Check(i, debug, modelName)
            print(i, prob_from_step)
            total_probability += prob_from_step
            #if total_probability >= property_prob:
            #    print("Yes, a counterexample was found at a probability greater than {}.".format(property_prob))
            #    break
        #if total_probability < property_prob:
        #    print("No, a counterexample was not found at a probability greater than {}.".format(property_prob))

        print("Total probability: {}".format(total_probability))
		
        if(debug == 1): # Clears txt file and prints graph.
            f = open(modelName[modelName.find('.')+1:len(modelName)] + ".output", "a") # Write to the result.txt file.
            f.truncate(0)
            f.write("#\n" + self.graph.returnGraphNodes() + "#\n" + self.graph.returnTerminalGraphNodes() + "#\n" + self.graph.returnGraphEdges() + self.graph.returnTerminalGraphEdges())
            f.close()


    def PathEncoding(self, step):
        """
        Adds an encoding of the path with a length of path_length, which is incremented in __init__
        """
        path = self.model.GetStep(step)
        self.solver.add(path)
        
    def ExcludePath(self, cx_model, debug, modelName):
        """
        Returns all found counterexamples to avoid recounting. The constraints are already complemented, so there 
        is no need to add NOT() before using AND() to merge the original model to the returned constraints.
        """
        # Creating a list of each variable's value to later merge together into the full counterexample
        cx_value_list = []
        for z in self.model.GetTransitionProbStrings(): # For all the probabilities
            self.cx_list_probablities.append(cx_model[z])
        for z in self.model.GetStateVaribleStrings():
            if is_int_value(cx_model[z]): # For Integer Values
                cx_value_list.append(z == cx_model[z])
                self.cx_list_asStrings.append(str(z)+" == "+str(cx_model[z]))
                #state[z]=cx_model[z]
            elif is_true(cx_model[z]) or is_false(cx_model[z]): # For Boolean Values
                cx_value_list.append(z == cx_model[z])
                self.cx_list_asStrings.append(str(z)+" == "+str(cx_model[z]))
            else:  # For Real Values
                cx_value_list.append(z == cx_model[z])
                self.cx_list_asStrings.append(str(z)+" == "+str(cx_model[z]))
        
        cx = Not(And(cx_value_list))  # Counterexample to begin avoiding
        self.reached_cx_list.append(cx)  # Add to list of all the counterexamples to avoid
        
        # Prints results to txt file
        if(debug == 1):
            if(self.booleanToCreateKey == 1):
                self.PrintKey(modelName)
                self.booleanToCreateKey = 0
            self.Print(modelName)
            self.cx_list_asStrings.clear()
            
        return self.reached_cx_list
        
    def Check(self, i, debug, modelName):
        """
        Finds all counterexample models given a path at its most recent step and returns the probability of the path occuring
        """
        # Initial state was added in __init__, the path was added through PathEncoding(), so the bounded model
        # only needs any prior counterexamples found and the property to be added as constraints.
        if self.reached_cx_list:
            self.solver.add(self.reached_cx_list)
        self.solver.push() # Saves initial states, path, and past counterexamples. These won't be removed on solver.pop()
        property = self.model.GetProperty(i) # Return property regarding the most recent step
        self.solver.add(property)
        # print(self.solver.check()) //Print "sat/unsat"

        # Check the Bounded Model
        total_probability = 0

        # Calculate the probability of all counterexamples that can be generated
        while(self.solver.check() == sat): # Runs when a counterexample is found
            cx_model = self.solver.model()

            # Find probability of the counterexample by multiplying all of the step's probabilities together
            probability = 1
            for z in self.model.GetTransitionProbStrings():
                numerator = float(cx_model[z].numerator_as_long())
                denominator = float(cx_model[z].denominator_as_long())
                probability *= numerator/denominator
            
            self.solver.add(self.ExcludePath(cx_model, debug, modelName))
            total_probability += probability  # Adding the probability of each cx at the given pathlength

        # Runs when no more counterexamples can be found
        self.solver.pop() # Removes the property to replace with a fresh one later, when Check() is called again
        return total_probability

    def PrintKey(self, modelName):
        """
        Generates a string that will contain only the key to the counter examples from the simulation. 
        """
        keyString = self.cx_list_asStrings[0][0:self.cx_list_asStrings[0].find('.')]
        i = 1
        while(i < len(self.cx_list_asStrings)):
            if(self.cx_list_asStrings[i].find("."+"0") != -1):
                keyString = keyString + ", " + self.cx_list_asStrings[i][0:self.cx_list_asStrings[i].find('.')]
                i = i + 1
            else:
                self.graph.addVertex("-1", keyString) # Adding node
                break;
        
    def Print(self, modelName):
        """
        Generates a txt file named "results.txt" that will contain the counter examples from the simulation.
        """
        subString = self.cx_list_asStrings[0][self.cx_list_asStrings[0].find('=') + 3:len(self.cx_list_asStrings[0])]
        value = 0 # Keeps track of the step value to keep all the variables of the same step together.
        i = 1 # used to index the loop.
        firstRun = 1 # Used to distinguish the first run.
        previousEdge = 0 # Keeps track of the previous edge. 
        
        while(i < len(self.cx_list_asStrings)):
            if(self.cx_list_asStrings[i].find("."+str(value)) != -1):
                subString = subString + ", " + self.cx_list_asStrings[i][self.cx_list_asStrings[i].find('=') + 3:len(self.cx_list_asStrings[i])] # Collecting all variables of the same step.
                i = i + 1
            else:
                try: # Try and except used to search for the node to make sure it is not printed twice.
                    x = self.priorValues.index(str(subString))
                    if(firstRun == 1): # The first run is just used to store the first edge as the next edge is not known yet.
                        firstRun = 0
                    else:
                        if(self.graph.searchEdges(str(previousEdge) + " " + (str(x)), str(self.cx_list_probablities[value])) == 0): # Search for redundant edges.
                            self.graph.addEdge(str(previousEdge) + " " + (str(x)), str(self.cx_list_probablities[value])) # Adding edge.
                    previousEdge = x
                 
                except:
                    if(firstRun == 1):
                        self.graph.addVertex((str(self.nodeCounter)), subString) # Adding node
                        firstRun = 0
                    else:
                        self.graph.addVertex((str(self.nodeCounter)), subString) # Adding node
                        if(self.graph.searchEdges(str(previousEdge) + " " + (str(self.nodeCounter)), str(self.cx_list_probablities[value])) == 0): # Search for redundant edges.
                            self.graph.addEdge(str(previousEdge) + " " + (str(self.nodeCounter)), str(self.cx_list_probablities[value])) # Adding edge.
                    previousEdge = self.nodeCounter
                        
                    self.nodeCounter = self.nodeCounter + 1
                    self.priorValues.append(str(subString))
                    
                subString = self.cx_list_asStrings[i][self.cx_list_asStrings[i].find('=') + 3:len(self.cx_list_asStrings[i])]
                i = i + 1
                value = value + 1
               
        try: # Adding the last node "T" which holds the property
            x = self.priorTerminalValues.index(subString)
            if(self.graph.searchEdges(str(previousEdge) + " " + ("T" + str(self.T)), str(-1)) == 0): # Search for redundant edges.
                self.graph.addTerminalEdge(str(previousEdge) + " " + ("T" + str(self.T)), str(-1)) # Adding last edge.
        except:
            self.T = self.T + 1
            self.graph.addTerminalVertex("T" + str(self.T), subString)
            self.priorTerminalValues.append(subString)
            if(self.graph.searchEdges(str(previousEdge) + " " + ("T" + str(self.T)), str(-1)) == 0): # Search for redundant edges.
                self.graph.addTerminalEdge(str(previousEdge) + " " + ("T" + str(self.T)), str(-1)) # Adding last edge.
        
        return
        
    def GetCounterExample():
        pass
        # if counter example found, user can query to get the cx 
        