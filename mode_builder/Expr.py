#@ Create expression from variables or operations on two expressions.
class Expr:
    def __init__(self, var):
            self.var = var
            self.op = None
            self.left = None
            self.right = None


    def set_var(self, var):
        self.var = var

    def set_op(self, op):
        self.op = op

    def set_left_expr(self, left_expr):
        self.left = left_expr

    def set_right_expr(self, right_expr):
        self.right = right_expr

    # # overload + operator
    # def __add__(self, e):
    #     e = Expr(self.left + self.right)
    #     e.set_op('+')
    #     return e

    # # overload - operator
    # def __sub__(self, point_ov):
    #     return Point(self.__xCoord - point_ov.__xCoord, self.__yCoord - point_ov.__yCoord)

    # # overload < (less than) operator
    # def __lt__(self, point_ov):
    #     return math.sqrt(self.__xCoord ** 2 + self.__yCoord ** 2) < math.sqrt(point_ov.__xCoord ** 2 + point_ov.__yCoord ** 2)

    # # overload > (greater than) operator
    # def __gt__(self, point_ov):
    #     return math.sqrt(self.__xCoord ** 2 + self.__yCoord ** 2) > math.sqrt(point_ov.__xCoord ** 2 + point_ov.__yCoord ** 2)

    # # overload <= (less than or equal to) operator
    # def __le__(self, point_ov):
    #     return math.sqrt(self.__xCoord ** 2 + self.__yCoord ** 2) <= math.sqrt(point_ov.__xCoord ** 2 + point_ov.__yCoord ** 2)

    # # overload >= (greater than or equal to) operator
    # def __ge__(self, point_ov):
    #     return math.sqrt(self.__xCoord ** 2 + self.__yCoord ** 2) >= math.sqrt(point_ov.__xCoord ** 2 + point_ov.__yCoord ** 2)

    # # overload == (equal to) operator
    # def __eq__(self, point_ov):
    #     return math.sqrt(self.__xCoord ** 2 + self.__yCoord ** 2) == math.sqrt(point_ov.__xCoord ** 2 + point_ov.__yCoord ** 2)


        