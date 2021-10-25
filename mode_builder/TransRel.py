#@ Class for building transition relations
class TransRel:
    def __init__(self, nxt_state_var, cur_state_def):
        self.next_state_var = nxt_state_var
        self.cur_state_def = cur_state_def
        
    def get_z3_encoding(self):
        #@  to be filled
        