
def And(left_e, right_e):
    exp = Expr()
    exp.set_left_expr(left_e)
    exp.set_right_expr(right_e)
    exp.set_op('and')
    return exp


def Or(left_e, right_e):
    exp = Expr()
    exp.set_left_expr(left_e)
    exp.set_right_expr(right_e)
    exp.set_op('or')
    return exp

def Not(e):
    exp = Expr()
    exp.set_left_expr(e)
    exp.set_op('Not')
    return exp

def Imply(left_e, right_e):
    exp = Expr()
    exp.set_left_expr(left_e)
    exp.set_right_expr(right_e)
    exp.set_op('->')
    return exp

def Add(left_e, right_e):
    exp = Expr()
    exp.set_left_expr(left_e)
    exp.set_right_expr(right_e)
    exp.set_op('+')
    return exp

def Sub(left_e, right_e):
    exp = Expr()
    exp.set_left_expr(left_e)
    exp.set_right_expr(right_e)
    exp.set_op('-')
    return exp



        