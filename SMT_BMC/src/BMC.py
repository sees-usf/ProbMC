"""SMT Bounded Model Checker Class"""

from z3 import *
import importlib

class BMC:

    def __init__(self, path_length, model, property_prob):
        self.solver = Solver() # Holds all the constraints that define a bounded model
        self.path_length = path_length # Max number of steps that can be taken to find the criteria-fitting cx
        self.model = importlib.import_module(model) # import the model's .py file
        self.property_prob = property_prob # The probabily that must be met to consider a cx as found
        self.path = None # PathEncoding() creates the path
        self.reached_cx_list = [] # List of cx's already reached, including prior steps.
        self.solver.add(self.model.GetInitialStates()) # Add initial states to the bounded model definition
        # Increase the path_length until a counterexample that meets property_prob is found
        # or until the designated path_length is reached
        total_probability = 0
        for i in range(1, (self.path_length+1)):
            self.path_length = i
            self.PathEncoding()
            prob_from_step = self.Check()
            print(i, prob_from_step)
            total_probability += prob_from_step
            if total_probability >= property_prob:
                print("Yes, a counterexample was found at a probability greater than {}.".format(property_prob))
                break
        if total_probability < property_prob:
            print("No, a counterexample was not found at a probability greater than {}.".format(property_prob))

        print("Total probability: {}".format(total_probability))


    def PathEncoding(self):
        """
        Adds an encoding of the path with length a length of path_length, whic is incremented in __init__
        """
        self.path = self.model.GetStep(self.path_length-1)
        self.solver.add(self.path)

    def ExcludePath(self, cx_model):
        """
        Returns all found counterexamples to avoid recounting. The constraints are already complemented, so there 
        is no need to add NOT() before using AND() to merge the original model to the returned constraints.
        """
        # Creating a list of each variable's value to later merge together into the full counterexample
        for k in range(self.path_length+1):
            cx_value_list = []
            for declaration in cx_model.decls():
                # print ("%s = %s" % (d.name(), cx_model[d]))
                if is_int_value(cx_model[declaration]):
                    cx_value_list.append(Int(declaration.name())==cx_model[declaration].as_long())
                elif is_true(cx_model[declaration]) or is_false(cx_model[declaration]):
                    cx_value_list.append(Bool(declaration.name())==cx_model[declaration])
                else:  # For Real Values
                    numerator = float(cx_model[declaration].numerator_as_long())
                    denominator = float(cx_model[declaration].denominator_as_long())
                    p_value = numerator/denominator
                    cx_value_list.append(Real(declaration.name())==p_value)
        
        cx = Not(And(cx_value_list))  # Counterexample to begin avoiding
        self.reached_cx_list.append(cx)  # Add to list of all the counterexamples to avoid
        all_cx_constraints = And(self.reached_cx_list)  # And() together all of the counterexamples to avoid
        return all_cx_constraints

    def Check(self):
        """Finds a counterexample model given a path and returns the probability of the path occuring"""
        # Initial state was added in __init__, the path was added through PathEncoding(), so the bounded model
        # only needs any prior counterexamples found and the property to be added as constraints.
        if self.reached_cx_list:
            self.solver.add(self.reached_cx_list)
        self.solver.push() # Saves initial states, path, and past counterexamples. These won't be removed on solver.pop()
        property = self.model.GetProperty(self.path_length-1) # Return property regarding the most recent step
        self.solver.add(property)

        # Check the Bounded Model
        total_probability = 0

        # Calculate the probability of all counterexamples that can be generated
        while(self.solver.check() == sat): # Runs when a counterexample is found
            cx_model = self.solver.model()

            # Find probability of the counterexample by multiplying all of the step's probabilities together
            probability = 1
            for k in range(self.path_length+1):
                for d in cx_model.decls():
                    if d.name() == "p{0}".format(k):
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
