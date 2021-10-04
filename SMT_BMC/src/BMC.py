#script takes two or three inputs: 
#input1: model file
#input2: bound to check for the model
#input3: 0 or 1. 1 indicates there is a fourth input containing the encoding of solver for bound-1 on the model 
#input4: if input3==1 then it's the file containing encoding of solver for bound-1. If not present, it's assumed the file-name is constraints.smt
#the output will be saved in constraints.smt file

from z3 import *
import importlib
import os, sys
from Graph import Graph, Node, Edge
#########################
def exclude_path(path): 
	assignments = []
	for d in path.decls(): 
		x = Int(d.name())
		assignments.append(x  == path[d])
	return Not(And(assignments))

def sort_first(val): 
	return val[0]

def add_edges_from_path(graph, path): 
	for i in path.decls():
		from_index = int(i.name()[i.name().rfind('.')+1:])
		for j in path.decls():
			to_index = int(j.name()[j.name().rfind('.')+1:])
			if from_index==(to_index-1):
				from_node_name = '['
				from_node_list = []
				to_node_name = '['
				to_node_list = []
				for k in path.decls(): 
					if from_index == int(k.name()[k.name().rfind('.')+1:]):
						variable_name = k.name()[k.name().find('s')+1:k.name().find('.')]
						from_node_list.append([int(variable_name), str(path[k])])
					if to_index == int(k.name()[k.name().rfind('.')+1:]):
						to_node_list.append([int(variable_name), str(path[k])])
				from_node_list.sort(key=sort_first)
				to_node_list.sort(key=sort_first)
				flag = False
				for l in from_node_list: 
					if flag == False: 
						from_node_name = from_node_name + str(l[1])
						flag = True
					else: 
						from_node_name = from_node_name + ',' + str(l[1])
				from_node_name = from_node_name + ']'
				flag = False
				for l in to_node_list: 
					if flag == False: 
						to_node_name = to_node_name + str(l[1])
						flag = True
					else: 
						to_node_name = to_node_name + ',' + str(l[1])
				to_node_name = to_node_name + ']'
				from_node = Node.Node(from_node_name)
				to_node = Node.Node(to_node_name)
				edge = Edge.Edge(from_node, to_node)
				graph.add_edges([edge])
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
graph = Graph.Graph()



if bound==0:
	init_const, init_node, state_vector = model.get_initial_state()
	graph.add_nodes(init_node)
	solver.add(init_const)
	solver.push()
	solver.add(model.get_property(0)[0])
	graph.add_nodes(model.get_property(0)[1])
	check = solver.check()
	solver.pop()
	if (check==sat):
		solver.add(Not(model.get_property(0)[0]))
	smt2 = solver.sexpr()
	with open('constraints.smt', mode='w', encoding='ascii') as f:
		f.truncate()
		f.write(smt2)
		f.close()
	graph.to_file('graph.g', state_vector)



if (sys.argv[3]=='1') and (bound!=0): 
	state_vector = graph.from_file('graph.g')
	solver.from_file(constraints_file)
	solver.add(model.get_encoding(bound))
	solver.push()
	solver.add(model.get_property(bound)[0])
	while (solver.check()==sat):
		path=solver.model()
		add_edges_from_path(graph, path)
		solver.pop()
		solver.add(exclude_path(path))
		solver.push()
		solver.add(model.get_property(bound)[0])
	solver.pop()
	smt2 = solver.sexpr()
	with open('constraints.smt', mode='w', encoding='ascii') as f:
		f.truncate()
		f.write(smt2)
		f.close()
	graph.to_file('graph.g', state_vector)



