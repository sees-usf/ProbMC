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
    path = And(choices_list)
    # print(path)
    return path
        

def GetProperty(step_number, property_file_name):
    """Returns the property from the property_file_name found at the given step_number"""
    # Read in given property file
    with open(property_file_name, 'r') as propertyfile:
        filedata = propertyfile.read()

    # Replace the variable names
    filedata = filedata.replace('current_s', 's{0}'.format(step_number))
    filedata = filedata.replace('probability', 'p{0}'.format(step_number+1))
    filedata = filedata.replace('next_s', 's{0}'.format(step_number+1))
    filedata = filedata.replace('dice_value', 'dv{0}'.format(step_number+1))
    # Rewrite property.py GetProperty() with new property
    with open('src/property.py', 'r') as propertyfile :
        property_file_data = propertyfile.read()
    
    original_file_data = property_file_data
    property_file_data = property_file_data.replace('#property_info', filedata)

    with open('src/property.py', 'w') as propertyfile:
        propertyfile.write(property_file_data)

    importlib.reload(property)

    property_retrieved = property.GetProperty()

    # Return property.py to its original form
    with open('src/property.py', 'w') as modelfile:
        modelfile.write(original_file_data)

    importlib.reload(property)

    return property_retrieved


def GetInitialStates(init_states_file_name):
    """Returns the initial state from init_states_file_name"""
    # Read in given initial states file
    with open(init_states_file_name, 'r') as initialfile:
        filedata = initialfile.read()

    # Replace the variable names
    filedata = filedata.replace('current_s', 's0')
    filedata = filedata.replace('probability', 'p1')
    filedata = filedata.replace('next_s', 's1')
    filedata = filedata.replace('dice_value', 'dv1')

    # Rewrite initial_states.py GetInitialStates() with new initial states
    with open('src/initial_states.py', 'r') as initialfile:
        init_file_data = initialfile.read()

    original_file_data = init_file_data
    init_file_data = init_file_data.replace("#initial_state_info", filedata)

    with open('src/initial_states.py', 'w') as initialfile:
        initialfile.write(init_file_data)

    importlib.reload(initial_states)

    initial_state = initial_states.GetInitialStates()

    # Return initial_states.py to original form
    with open('src/initial_states.py', 'w') as initialfile:
        initialfile.write(original_file_data)

    importlib.reload(initial_states)

    return initial_state


reached_cx_list = []
def ExcludePath(cx_model, path_length):
    """
    Returns all found counterexamples to avoid recounting. The constraints are already complemented, so there 
    is no need to add NOT() before using AND() to merge the original model to the returned constraints.
    """
    global reached_cx_list
    # Creating a list of each variable's value to later merge together into the full counterexample
    for k in range(path_length+1):
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
    
    # Merging the counterexample values into a full counterexample, 
    # adding them to a list of the total counterexamples, 
    # and using AND() to merge all the counterexamples together.
    cx = Not(And(cx_value_list))  # Counterexample to begin avoiding
    reached_cx_list.append(cx)  # Add to list of counterexamples to avoid
    all_cx_constraints = And(reached_cx_list)  # All of the counterexamples to avoid
    return all_cx_constraints


def BMC(path, path_length, property_file, init_states_file):
    """Finds a counterexample model given a path and returns the probability of the path occuring"""
    global reached_cx_list  # Global list of cx's already reached, including prior steps.

    # Build the Bounded Model
    properties_list = []  # List of property constraints after a path_length amount of steps
    for k in range(path_length):
        # Get the initial state and all of the properties for all k's up to path_length
        if k == 0:
            initial_state = GetInitialStates(init_states_file)
        properties_list.append(GetProperty(k, property_file))
        # Construct the bounded model
        properties = Or(properties_list)
        bounded_model = And(initial_state, path, properties)

    # print(bounded_model)
    # Checking the Bounded Model
    solver = Solver()
    if reached_cx_list:
        past_path_constraints = And(reached_cx_list)
        new_bounded_model = And(bounded_model, past_path_constraints)
        solver.add(new_bounded_model)
    else:
        solver.add(bounded_model)
    total_probability = 0
    try:   # Only ran if a counterexample was found
        while(True):  # Calculate the probability of all counterexamples that can be generated
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
            
            all_cx_constraints = ExcludePath(cx_model, path_length)
            new_bounded_model = And(bounded_model, all_cx_constraints)  
            # print("New Model:")
            # simplify(new_bounded_model)
            # print(new_bounded_model)
            solver.reset()
            solver.add(new_bounded_model)
            # print(probability)
            total_probability += probability  # Adding the probability of each cx at the given pathlength
    except:
        # No more counterexamples can be found
        return total_probability


# Obtain information about the model to check and what to check for.
model_file_name = property_file_name = init_states_file_name = "benchmark/"
model_file_name += input("Model file name (include .txt): ")
property_file_name += input("Property file name (include .txt): ")
init_states_file_name += input("Initial states file name (include .txt): ")
path_length = int(input("Provide a path length: "))

# Get the property probability threshold
with open(property_file_name, 'r') as property_file:
    last_line = property_file.readlines()[-1]
property_prob = (float(last_line.split("=", 1)[1]))

# Increase the path_length until a counterexample that meets the probability is found
# or until the designated path_length is reached
total_probability = 0
for i in range(1, (path_length+1)):
    prob_from_step = BMC(PathEncoding(i, model_file_name), i, property_file_name, init_states_file_name)
    print(i, prob_from_step)
    total_probability += prob_from_step
    if total_probability >= property_prob:
        print("Yes, a counterexample was found at a probability greater than {}.".format(property_prob))
        break
if total_probability < property_prob:
    print("No, a counterexample was not found at a probability greater than {}.".format(property_prob))

print("Total probability: {}".format(total_probability))
