import random
import numpy as np
import math
from tqdm import tqdm
import model

np.random.seed(1234)
random.seed(1234)

# number of simulation runs
n = 10000

###########################################
# you can define the property here. The property would consist of
# a time bound and a set of accepting states

# time bound in the property
t = 100

# set of accepting states
accepting_states = [1]
###########################################


###########################################
# you can either define the model as a list of states, a list of transitions (each in the format
# of a list [source, destination, rate]) and an initial state here and pass them as arguments with
# that order to model constructor or change those in model.py
m1 = model.model()
# print(m1.r_matrix)

###########################################
# you can define the weight assigned to each transition
# here. For example [0, 1, 2] means that transition from state 0
# to state 1 has a assigned weight of 2 (meaning double of its rate would be
# considered in next state selection). The default weight would be 1 if no weight
# is assigned

weights = [[0, 1, 10], [0, 2, 1]]
w_matrix = list()

for i in range(len(m1.states)):
    temp = list()
    for j in range(len(m1.states)):
        temp.append(1)
    w_matrix.append(temp)
for e in weights:
    s = e[0]
    d = e[1]
    w = e[2]
    w_matrix[s][d] = w

# print(w_matrix)

###########################################

count = 0
progress = np.zeros(n)

for i in tqdm(range(n)):
    flag = False
    done = False
    time = 0
    w = 1
    state = m1.initial_state

    a = np.array(m1.r_matrix[state])
    a0 = a.sum()

    b = np.array(m1.r_matrix[state]) * np.array(w_matrix[state])
    b0 = b.sum()

    while time <= t:
        if flag == True:
            count = count + w
            break
        if done == True:
            break
        r1 = random.uniform(0, 1)
        r2 = random.uniform(0, b0)

        tau = float(1.0 / float(a0)) * math.log(float(1.0 / r1))
        j = 0
        temp_sum = 0

        while temp_sum <= r2:
            temp_sum = temp_sum + b[j]
            j = (j + 1)

        j = j - 1
        w = w * (a[j] / b[j]) * (b0 / a0)
        time = time + tau


        state = j
        a = np.array(m1.r_matrix[state])
        a0 = a.sum()

        b = np.array(m1.r_matrix[state]) * np.array(w_matrix[state])
        b0 = b.sum()

        if j in accepting_states:
            flag = True
            done = True
        elif a0 == 0:
            done = True



    progress[i] = float(count / float(i + 1))

npprogress = np.array(progress)

print("probability: " + str(float(count / float(n))))

print("=" * 30)
