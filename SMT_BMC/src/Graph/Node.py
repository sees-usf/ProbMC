class Node:

	def __init__(self, name): 
		self.name = name
		self.is_terminal = False

	def make_terminal(self): 
		self.is_terminal = True

	def equals(self, node): 
		if self.name == node.name: 
			return True
		return False
