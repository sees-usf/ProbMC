#script takes two or three inputs: 
#input1: model file
#input2: bound to check for the model
#input3: 0 or 1. 1 indicates there is a fourth input containing the encoding of solver for bound-1 on the model 
#input4: if input3==1 then it's the file containing encoding of solver for bound-1. If not present, it's assumed the file-name is constraints.smt
#the output will be saved in constraints.smt file

from z3 import *
import importlib
import os, sys

#########################
def exclude_path(path): 
	assignments = []
	for d in path.decls(): 
		x = Int(d.name())
		assignments.append(x  == path[d])
	print(Not(And(assignments)))
	return Not(And(assignments))







#########################


#importing the python script for model specified in input1 as module
if '/' in sys.argv[1]:
	module_path = sys.argv[1][0:sys.argv[1].rfind('/')+1]
	sys.path.insert(0, os.getcwd()+'/'+module_path)
	module_name = sys.argv[1][sys.argv[1].rfind('/')+1:len(sys.argv[1])]
	module_name = module_name[0:module_name.rfind('.')]
else:
	module_name = sys.argv[1][0:module_name.rfind('.')]
model = importlib.import_module(module_name)
#


if len(sys.argv)>4:
	constraints_file = sys.argv[4]
else:
	constraints_file = 'constraints.smt'


bound = int(sys.argv[2])
solver = Solver()


if bound==0:
	solver.add(model.get_initial_state())
	solver.push()
	solver.add(model.get_property(0))
	#graph.add_nodes(model.get_initial_state()[1])
	check = solver.check()
	solver.pop()
	if (check==sat):
		solver.add(Not(model.get_property(0)))
	smt2 = solver.sexpr()
	with open('constraints.smt', mode='w', encoding='ascii') as f:
		f.truncate()
		f.write(smt2)
		f.close()
	#graph.to_file('filename.txt')



if (sys.argv[3]=='1') and (bound!=0): 
	#graph = Graph()
	#graph.from_file('filename.txt')
	solver.from_file(constraints_file)
	solver.add(model.get_encoding(bound))
	#graph.add_nodes(model.get_encoding[1])
	solver.push()
	solver.add(model.get_property(bound))
	while (solver.check()==sat):
		path=solver.model()
		#add_edges_from_path(graph, path)
		solver.pop()
		solver.add(exclude_path(path))
		solver.push()
		solver.add(model.get_property(bound))
	solver.pop()
	smt2 = solver.sexpr()
	with open('constraints.smt', mode='w', encoding='ascii') as f:
		f.truncate()
		f.write(smt2)
		f.close()
	#graph.to_file('filename.txt')



