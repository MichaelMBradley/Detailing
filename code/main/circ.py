class circ:
    def __init__(self, x, y, r):
        self.x = x
        self.y = y
        self.r = r
        self.pv = PVector(x, y)
    
    def loc(self):
        return (self.x, self.y)
    
    def raddist(self, x, y):
        return self.cendist(x, y) - self.r
    
    def cendist(self, x, y):
        return dist(self.x, self.y, x, y)
    
    def circdist(self, c):
        return self.raddist(c.x, c.y) - c.r
    
    def drw(self, xoff=0, yoff=0):
        circle(self.x + xoff, self.y + yoff, self.r * 2)  # p5's circle() accepts diameter, not radius
    
    def __repr__(self):
        return "Circle at ({:.2f}, {:.2f}) with radius {:.2f}".format(self.x, self.y, self.r)
    
    def __str__(self):
        return "{:.2f}, {:.2f}".format(self.x, self.y)
