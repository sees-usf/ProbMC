#@ Class that can be used for instantiating variable objects with different names.
class Var:
    def __init__(self, name, type):
        self.name = name
        self.z3var = None
        self.type = type
        if self.type == 'bool':
            self.z3var = Bool(name)
        else:
            self.z3var = Int(name)
        

    def rename(self, new_name):
        self.name = new_name
        self.create_z3var()

    def get_name(self):
        return self.name

    def get_z3var(self):
        return self.z3var

    def create_z3var(self):
        if self.type == 'bool':
            self.z3var = Bool(name)
        else:
            self.z3var = Int(name)
        
