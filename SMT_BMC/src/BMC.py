"""SMT Bounded Model Checker Class"""

from z3 import *
import importlib

class BMC:

    def __init__(self, solver, path_length, model):
        self.solver = solver
        self.path_length = path_length
        self.model = importlib.import_module(model) # import the model's .py file
        self.path = None # Call PathEncoding() before 
        self.reached_cx_list = [] # List of cx's already reached, including prior steps.

    def PathEncoding(self):
        """
        Returns an encoding of ALL paths with length up to path_length
        This path, starting from initial state and ending at the negation of the property 
        """
        for k in range(self.path_length):
            self.path = And(self.model.GetStep(k))
        return self.path

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

    def BMC(self):
        """Finds a counterexample model given a path and returns the probability of the path occuring"""
        # Build the Bounded Model
        initial_state = self.model.GetInitialStates()
        property = self.model.GetProperty(self.path_length)
        bounded_model = And(initial_state, self.path)
        if self.reached_cx_list:
            past_path_constraints = And(self.reached_cx_list)
            new_bounded_model = And(bounded_model, past_path_constraints)
            self.solver.add(new_bounded_model)
        else:
            self.solver.add(bounded_model)
        self.solver.push()
        self.solver.add(property)

        # Check the Bounded Model
        total_probability = 0
        print(self.solver)
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
            
            all_cx_constraints = self.ExcludePath(cx_model)
            self.solver.add(all_cx_constraints)
            total_probability += probability  # Adding the probability of each cx at the given pathlength

        # Runs when no more counterexamples can be found
        self.solver.pop()
        return total_probability