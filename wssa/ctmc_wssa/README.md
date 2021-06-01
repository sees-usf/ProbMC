# stochastic simulator utilizing importance sampling on simple CTMCs

to define a new model you can do so by changing the model.py script.

Each model is defined as a set of states, set of transitions and an initial state. States are represented with integers starting from 0. So if a 4 state model is desired set of states would be [0, 1, 2, 3] and if a 5 state model is desired set of states would be [0, 1, 2, 3, 4].You can change it in line 10 of model.py.

Each transitions is defined as a list with the following format: [source, destination, rate]. So the transitions is a list of lists, each following the mentioned format. You can add transitions by changing line 14 of model.py. 

Initial state is an integer indicating the initial state of the model. You can set it in line 18 of model.py. 

For example, the current model is defining a ctmc with 3 states: 0, 1, 2. There is a transition from state 0 to state 1 with rate 1. There is a transition from state 0 to state 2 with rate 999 and there is a transitons from state 2 to state 0 with rate 5.

The simulator is set to find the probabilities of form P=?[true U<t X]. to change time bound t you can change line 18 of ctmc_wssa.py. X is the set of accepting (error, target or other terminology) states. You can change that in line 21. For example for the current model I have set state 1 as the accepting state.

By default, all the weights assigned to transitions are 1 (so if no weights are defined the simulator works as a stochastic simulator without importance sampling). If you want to assign weights to any of the transitions, you can do so by changing line 39 of ctmc_wssa.py. weights is a list of lists where each list is of the form [source, destination, weight] so that you can define a weight assigned to any transition. If no weight is assinged, it is considered to be 1. For example in the current model weight 10 is assigned to transition from state 0 to state 1 (and weight 1 is assigned to transition from state 0 to 2 which has no effect and can be deleted). 

By running the ctmc_wssa.py script the probablity of property of interest on the model is printed by the end.