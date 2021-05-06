class node:
    def __init__(self, c):
        self.x = c.x
        self.y = c.y
        self.r = c.r
        self.touching = []
        self.graph = set()
    
    def findtouching(self, nodes):
        for n in nodes:
            if abs(dist(self.x, self.y, n.x, n.y) - (self.r + n.r)) <= 1: # Allowing some rounding errors
                self.touching.append(n)
    
    def graphing(self, n):
        if n not in self.graph:
            self.graph.update([n])
            for d in self.touching:
                d.graphing(n)
    
    def __repr__(self):
        return "Node at ({:.2f},{:.2f}) with radius {:.2f}".format(self.x, self.y, self.r)
    
    def __str__(self):
        return "{:.2f}, {:.2f}".format(self.x, self.y)


def creategraph(circles):
    available = [node(c) for c in circles]
    graphs = []
    for i, n in enumerate(available):
        n.findtouching(available[:i] + available[i+1:])
    for n in available:
        for t in n.touching:
            n.graphing(t)
    for n in available:
        if n.graph not in graphs:
            graphs.append(n.graph)
    return graphs
