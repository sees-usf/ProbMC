""" srn-6s ("syntheic biology model")
    variables: 
    R1, R2, R3, R4, R5, R6: int
    s1, s1_next, s2, s2_next, s3, s3_next, s4, s4_next, s5, s5_next, s6, s6_next: int
    trp :real

    initial state: s1 == 1 && s4 == 1 && s2 == 50 && s5 == 50 && s3 == 0 && s6 == 0

    state transitions: 
    transition1: s1_next=s1-1, s2_next=s2-1, s3_next=s3+1, R1=1,R2=0,R3=0,R4=0,R5=0,R6=0, trp= (s1*s2)/((s1*s2)+s3+(s3*0.1)+(s4*s5)+s6+(s6*0.1))
    transition2: s3_next=s3-1, s1_next=s1+1, s2_next=s2+1, R1=0,R2=1,R3=0,R4=0,R5=0,R6=0, trp= s3/((s1*s2)+s3+(s3*0.1)+(s4*s5)+s6+(s6*0.1))
    transition3: s3_next=s3-1, s1_next=s1+1, s5_next=s5+1, R1=0,R2=0,R3=1,R4=0,R5=0,R6=0, trp= (s3*0.1)/((s1*s2)+s3+(s3*0.1)+(s4*s5)+s6+(s6*0.1))
    transition4: s4_next=s4-1, s5_next=s5-1, s6_next=s6+1, R1=0,R2=0,R3=0,R4=1,R5=0,R6=0, trp= (s4*s5)/((s1*s2)+s3+(s3*0.1)+(s4*s5)+s6+(s6*0.1))
    transition5: s6_next=s6-1, s4_next=s4+1, s5_next=s5+1, R1=0,R2=0,R3=0,R4=0,R5=1,R6=0, trp= s6/((s1*s2)+s3+(s3*0.1)+(s4*s5)+s6+(s6*0.1))
    transition6: s6_next=s6-1, s4_next=s4+1, s2_next=s2+1, R1=0,R2=0,R3=0,R4=0,R5=0,R6=1, trp= (s6*0.1/((s1*s2)+s3+(s3*0.1)+(s4*s5)+s6+(s6*0.1))

    property: 
    s5 == 40
    
    Note: Ignoring R1 .. R6
"""

from z3 import *

# State variable strings.
s1 = 's1.{0}'
s2 = 's2.{0}'
s3 = 's3.{0}'
s4 = 's4.{0}'
s5 = 's5.{0}'
s6 = 's6.{0}'

# Transition probability strings.
trprb = 'p.{0}'

# List of variable and probability strings.
probabilties = []
state_variable_list = []

