"""SMT Bounded Model Checker Class"""

from z3 import *
import model, property, initial_states
import importlib

class BMC:

    def __init__(self, solver, path_length, model_file, property_file, init_states_file):
        self.solver = solver
        self.path_length = path_length
        self.model_file = model_file
        self.property_file = property_file
        self.init_states_file = init_states_file
        self.path = None # Call PathEncoding() before 
        self.reached_cx_list = [] # List of cx's already reached, including prior steps.

    def PathEncoding(self):
        """
        Returns an encoding of ALL paths with length up to path_length
        This path, starting from initial state and ending at the negation of the property 
        """
        choices_list = []  # List of choice constraints after a path_length amount of steps
        for k in range(self.path_length):
            # Read in given model file
            with open(self.model_file, 'r') as modelfile :
                filedata = modelfile.read()

            # Replace the variable names
            filedata = filedata.replace('current_s', 's{0}'.format(k))
            filedata = filedata.replace('probability', 'p{0}'.format(k+1))
            filedata = filedata.replace('next_s', 's{0}'.format(k+1))
            filedata = filedata.replace('dice_value', 'dv{0}'.format(k+1))

            # Rewrite model.py GetStep() with new model
            with open('src/model.py', 'r') as modelfile :
                model_file_data = modelfile.read()
            
            original_file_data = model_file_data
            model_file_data = model_file_data.replace('#model_info', filedata)
            
            with open('src/model.py', 'w') as modelfile:
                modelfile.write(model_file_data)

            importlib.reload(model)

            choices_list.append(model.GetStep())

            # Return model.py to orignal form
            with open('src/model.py', 'w') as modelfile:
                modelfile.write(original_file_data)

            importlib.reload(model)

        # Construct the path
        self.path = And(choices_list)
        return self.path


    def GetProperty(self):
        """Returns the property from the property_file_name found at the given path_length"""
        # Read in given property file
        with open(self.property_file, 'r') as propertyfile:
            filedata = propertyfile.read()

        # Replace the variable names
        filedata = filedata.replace('current_s', 's{0}'.format(self.path_length-1))
        filedata = filedata.replace('probability', 'p{0}'.format(self.path_length))
        filedata = filedata.replace('next_s', 's{0}'.format(self.path_length))
        filedata = filedata.replace('dice_value', 'dv{0}'.format(self.path_length))

        # Rewrite property.py GetProperty() with the new property
        with open('src/property.py', 'r') as propertyfile :
            property_file_data = propertyfile.read()
        
        original_file_data = property_file_data
        property_file_data = property_file_data.replace('#property_info', filedata)

        with open('src/property.py', 'w') as propertyfile:
            propertyfile.write(property_file_data)

        importlib.reload(property)

        property_retrieved = property.GetProperty()

        # Return property.py back to its original form
        with open('src/property.py', 'w') as modelfile:
            modelfile.write(original_file_data)

        importlib.reload(property)

        return property_retrieved

    def GetInitialStates(self):
        """Returns the initial state from init_states_file_name"""
        # Read in given initial states file
        with open(self.init_states_file, 'r') as initialfile:
            filedata = initialfile.read()

        # Replace the variable names
        filedata = filedata.replace('current_s', 's0')
        filedata = filedata.replace('probability', 'p1')
        filedata = filedata.replace('next_s', 's1')
        filedata = filedata.replace('dice_value', 'dv1')

        # Rewrite initial_states.py GetInitialStates() with the new initial states
        with open('src/initial_states.py', 'r') as initialfile:
            init_file_data = initialfile.read()

        original_file_data = init_file_data
        init_file_data = init_file_data.replace("#initial_state_info", filedata)

        with open('src/initial_states.py', 'w') as initialfile:
            initialfile.write(init_file_data)

        importlib.reload(initial_states)

        initial_state = initial_states.GetInitialStates()

        # Return initial_states.py back to its original form
        with open('src/initial_states.py', 'w') as initialfile:
            initialfile.write(original_file_data)

        importlib.reload(initial_states)

        return initial_state


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
                if declaration.name() == "p{0}".format(k):
                    pass
                else:
                    try:  # For Integer Values
                        cx_value_list.append(Int(declaration.name())==cx_model[declaration].as_long())
                    except:  # For Real Values
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
        initial_state = self.GetInitialStates()
        property = self.GetProperty()
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