"""
SMT Bounded Model Checker

Asks the user for a model file, property file, and initial state file.
Returns whether or not there exists enough counterexamples to meet,
or go over, a specified probability.

Please refer to the following for examples on how the files should be built:
    Model File: die.txt
    Property File: die_property.txt
    Initial State File: die_init.txt

Note: If a file cannot be found, ensure the program is being ran in the correct directory.
"""

from z3 import *
import model, property, initial_states
import importlib

def PathEncoding(path_length, model_file_name):
    """
    Returns an encoding of ALL paths with length up to path_length
    This path, starting from initial state and ending at the negation of the property 
    """
    choices_list = []  # List of choice constraints after a path_length amount of steps
    for k in range(path_length):
        # Read in given model file
        with open(model_file_name, 'r') as modelfile :
            filedata = modelfile.read()

        # Replace the variable names
        filedata = filedata.replace('current_x1', 'current_x1_{0}'.format(k))
        filedata = filedata.replace('current_x2', 'current_x2_{0}'.format(k))
        filedata = filedata.replace('current_x3', 'current_x3_{0}'.format(k))
        filedata = filedata.replace('probability', 'p{0}'.format(k+1))
        filedata = filedata.replace('next_x1', 'next_x1_{0}'.format(k+1))
        filedata = filedata.replace('next_x2', 'next_x2_{0}'.format(k+1))
        filedata = filedata.replace('next_x3', 'next_x3_{0}'.format(k+1))

        # Rewrite model.py GetStep() with new model
        with open('model.py', 'r') as modelfile :
            model_file_data = modelfile.read()
        
        original_file_data = model_file_data
        model_file_data = model_file_data.replace('#model_info', filedata)
        
        with open('model.py', 'w') as modelfile:
            modelfile.write(model_file_data)

        importlib.reload(model)

        choices_list.append(model.GetStep())

        # Return model.py to orignal form
        with open('model.py', 'w') as modelfile:
            modelfile.write(original_file_data)

        importlib.reload(model)

    # Construct the path
    path = And(choices_list)
    # print(path)
    return path
        
