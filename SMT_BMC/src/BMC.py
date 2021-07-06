"""SMT Bounded Model Checker Class"""

from z3 import *
import importlib

class BMC:

    def __init__(self, path_length, model, property_prob):
        """
        Initializes variables and finds the total probability of the property by looping through all possible steps
        and calling various functions within this class. Prints out whether or not a counterexample was found or not.
        """
        self.path_length = path_length # Max number of steps that can be taken to find the criteria-fitting cx
        self.model = importlib.import_module(model) # import the model's .py file
        self.property_prob = property_prob # The probability that must be met to consider a cx as found
        self.reached_cx_list = [] # List of cx's already reached, including prior steps.
        self.solver = Solver() # Holds all the constraints that define a bounded model
        self.solver.add(self.model.GetInitialStates()) # Add initial states to the bounded model definition
        # Increase the path_length until a counterexample that meets property_prob is found
        # or until the designated path_length is reached
        total_probability = 0
        for i in range(1, (self.path_length+1)):
            self.path_length = i
            self.PathEncoding(i)
            prob_from_step = self.Check(i)
            print(i, prob_from_step)
            total_probability += prob_from_step
            if total_probability >= property_prob:
                print("Yes, a counterexample was found at a probability greater than {}.".format(property_prob))
                break
        if total_probability < property_prob:
            print("No, a counterexample was not found at a probability greater than {}.".format(property_prob))

        print("Total probability: {}".format(total_probability))


    def PathEncoding(self, i):
        """
        Adds an encoding of the path with a length of path_length, which is incremented in __init__
        """
        path = self.model.GetStep(i)
        self.solver.add(path)

    def ExcludePath(self, cx_model):
        """
        Returns all found counterexamples to avoid recounting. The constraints are already complemented, so there 
        is no need to add NOT() before using AND() to merge the original model to the returned constraints.
        """
        # Creating a list of each variable's value to later merge together into the full counterexample
        cx_value_list = []
        for declaration in cx_model.decls():
            # print ("%s = %s" % (declaration.name(), cx_model[declaration]))
            name = str(declaration.name())
            if name.find('/0') != -1: #Avoiding '/0' in counter example (Appears when using '/' in model).
                break;
            if is_int_value(cx_model[declaration]):
                cx_value_list.append(Int(declaration.name())==cx_model[declaration].as_long())
            elif is_true(cx_model[declaration]) or is_false(cx_model[declaration]):
                cx_value_list.append(Bool(declaration.name())==cx_model[declaration])
            else:  # For Real Values
                if(name.find('p.') == -1): #Avoid adding the probability to counter example
                    cx_value_list.append(Real(declaration.name())==cx_model[declaration])
        
        cx = Not(And(cx_value_list))  # Counterexample to begin avoiding
        self.reached_cx_list.append(cx)  # Add to list of all the counterexamples to avoid
        return self.reached_cx_list

    def Check(self, i):
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
            for k in range(self.path_length+1):
                for d in cx_model.decls():
                    if d.name() == "p.{0}".format(k):
                        numerator = float(cx_model[d].numerator_as_long())
                        denominator = float(cx_model[d].denominator_as_long())
                        probability *= numerator/denominator
            
            self.solver.add(self.ExcludePath(cx_model))
            total_probability += probability  # Adding the probability of each cx at the given pathlength

        # Runs when no more counterexamples can be found
        self.solver.pop() # Removes the property to replace with a fresh one later, when Check() is called again
        return total_probability

    def GetCounterExample():
        pass
        # if counter example found, user can query to get the cx 
