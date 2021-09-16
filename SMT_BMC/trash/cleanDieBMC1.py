from z3 import *

def GetChoices(model_to_check, current_s, probability, dice_value, next_s):
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

    choices = And(ranges, Or(choice1, choice2, choice3, choice4, choice5, choice6, choice7, choice8))
    return choices


def GetProperties(next_s, dice_value):
    """Returns the negated property for one step"""
    return Not(And(next_s==7, dice_value==1))


def PathEncoding(path_length, model_to_check, initial_state, property, property_prob):
    """
    Returns an encoding of ALL paths with length up to path_length
    This path, starting from initial state and ending at the negation of the property 
    """
    total_prob = Real('total_prob')
    cur_s_list = []  # List of current states
    pv_list = []  # List of probability values
    dv_list = []  # List of dice values
    next_s_list = []  # List of next states
    choices_list = []  # List of choice constraints after a path_length amount of steps
    properties_list = []  # List of property constraints after a path_length amount of steps

    for k in range(path_length):
        # Build all necessary variables
        cur_s_list.append(Int("s{0}".format(k)))
        pv_list.append(Real("p{0}".format(k+1)))
        dv_list.append(Int("dv{0}".format(k+1)))
        next_s_list.append(Int("s{0}".format(k+1)))
        if k == 0:
            initial_state = And(cur_s_list[k]==0, dv_list[k]==0)
        # Obtain choices/properties by using their corresponding variables (s0..k, pv1..k+1, dv1..k+1, s1..k+1)
        choices_list.append(GetChoices(model_to_check, cur_s_list[k], pv_list[k], dv_list[k], next_s_list[k]))
        properties_list.append(GetProperties(next_s_list[k], dv_list[k]))

        # Set the new current state to point to the old next state
        cur_s_list[k] = next_s_list[k]

        # Construct the path
        choices_list.insert(0, initial_state)  # Start with initial state
        choices = And(choices_list)  # Middle = choices
        properties = Or(properties_list)  # End with negated property
        path = And(choices, properties)  # I(s0) & T(s0) & T(s1..k) & (!P(s0) || !P(s1..k))

        # Plug in the initial state & negation in BMC
        # Then send to Z3 Solver

        print(path)
        #  If given a probability to test...
        if property_prob != None:
            prob_success = False
            prob_solver = Solver()
            current_prob = BMC(path, path_length)
            prob_solver.add(property_prob, total_prob==current_prob)
            if str(prob_solver.check()) == "sat":
                return
        else:
            prob_success = True
            probability = 0
            probability = BMC(path, path_length)
            if probability != 0:
                return

    # print(path)
    if prob_success == False:
        print("Failed to meet probability.")
    return path

    """
    ~~~Testing memory addresses~~~
    print(path)
    print("current")
    for item in cur_s_list:
        print(id(item))

    print("next")
    for item in next_s_list:
        print(id(item))
    """
        

def BMC(path, path_length):
    """Finds a counter-example model given a path and returns the probability of the path occuring"""
    # Adds initial state and negation of property to here
    # Properties file + Model file (+ bounds) inserted 
    # BMC is the one that loops, Path Encoding takes path_length and returns path ( + bounds)
    # Try logarithmic addition of probabilities
    # Log all? the transition propabilites -> Add them -> Exponent stuff
    # Returns "Yes" when a counter example can be met given a probability
    # Returns "No" when a counter example cannot be met given a probability


    solver = Solver()
    solver.add(path)
    solver.check()
    try:  # Counter Example Found
        print(solver.model())
        model = solver.model()

        # Find probability by multiplying all of the step's probabilities together
        probability = 1
        for k in range(path_length+1):
            for d in model.decls():
                if d.name() == "p{0}".format(k):
                    numerator = float(model[d].numerator_as_long())
                    denominator = float(model[d].denominator_as_long())
                    probability *= numerator/denominator
        print(probability)
        return probability
    except:  # Counter Example Not Found
        print("The property has no counter-examples meeting the given requirements.")
        return 0  # Probability of the property occuring with the given constraints = 0


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
path_length = 2
initial_state = And(current_s==0, dice_value==0)  # Not used
property = And(next_s==7, dice_value==1)  # Not used
property_prob = And(total_prob > 0.2, total_prob < 0.5) # Find a counter example that has this probability of occuring

PathEncoding(path_length, model_to_check, initial_state, property, property_prob)

