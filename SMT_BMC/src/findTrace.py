"""SMT Bounded Model Checker Class"""

from z3 import *
import importlib
import os

class findTrace:

    def __init__(self, path_length, model, debug, modelName):
        """
        Initializes variables and finds the total probability of the property by looping through all possible steps
        and calling various functions within this class. Prints out whether or not a counterexample was found or not.
        """
        
        self.path_length = path_length # Max number of steps that can be taken to find the criteria-fitting cx
        self.model = importlib.import_module(model) # import the model's .py file
        self.reached_cx_list = [] # List of cx's already reached, including prior steps.
        self.cx_list_asStrings = [] # List of cx's in the form of strings.(Only used when printing)
        self.cx_list_probablities = [] # List of only the probabilities.(Only used when printing)
        self.Transitions_strings = [] # List of onty the transisitons.
        self.targetStates = ""
        self.nonTargetStates = ""
        
        self.aquiredTagetLengths = [] # Used for returning the lengths to which a target state as aquired. (Used for stocastic)
        
        self.solver = Solver() # Holds all the constraints that define a bounded model
        self.solver.add(self.model.GetInitialStates()) # Add initial states to the bounded model definition

        total_probability = self.PathEncoding(modelName, 1)
        
        #print("Total probability: {}".format(total_probability))
        
        f = open(os.getcwd() + "\\programData\\" + modelName[modelName.find('.')+1:len(modelName)] + ".Target" + ".output", "a") # Write to the result.txt file.
        f.write(self.targetStates)
        f.close()
        f = open(os.getcwd() + "\\programData\\" + modelName[modelName.find('.')+1:len(modelName)] + ".nonTarget" + ".output", "a") # Write to the result.txt file.
        f.write(self.nonTargetStates)
        f.close()
            
    def PathEncoding(self, modelName, debug):
        """
        Adds an encoding of the path with a length of path_length, which is incremented in __init__
        """
        
        # or until the designated path_length is reached
        total_probability = 0
        for i in range(1, (self.path_length+1)):
            path = self.model.GetStep(i-1, i)
            self.solver.add(path)
            prob_from_step = self.CheckNonTarget(i, debug, modelName)
            #print("Inverted: ",i, prob_from_step)
            prob_from_step = self.Check(i, debug, modelName)
            #print("Normal: ",i, prob_from_step)
            total_probability += prob_from_step
            
        return total_probability
        
    def ExcludePath(self, cx_model, debug, modelName, outputFlag):
        """
        Returns all found counterexamples to avoid recounting. The constraints are already complemented, so there 
        is no need to add NOT() before using AND() to merge the original model to the returned constraints.
        """
        
        # Creating a list of each variable's value to later merge together into the full counterexample
        cx_value_list = []
        for probability in self.model.GetTransitionProbStrings(): # For all the probabilities.
            self.cx_list_probablities.append(cx_model[probability])
        for stateVariable in self.model.GetStateVaribleStrings(): # Where z represents any state variable.
            if is_int_value(cx_model[stateVariable]): # For Integer Values
                cx_value_list.append(stateVariable == cx_model[stateVariable])
            elif is_true(cx_model[stateVariable]) or is_false(cx_model[stateVariable]): # For Boolean Values
                cx_value_list.append(stateVariable == cx_model[stateVariable])
            else:  # For Real Values
                cx_value_list.append(stateVariable == cx_model[stateVariable])
                
        for transistion in self.model.GetTransition():
            self.Transitions_strings.append(str(cx_model[transistion]))
        
        cx = Not(And(cx_value_list))  # Counterexample to begin avoiding
       
        # Prints results to txt file
        if(outputFlag == 1):
            self.printTarget()
            self.Transitions_strings.clear()
        elif(outputFlag == 0):
            self.printNonTarget()
            self.Transitions_strings.clear()
            
        return cx
       
    def Check(self, stepIndex, debug, modelName):
        """
        Finds all counterexample models given a path at its most recent step and returns the probability of the path occuring
        """
        
        # Initial state was added in __init__, the path was added through PathEncoding(), so the bounded model
        # only needs any prior counterexamples found and the property to be added as constraints.
        if self.reached_cx_list:
            self.solver.add(self.reached_cx_list)
        
        self.solver.push() # Saves initial states, path, and past counterexamples. These won't be removed on solver.pop()
        property = self.model.GetProperty(stepIndex) # Return property regarding the most recent step
        
        self.solver.add(property)
        if(self.solver.check() == sat):
            self.aquiredTagetLengths.append(stepIndex)

        total_probability = 0
        
        constraintB = []
        constraintB.append(property)

        # Calculate the probability of all counterexamples that can be generated
        while(self.solver.check(constraintB) == sat): # Runs when a counterexample is found
            cx_model = self.solver.model()

            # Find probability of the counterexample by multiplying all of the step's probabilities together
            probability = 1
            for z in self.model.GetTransitionProbStrings():
                numerator = float(cx_model[z].numerator_as_long())
                denominator = float(cx_model[z].denominator_as_long())
                probability *= numerator/denominator
            
            total_probability += probability  # Adding the probability of each cx at the given pathlength
            constraintB.append(self.ExcludePath(cx_model, debug, modelName, 1))

        self.solver.pop() # Removes the property to replace with a fresh one later, when Check() is called again
        return total_probability
        
    def CheckNonTarget(self, stepIndex, debug, modelName):
        """
        Finds all counterexample models given a path at its most recent step and returns the probability of the path occuring
        """  
        property = self.model.GetProperty(stepIndex) # Return property regarding the most recent step

        total_probability = 0
        
        constraintG = []
        constraintG.append(Not(property))

        while(self.solver.check(constraintG) == sat): # Runs when a counterexample is found
            cx_model = self.solver.model()

            # Find probability of the counterexample by multiplying all of the step's probabilities together
            probability = 1
            for z in self.model.GetTransitionProbStrings():
                numerator = float(cx_model[z].numerator_as_long())
                denominator = float(cx_model[z].denominator_as_long())
                probability *= numerator/denominator
            
            total_probability += probability  # Adding the probability of each cx at the given pathlength
            constraintG.append(self.ExcludePath(cx_model, debug, modelName, 0))

        return total_probability
        
    def printTarget(self):
        """
        Generates a txt file named "results.txt" that will contain the transistions.
        """
        
        i = 0
        while(i < len(self.Transitions_strings) - 1):
            self.targetStates = self.targetStates + self.Transitions_strings[i] + ", "
            i += 1
        self.targetStates = self.targetStates + self.Transitions_strings[i]
        self.targetStates = self.targetStates + "\n"
        return
     
    def printNonTarget(self):
        """
        Generates a txt file named "results.txt" that will contain the transistions.
        """
        
        i = 0
        while(i < len(self.Transitions_strings) - 1):
            self.nonTargetStates = self.nonTargetStates + self.Transitions_strings[i] + ", "
            i += 1
        self.nonTargetStates = self.nonTargetStates + self.Transitions_strings[i]
        self.nonTargetStates = self.nonTargetStates + "\n"
        return