def GetStep(step):
    """ Transition Relations """
    s1 = Int('s1.{0}'.format(step-1))
    s1_nxt = Int("s1.{0}".format(step))
    
    s2 = Int('s2.{0}'.format(step-1))
    s2_nxt = Int("s2.{0}".format(step))
    
    s3 = Int('s3.{0}'.format(step-1))
    s3_nxt = Int("s3.{0}".format(step))
    
    s4 = Int('s4.{0}'.format(step-1))
    s4_nxt = Int("s4.{0}".format(step))
    
    s5 = Int('s5.{0}'.format(step-1))
    s5_nxt = Int("s5.{0}".format(step))
    
    s6 = Int('s6.{0}'.format(step-1))
    s6_nxt = Int("s6.{0}".format(step))
    
    trp = Real('p.{0}'.format(step))

    #ranges = And(0 <= trp, trp <= 1)
    
    transition_1 = And(trp==(ToReal(s1)*ToReal(s2))/((ToReal(s1)*ToReal(s2))+ToReal(s3)+(ToReal(s3)*0.1)+(ToReal(s4)*ToReal(s5))+ToReal(s6)+(ToReal(s6)*0.1)), s1_nxt==(s1 - 1), s2_nxt==(s2 - 1), s3_nxt==(s3 + 1), s4_nxt==(s4), s5_nxt==(s5),s6_nxt==(s6))
    transition_2 = And(trp==((ToReal(s3))/((ToReal(s1)*ToReal(s2))+ToReal(s3)+(ToReal(s3)*0.1)+(ToReal(s4)*ToReal(s5))+ToReal(s6)+(ToReal(s6)*0.1))), s1_nxt==(s1 + 1), s2_nxt==(s2 + 1), s3_nxt==(s3 - 1), s4_nxt==(s4), s5_nxt==(s5),s6_nxt==(s6))
    transition_3 = And(trp==(ToReal(s3)*0.1)/((ToReal(s1)*ToReal(s2))+ToReal(s3)+(ToReal(s3)*0.1)+(ToReal(s4)*ToReal(s5))+ToReal(s6)+(ToReal(s6)*0.1)), s1_nxt==(s1+1), s2_nxt==(s2), s3_nxt==(s3-1), s4_nxt==(s4), s5_nxt==(s5+1), s6_nxt==(s6))
    transition_4 = And(trp==((ToReal(s4)*ToReal(s5))/((ToReal(s1)*ToReal(s2))+ToReal(s3)+(ToReal(s3)*0.1)+(ToReal(s4)*ToReal(s5))+ToReal(s6)+(ToReal(s6)*0.1))), s1_nxt==(s1), s2_nxt==(s2), s3_nxt==(s3), s4_nxt==(s4-1), s5_nxt==(s5-1), s6_nxt==(s6+1))
    transition_5 = And(trp==ToReal(s6)/((ToReal(s1)*ToReal(s2))+ToReal(s3)+(ToReal(s3)*0.1)+(ToReal(s4)*ToReal(s5))+ToReal(s6)+(ToReal(s6)*0.1)), s1_nxt==(s1), s2_nxt==(s2), s3_nxt==(s3), s4_nxt==(s4+1), s5_nxt==(s5+1), s6_nxt==(s6-1))
    transition_6 = And(trp==(ToReal(s6)*0.1/((ToReal(s1)*ToReal(s2))+ToReal(s3)+(ToReal(s3)*0.1)+(ToReal(s4)*ToReal(s5))+ToReal(s6)+(ToReal(s6)*0.1))), s1_nxt==(s1), s2_nxt==(s2+1), s3_nxt==(s3), s4_nxt==(s4+1), s5_nxt==(s5), s6_nxt==(s6-1))
    
    step = Or(transition_1, transition_2, transition_3, transition_4)
    
    probabilties.append(trp)
    state_variable_list.append(s1_nxt)
    state_variable_list.append(s2_nxt)
    state_variable_list.append(s3_nxt)
    state_variable_list.append(s4_nxt)
    state_variable_list.append(s5_nxt)
    state_variable_list.append(s6_nxt)
	
    return step

def GetProperty(step):
    """ Property """
    s5_nxt = Int('s5.{0}'.format(step))

    property = (s5_nxt == 40)

    return property

def GetInitialStates():
    """ Initial States """ #s1 == 1 && s4 == 1 && s2 == 50 && s5 == 50 && s3 == 0 && s6 == 0
    trp = Real('p.{0}'.format(0))
    s1 = Int('s1.{0}'.format(0))
    s2 = Int('s2.{0}'.format(0))
    s3 = Int('s3.{0}'.format(0))
    s4 = Int('s4.{0}'.format(0))
    s5 = Int('s5.{0}'.format(0))
    s6 = Int('s6.{0}'.format(0))

    initial_states = And(s1 == 1, s2 == 50, s3 == 0, s4 == 1, s5 == 50, s6 == 0, trp == 1)
    
    state_variable_list.append(s1)
    state_variable_list.append(s2)
    state_variable_list.append(s3)
    state_variable_list.append(s4)
    state_variable_list.append(s5)
    state_variable_list.append(s6)

    return initial_states
    
def GetStateVaribleStrings(): # Return all the states not including probabilties.
    return state_variable_list

def GetTransitionProbStrings(): # Return all the states including probabilties
    return probabilties