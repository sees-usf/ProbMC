from z3 import *
import model, property, initial_states
import importlib

# s0, s1, d1, p1 = 1 constraint k = 1 (1 step)
# s0, s1, d1, p1, s1, s2, d2, p2 = 2nd Z3 encoding k =2 (2 steps)
# s0, s1, d1, p1, s1, s2, d2, p2, s2, s3 etc. = 3rd Z3 Encoding


def PathEncoding(path_length): # transition relation of model too - return Z3 encoding
    """
    Returns an encoding of ALL paths with length up to path_length
    This path, starting from initial state and ending at the negation of the property 
    """
    cur_s_list = []  # List of current states
    pv_list = []  # List of probability values
    dv_list = []  # List of dice values
    next_s_list = []  # List of next states
    choices_list = []  # List of choice constraints after a path_length amount of steps
    for k in range(path_length):
        # Read in the file
        with open('model.py', 'r') as modelfile :
          filedata = modelfile.read()

        # Replace the variable names
        filedata = filedata.replace('current_s', 's{0}'.format(k))
        filedata = filedata.replace('probability', 'p{0}'.format(k+1))
        filedata = filedata.replace('next_s', 's{0}'.format(k+1))
        filedata = filedata.replace('dice_value', 'dv{0}'.format(k+1))

        # Rewrite model
        with open('model.py', 'w') as modelfile:
          modelfile.write(filedata)

        importlib.reload(model)

        choices_list.append(model.GetStep())

        # Put file back together - Just realized this could be done easier 
        # (save original read in a variable and write that variable into file instead of re-replacing stuff)
        # Read in the file
        with open('model.py', 'r') as modelfile :
          filedata = modelfile.read()

        # Replace the variable names
        filedata = filedata.replace('s{0}'.format(k), 'current_s')
        filedata = filedata.replace('p{0}'.format(k+1), 'probability')
        filedata = filedata.replace('s{0}'.format(k+1), 'next_s')
        filedata = filedata.replace('dv{0}'.format(k+1), 'dice_value')

        # Rewrite model
        with open('model.py', 'w') as modelfile:
          modelfile.write(filedata)

        importlib.reload(model)

    # Construct the path
    path = And(choices_list)
    # print(path)
    return path
        

def BMC(path, path_length, property_prob):
    """Finds a counter-example model given a path and returns the probability of the path occuring"""
    cur_s_list = []  # List of current states
    pv_list = []  # List of probability values
    dv_list = []  # List of dice values
    next_s_list = []  # List of next states
    properties_list = []  # List of property constraints after a path_length amount of steps

    for k in range(path_length):
        """ Property """
        # Read in the file
        with open('property.py', 'r') as propertyfile:
          filedata = propertyfile.read()

        # Replace the variable names
        filedata = filedata.replace('current_s', 's{0}'.format(k))
        filedata = filedata.replace('probability', 'p{0}'.format(k+1))
        filedata = filedata.replace('next_s', 's{0}'.format(k+1))
        filedata = filedata.replace('dice_value', 'dv{0}'.format(k+1))

        # Rewrite file
        with open('property.py', 'w') as propertyfile:
          propertyfile.write(filedata)

        importlib.reload(property)

        properties_list.append(property.GetProperty()[0])

        # Put file back together
        # Read in the file
        with open('property.py', 'r') as propertyfile:
          filedata = propertyfile.read()

        # Replace the variable names
        filedata = filedata.replace('s{0}'.format(k), 'current_s')
        filedata = filedata.replace('p{0}'.format(k+1), 'probability')
        filedata = filedata.replace('s{0}'.format(k+1), 'next_s')
        filedata = filedata.replace('dv{0}'.format(k+1), 'dice_value')

        # Rewrite file
        with open('property.py', 'w') as propertyfile:
          propertyfile.write(filedata)

        importlib.reload(property)

        """ Initial State """
        if k == 0:

            # Read in the file
            with open('initial_states.py', 'r') as initialfile:
              filedata = initialfile.read()

            # Replace the variable names
            filedata = filedata.replace('current_s', 's0')
            filedata = filedata.replace('probability', 'p1')
            filedata = filedata.replace('next_s', 's1')
            filedata = filedata.replace('dice_value', 'dv1')

            # Rewrite file
            with open('initial_states.py', 'w') as initialfile:
              initialfile.write(filedata)

            importlib.reload(initial_states)

            initial_state = initial_states.GetInitialStates()

            # Put file back together
            # Read in the file
            with open('initial_states.py', 'r') as initialfile:
              filedata = initialfile.read()

            # Replace the variable names
            filedata = filedata.replace('s0', 'current_s')
            filedata = filedata.replace('p1', 'probability')
            filedata = filedata.replace('s1', 'next_s')
            filedata = filedata.replace('dv1', 'dice_value')

            # Rewrite file
            with open('initial_states.py', 'w') as initialfile:
              initialfile.write(filedata)

            importlib.reload(initial_states)

        # Construct the bounded model
        properties = Or(properties_list)
        bounded_model = And(initial_state, path, properties)

    # print(bounded_model)
    # Checking the Bounded Model
    solver = Solver()
    solver.add(bounded_model)
    total_probability = 0
    step_count = 0
    try:  # Only ran if a counter-example was found
        new_model_list = []
        while(total_probability < property_prob):
            solver.check()
            # print(solver.model())
            cx_model = solver.model()

            # Find probability by multiplying all of the step's probabilities together
            probability = 1
            for k in range(path_length+1):
                for d in cx_model.decls():
                    if d.name() == "p{0}".format(k):
                        numerator = float(cx_model[d].numerator_as_long())
                        denominator = float(cx_model[d].denominator_as_long())
                        probability *= numerator/denominator

            # Preventing a step from being counted multiple times
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

            new_model = Not(And(cx_value_list))  # Create a list of new models to add to bounded_model each time.
            new_model_list.append(new_model)
            all_new_model_constraints = And(new_model_list)
            new_bounded_model = And(bounded_model, all_new_model_constraints)
            print("New Model:")
            # simplify(new_bounded_model)
            print(new_bounded_model)
            solver.reset()
            solver.add(new_bounded_model)
            # print(probability)
            total_probability += probability
            print(total_probability)
            step_count += 1
            if total_probability >= 1:  # this is never met, get rid of?
                print("Invalid probability achieved")
                break
        if total_probability >= property_prob:
            print("Yes, a counter example was found at a probability greather than {}.".format(property_prob))
    except:
        print("No, a counter example was not found at a probability greater than {}.".format(property_prob))
    


# Load model to get the transition relation (steps), then pass these steps to the bmc
# From BMC, do model checking for all paths up to that path length
# Should be able to handle arbitrary models - Model info should be passed as a parameter
# BMC is iterative- takes k and it iterates over all possible values of k, start at k = 1
# Main function should iterate over 1 all the way to k, then passes # to BMC
# Handles all of the paths at exactly that particular length

# Harmon.py

path_length = 4
property_prob = property.GetProperty()[1]
for i in range(1,4):  # For 1 to 3
  # Replace path length with i 
  # path_length = i
  total_probability = 0 # Returned by BMC
  # Compare property_prob here

BMC(PathEncoding(path_length), path_length, property_prob)

