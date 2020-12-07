from z3 import *


def GetStep(model_to_check, current_s, probability, dice_value, next_s):
    """Returns the choices for one step"""
    ranges = And(And(0 <= current_s, current_s <= 7), And(0 <= next_s, next_s <= 7), And(0 <= dice_value, dice_value <= 6), And(0 <= probability, probability <= 1))
    choice1 = And(current_s==0, (Or(And(probability==0.5, dice_value==0, next_s==1), And(probability==0.5, dice_value==0, next_s==2))))
    choice2 = And(current_s==1, (Or(And(probability==0.5, dice_value==0, next_s==3), And(probability==0.5, dice_value==0, next_s==4))))
    choice3 = And(current_s==2, (Or(And(probability==0.5, dice_value==0, next_s==5), And(probability==0.5, dice_value==0, next_s==6))))
    choice4 = And(current_s==3, (Or(And(probability==0.5, dice_value==0, next_s==1), And(probability==0.5, dice_value==1, next_s==7))))
    choice5 = And(current_s==4, (Or(And(probability==0.5, dice_value==2, next_s==7), And(probability==0.5, dice_value==3, next_s==7))))
    choice6 = And(current_s==5, (Or(And(probability==0.5, dice_value==4, next_s==7), And(probability==0.5, dice_value==5, next_s==7))))
    choice7 = And(current_s==6, (Or(And(probability==0.5, next_s==2), And(probability==0.5, dice_value==6, next_s==7))))
    choice8 = And(current_s==7, next_s==7)

    step = And(ranges, Or(choice1, choice2, choice3, choice4, choice5, choice6, choice7, choice8))
    return step

def PathEncoding(path_length, model_to_check):
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
        # Build all necessary variables
        cur_s_list.append(Int("s{0}".format(k)))
        pv_list.append(Real("p{0}".format(k+1)))
        dv_list.append(Int("dv{0}".format(k+1))) # Change to final value... This could actually be multiple variables
        next_s_list.append(Int("s{0}".format(k+1)))
        # Obtain path at current state by using its corresponding variables (s0..k, pv1..k+1, dv1..k+1, s1..k+1)
        choices_list.append(GetStep(model_to_check, cur_s_list[k], pv_list[k], dv_list[k], next_s_list[k]))
        # Set the new current state to point to the current step's next state
        cur_s_list[k] = next_s_list[k]

    # Construct the path
    path = And(choices_list)
    # print(path)
    return path
        

def BMC(path, path_length, property_prob):
    """Finds a counter-example model given a path and returns the probability of the path occuring"""
    # Adds initial state and negation of property to here
    # Properties file + Model file (+ bounds) inserted 
    # BMC is the one that loops, Path Encoding takes path_length and returns path ( + bounds)
    # Try logarithmic addition of probabilities
    # Log all? the transition propabilites -> Add them -> Exponent stuff
    # Returns "Yes" when a counter example can be met given a probability
    # Returns "No" when a counter example cannot be met given a probability

    total_prob = Real('total_prob')
    cur_s_list = []  # List of current states
    pv_list = []  # List of probability values
    dv_list = []  # List of dice values
    next_s_list = []  # List of next states
    properties_list = []  # List of property constraints after a path_length amount of steps

    for k in range(path_length):
        # Build all necessary variables
        cur_s_list.append(Int("s{0}".format(k)))
        pv_list.append(Real("p{0}".format(k+1)))
        dv_list.append(Int("dv{0}".format(k+1)))
        next_s_list.append(Int("s{0}".format(k+1)))
        if k == 0:
            initial_state = And(cur_s_list[k]==0, dv_list[k]==0)
        # Obtain property by using its corresponding variables (s0..k, pv1..k+1, dv1..k+1, s1..k+1)
        properties_list.append(Not(Not(And(next_s_list[k]==7, dv_list[k]==1))))
        # Set the new current state to point to the current step's next state
        cur_s_list[k] = next_s_list[k]

        # Construct the bounded model
        properties = Or(properties_list)
        bounded_model = And(initial_state, path, properties)

    # print(bounded_model)
    # Plug in the initial state & negation in BMC
    # Then send to Z3 Solver
    solver = Solver()
    solver.add(bounded_model)
    total_probability = 0
    step_count = 0
    try:
        new_model_list = []
        while(total_probability < property_prob):
            solver.check()
            # try:  # Counter Example Found
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
            if total_probability >= 1:
                print("Invalid probability given")
                break
        if total_probability < property_prob:
            print("Failed to find a probability >= to the given probability")
        else:
            print(total_probability)
    except:
        print("No counter example found")


current_s = Int("current_s")
probability = Real('probability')
dice_value = Int("dice_value")
next_s = Int("next_s")
total_prob = Real('total_prob')

ranges = And(And(0 <= current_s, current_s <= 7), And(0 <= next_s, next_s <= 7), And(0 <= dice_value, dice_value <= 6), And(0 <= probability, probability <= 1))
choice1 = And(current_s==0, (Or(And(probability==0.5, dice_value==0, next_s==1), And(probability==0.5, dice_value==0, next_s==2))))
choice2 = And(current_s==1, (Or(And(probability==0.5, dice_value==0, next_s==3), And(probability==0.5, dice_value==0, next_s==4))))
choice3 = And(current_s==2, (Or(And(probability==0.5, dice_value==0, next_s==5), And(probability==0.5, dice_value==0, next_s==6))))
choice4 = And(current_s==3, (Or(And(probability==0.5, dice_value==0, next_s==1), And(probability==0.5, dice_value==1, next_s==7))))
choice5 = And(current_s==4, (Or(And(probability==0.5, dice_value==2, next_s==7), And(probability==0.5, dice_value==3, next_s==7))))
choice6 = And(current_s==5, (Or(And(probability==0.5, dice_value==4, next_s==7), And(probability==0.5, dice_value==5, next_s==7))))
choice7 = And(current_s==6, (Or(And(probability==0.5, next_s==2), And(probability==0.5, dice_value==6, next_s==7))))
choice8 = And(current_s==7, next_s==7)

model_to_check = [ranges, choice1, choice2, choice3, choice4, choice5, choice6, choice7, choice8] # Not used
path_length = 3
property_prob = 1
# PathEncoding(path_length, model_to_check)
BMC(PathEncoding(path_length, model_to_check), path_length, property_prob)

