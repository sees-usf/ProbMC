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
	index = -1
	for i in path.decls(): 
		index_temp = int(i.name()[i.name().rfind('.')+1:])
		if index_temp > index: 
			index = index_temp

	

	for i in range(0, index): 
		from_node_name = '['
		from_node_list = []
		to_node_name = '['
		to_node_list = []
		for j in path.decls(): 
			if int(j.name()[j.name().rfind('.')+1:])==i: 
				from_node_list.append([int(j.name()[j.name().find('s')+1:j.name().find('.')]), str(path[j])])
			if int(j.name()[j.name().rfind('.')+1:])==i+1: 
				to_node_list.append([int(j.name()[j.name().find('s')+1:j.name().find('.')]), str(path[j])])

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
		temp = to_node_name[:to_node_name.rfind(',')]
		temp = temp[temp.rfind(',')+1:]
		if (temp=='40'):
			to_node.make_terminal()
		edge = Edge.Edge(from_node, to_node)
		graph.add_edges([edge])

def loop_constraints(bound):
	constraints = []
	x = Int('s2.{0}'.format(bound))
	for i in range(0,bound):
		y = Int('s2.{0}'.format(i))
		constraints.append(Not(x == y))
	return And(constraints)

def add_loops_to_graph(model, graph, loop_size):
	loop_solver = Solver()
	paths = []
	#print(loop_solver.sexpr())
	for i in range(2,loop_size): 
		for n in graph.nodes:
			loop_solver.push()
			if not (n.marked):
				s2_1 = Int('s2.{0}'.format(0))
				loop_solver.add(s2_1 == n.get_int())
				for j in range(1,i+1): 
					loop_solver.add(model.get_encoding(j))
				x_temp = Int('s2.{0}'.format(i))
				loop_solver.add(s2_1 == x_temp)
				n.mark()
				while (loop_solver.check()==sat):
					path=loop_solver.model()
					paths.append(path)
					loop_solver.add(exclude_path(path))
					#print(loop_solver.sexpr())
			loop_solver.pop()

	for path in paths:
		add_edges_from_path(graph, path)



#########################


#importing the python script for model specified in input1 as module
if '/' in sys.argv[1]:
	module_path = sys.argv[1][0:sys.argv[1].rfind('/')+1]
	sys.path.insert(0, os.getcwd()+'/'+module_path)
	sys.path.insert(0, module_path)

	module_name = sys.argv[1][sys.argv[1].rfind('/')+1:len(sys.argv[1])]
	module_name = module_name[0:module_name.rfind('.')]
else:
	module_name = sys.argv[1][0:module_name.rfind('.')]
model = importlib.import_module(module_name)
#


if len(sys.argv)>4:
	constraints_file = sys.argv[4]
else:
	constraints_file = '/Users/mo/usf/projects/probmc/ProbMC/SMT_BMC/src/constraints.smt'
	#constraints_file = 'constraints.smt'

graph_file = '/Users/mo/usf/projects/probmc/ProbMC/SMT_BMC/src/graph.g'
#graph_file = 'graph.g'

bound = int(sys.argv[2])
solver = Solver()
graph = Graph.Graph()



if bound==0:
	init_const, init_node, state_vector = model.get_initial_state()
	graph.add_nodes(init_node)
	solver.add(init_const)
	smt2 = solver.sexpr()
	with open(constraints_file, mode='w', encoding='ascii') as f:
		f.truncate()
		f.write(smt2)
		f.close()
	graph.to_file(graph_file, state_vector)



if (sys.argv[3]=='1') and (bound!=0): 
	state_vector = graph.from_file(graph_file)
	solver.from_file(constraints_file)
	solver.add(model.get_encoding(bound))
	#solver.add(loop_constraints(bound))
	solver.push()
	solver.add(model.get_property(bound))
	count = 0
	flag = False
	while (solver.check(model.get_property(bound))==sat):
		path=solver.model()
		add_edges_from_path(graph, path)
		solver.pop()
		solver.add(exclude_path(path))
		solver.push()
		solver.add(model.get_property(bound))
		count = count+1
		flag = True
	solver.pop()
	print(count)
	# if flag or bound>25:
	# 	add_loops_to_graph(model, graph, 3)
	smt2 = solver.sexpr()
	with open(constraints_file, mode='w', encoding='ascii') as f:
		f.truncate()
		f.write(smt2)
		f.close()
	graph.to_file(graph_file, state_vector)



