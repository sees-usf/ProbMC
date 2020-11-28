# SMT-Based Bounded Model Checking
SMT-based bounded model checking attempts to find a counterexample for a given property within a specified path length and probability.
> Given a system of path length k, with s representing states, T representing transition relations, I representing the initial state, and P representing the property we aim to find a counterexample for, the logic forumula below can be used to find counterexamples.
>
> I(s0) ∧ T(s0, s1) ∧ T(s1, s2) ∧ · · · ∧ T(sk−1, sk) ∧ ¬(P(s1) ∧ · · · ∧ P(sk))
>
> If there exists a path that meets the above criteria, then a counterexample has been found.

## Setting Up
This project uses Python and Z3Prover. Find more information on Z3Prover here: https://github.com/Z3Prover/z3. 

To use the SMT-BMC solver, use the model_template.py to set up your model file. Instructions on how to add your model is  inside of that model_template.py file.  Once you have your own model file created and set up, move it into the src folder (SMT_BMC/src).  

Run the SMT-BMC solver by executing the main.py file in the src folder (SMT_BMC/src/main.py). The program will ask for three inputs, the name of your module file (excluding the .py), a path length, and the probability the counterexamples should reach to be considered a problem for the given path length. It will then return whether or not there exists a counterexample that meets or goes over the provided probability along with each step's reached probabilities and the total probability reached.

## Code Explained
The code relies on three files found in the src folder (SMT_BMC/src): main.py, BMC.py, and the module file the user selects when the program is first ran.
### main.py 
Asks the user for the name of the module containing their model, the path lengh to test for counterexamples, and the probability the counterexamples must meet or go over to be considered a problem. Returns whether or not there exists enough counterexamples to meet or go over the given probability.

This file imports BMC.py to build a BMC object through the user's input, which in turn runs the BMC class's initialization code. The initialization code prints out the all of its most recent steps it has searched through with their probability of finding a counterexample to the right of them. Once the program determines whether or not a counter example can be found, it will print out the answer as well as the total probability the program reached. Essentially, the BMC's initialization code runs the SMT-BMC solver and returns the solution. See BMC.py for more information. 
