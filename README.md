# Probabilitic Model Checking

Probablistic model checking is a formal approach to verify properties of models of systems with uncertainties.  Systems with uncertainties can be found in various situations including transmission delays or packet drop rates over an unreliable network, incoming traffice flows from a direction of an intersection, etc.  A typical goal of probablistic model checking is to decide probability of a property to hold on a model, *e.g.* the probability of a packet arriving at the destiantion within 10 time untis is 99%.

Similar to other model checking approaches, probablistic model checking also suffers the *state space explision* problem.  That problem is exacerbated as probablisitc models often have infinite state space, therefore, techniques developed for finite state model checking are not effective in this case.  In this project, we aim to develop methods to address that challenges.  One particular direction explored is approxmiate state space search based on the main idea that the state space search stops when a property can be proved or disproved.  The papers below summarizes the initial work.

* *STAMINA: STochastic ApproximateModel-checker for INfinite-state Analysis*, Thakur Neupane, Chris J. Myers,Curtis Madsen, Hao Zheng, Zhen Zhang, CAV'19, July, 2019, to appear.

* *Approximation Techniques for Stochastic Analysis of Biological Systems*, Thakur Neupane, Zhen Zhang, Curtis Madsen, Hao Zheng, Chris J. Myers, a book chapter in Automated Reasoning for Systems Biology and Medicine. Springer International Publishing, 2019
