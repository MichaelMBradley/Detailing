# Algorithms to traverse circles
from circ import *

def closestcircle(vertices, circles):
    tests = 25
    lines = []
    for i in range(-1, len(vertices) - 1):
        for j in range(tests):
            x = (vertices[i][0] * (tests - j) / tests) + (vertices[i + 1][0] * j / tests)
            y = (vertices[i][1] * (tests - j) / tests) + (vertices[i + 1][1] * j / tests)
            closestcircle = circ(0, 0, 0)
            distance = 1e6
            for c in circles:
                testdist = abs(c.raddist(x, y))
                if testdist < distance:
                    closestcircle = c
                    distance = testdist
            lines.append(((x, y), closestcircle.loc()))
    return lines

def circletocircle(vertices, circles, a=1, b=1):
    dists = [c.cendist(vertices[0].x, vertices[0].y) for c in circles]
    points = [circles[dists.index(min(dists))].pv]
    for i, v in enumerate(vertices[1:]):
        closest = min([PVector.dist(c.pv, v) for c in circles])
        while PVector.dist(v, points[-1]) != closest:
            weights = [a * PVector.dist(points[-1], c.pv) * b * PVector.angleBetween(v - points[-1], c.pv - points[-1]) for c in circles]
            weightss = ["{}, {}, {}".format(a * PVector.dist(points[-1], c.pv), b * 60 * PVector.angleBetween(v - points[-1], c.pv - points[-1]), c.pv) for c in circles]
            small = (-1, 1e6)
            for j, w in enumerate(weights):
                if w < small[1] and circles[j].pv != points[-1]:
                    small = (i, w)
            if small[0] != -1 and circles[small[0]].pv not in points:
                points.append(circles[small[0]].pv)
                #print(weightss)
                #print(weightss[small[0]])
            else:
                break
    return points
