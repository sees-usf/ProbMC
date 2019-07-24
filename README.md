# Stochastic Verification and Probabilitic Model Checking

The goal of this research is to develop scalable and efficient methods and algorithms for verification and analysis of systems created using emerging technologies such as synthetic biology.  These systems are often created with unreliable components, and operate in noisy environment. Therefore, the behavior of these systems is typically stochastic, and they often have large or even infinite state space. An example of such systems can be found in computer networks with uncertain transmission delays or packet drop rates over an unreliable network medum, incoming traffice flows from a direction of an intersection, etc.

Stochastic verificaiton technique, *probabilistic model checking* in particular, have demonstrated significant potential in quantitatively analyzing such systems.  A typical goal of stochastic verification is to decide probability of a property to hold on a model, *e.g.* the probability of a packet arriving at the destiantion within 10 time untis is 99%. Unfortunately, they are generally computationally intractable for large and complex systems due to state space explosion or infinite state space. This project will address that challenge by developing an automated stochastic verification framework that integrates an approximate stochastic model checking approach and counterexample-guided rare-event simulation to improve the analysis accuracy and efficiency.

The papers below summarizes our recent work from this project.

* *STAMINA: STochastic ApproximateModel-checker for INfinite-state Analysis*, Thakur Neupane, Chris J. Myers,Curtis Madsen, Hao Zheng, Zhen Zhang, CAV'19, July, 2019, to appear.

* *Approximation Techniques for Stochastic Analysis of Biological Systems*, Thakur Neupane, Zhen Zhang, Curtis Madsen, Hao Zheng, Chris J. Myers, a book chapter in Automated Reasoning for Systems Biology and Medicine. Springer International Publishing, 2019
