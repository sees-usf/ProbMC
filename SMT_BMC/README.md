# SMT-Based Bounded Model Checking
SMT-based bounded model checking attempts to find a counterexample for a given property within a specified path length and probability.
> Given a system of path length k, with s representing states, T representing transition relations, I representing the initial state, and P representing the property we aim to find a counterexample for, the logic forumula below can be used to find counterexamples.
> 
> I(s0) ∧ T(s0, s1) ∧ T(s1, s2) ∧ · · · ∧ T(sk−1, sk) ∧ ¬(P(s1) ∧ · · · ∧ P(sk))
>
> If there exists a path that meets the above criteria, then a counterexample has been found.

## Setting Up
This project uses Python and Z3Prover. Find more information about Z3Prover here: https://github.com/Z3Prover/z3. To use the SMT-BMC solver, set up your model file, property file, and initial state file in the SMT_BMC/benchmark folder.  Follow the formatting of the die.txt, die_property.txt, and die_init.txt to see how the files should be created.  Once your files are set up, make sure you are in the SMT_BMC directory, and run BMC.py, found in the SMT_BMC/src folder. This file will ask for the name of the files placed within the benchmark folder, and return whether or not there exists a counterexample that meets or goes over the specified probability provided in the property file.