new_model_list = []
def BMC(path, path_length):
    """Finds a counter-example model given a path and returns the probability of the path occuring"""
    
    properties_list = []  # List of property constraints after a path_length amount of steps
    global new_model_list  # Global list of cx's already reached, including prior steps.

    for k in range(path_length):
        """ Property """
        # Read in given property file
        with open(property_file_name, 'r') as propertyfile:
            filedata = propertyfile.read()

        # Replace the variable names
        filedata = filedata.replace('current_x1', 'current_x1_{0}'.format(k))
        filedata = filedata.replace('current_x2', 'current_x2_{0}'.format(k))
        filedata = filedata.replace('current_x3', 'current_x3_{0}'.format(k))
        filedata = filedata.replace('probability', 'p{0}'.format(k+1))
        filedata = filedata.replace('next_x1', 'next_x1_{0}'.format(k+1))
        filedata = filedata.replace('next_x2', 'next_x2_{0}'.format(k+1))
        filedata = filedata.replace('next_x3', 'next_x3_{0}'.format(k+1))

        # Rewrite property.py GetProperty() with new property
        with open('property.py', 'r') as propertyfile :
            property_file_data = propertyfile.read()
        
        original_file_data = property_file_data
        property_file_data = property_file_data.replace('#property_info', filedata)

        with open('property.py', 'w') as propertyfile:
            propertyfile.write(property_file_data)

        importlib.reload(property)

        properties_list.append(property.GetProperty())

        # Return property.py to its original form
        with open('property.py', 'w') as modelfile:
            modelfile.write(original_file_data)

        importlib.reload(property)

        """ Initial State """
        if k == 0:

            # Read in given initial states file
            with open(init_states_file_name, 'r') as initialfile:
                filedata = initialfile.read()

            # Replace the variable names
            filedata = filedata.replace('current_x1', 'current_x1_0')
            filedata = filedata.replace('current_x2', 'current_x2_0')
            filedata = filedata.replace('current_x3', 'current_x3_0')
            filedata = filedata.replace('probability', 'p1')
            filedata = filedata.replace('next_x1', 'next_x1_1')
            filedata = filedata.replace('next_x2', 'next_x2_1')
            filedata = filedata.replace('next_x3', 'next_x3_1')

            # Rewrite initial_states.py GetInitialStates() with new initial states
            with open('initial_states.py', 'r') as initialfile:
                init_file_data = initialfile.read()

            original_file_data = init_file_data
            init_file_data = init_file_data.replace("#initial_state_info", filedata)

            with open('initial_states.py', 'w') as initialfile:
                initialfile.write(init_file_data)

            importlib.reload(initial_states)

            initial_state = initial_states.GetInitialStates()

            # Return initial_states.py to original form
            with open('initial_states.py', 'w') as initialfile:
                initialfile.write(original_file_data)

            importlib.reload(initial_states)

        # Construct the bounded model
        properties = Or(properties_list)
        bounded_model = And(initial_state, path, properties)

    print(bounded_model)
    # Checking the Bounded Model
    solver = Solver()
    if new_model_list:
        past_path_constraints = And(new_model_list)
        new_bounded_model = And(bounded_model, past_path_constraints)
        solver.add(new_bounded_model)
    else:
        solver.add(bounded_model)
    total_probability = 0
    print(solver.check())
    try:  # Only ran if a counter-example was found
        while(True):  # Calculate the probability of all counter examples that can be generated
            solver.check()
            # print(solver.model())
            cx_model = solver.model()

            # Find probability of the counterexample by multiplying all of the step's probabilities together
            probability = 1
            for k in range(path_length+1):
                for d in cx_model.decls():
                    if d.name() == "p{0}".format(k):
                        numerator = float(cx_model[d].numerator_as_long())
                        denominator = float(cx_model[d].denominator_as_long())
                        probability *= numerator/denominator

            # Preventing a counterexample from being counted multiple times
            for k in range(path_length+1):
                cx_value_list = []
                for d in cx_model.decls():
                    # print ("%s = %s" % (d.name(), cx_model[d]))
                    if d.name() == "p{0}".format(k):
                        pass
                    else:
                        try:  # For Integer Values
                            cx_value_list.append(Int(d.name())==cx_model[d].as_long())
                        except:  # For Real Values
                            numerator = float(cx_model[d].numerator_as_long())
                            denominator = float(cx_model[d].denominator_as_long())
                            p_value = numerator/denominator
                            cx_value_list.append(Real(d.name())==p_value)

            # Adding the constraints to prevent the bounded model from using the same cx twice.
            new_model = Not(And(cx_value_list))
            new_model_list.append(new_model)
            all_new_model_constraints = And(new_model_list)
            new_bounded_model = And(bounded_model, all_new_model_constraints)
            # print("New Model:")
            # simplify(new_bounded_model)
            # print(new_bounded_model)
            solver.reset()
            solver.add(new_bounded_model)
            # print(probability)
            total_probability += probability  # Adding the probability of each cx at the given pathlength
    except:
        # No more counter examples can be found
        return total_probability


# Obtain information about the model to check and what to check for.
model_file_name = input("Model file name (include .txt): ")
property_file_name = input("Property file name (include .txt): ")
init_states_file_name = input("Initial states file name (include .txt): ")

with open(property_file_name, 'r') as property_file:
    last_line = property_file.readlines()[-1]

property_prob = (float(last_line.split("=", 1)[1]))

path_length = int(input("Provide a path length: "))

# Increase the path_length until a counter example that meets the probability is found
# or until the designated path_length is reached
total_probability = 0
for i in range(1, (path_length+1)):
    prob_from_step = BMC(PathEncoding(i, model_file_name), i)
    print(i, prob_from_step)
    total_probability += prob_from_step
    if total_probability >= property_prob:
        print("Yes, a counter example was found at a probability greater than {}.".format(property_prob))
        break
if total_probability < property_prob:
    print("No, a counter example was not found at a probability greater than {}.".format(property_prob))

print(total_probability)
