# Algorithms to traverse circles
from circ import *
from shaping import *

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

def circletocircle(vertices, circles, a=1):
    dists = [c.cendist(vertices[-1].x, vertices[-1].y) for c in circles]
    points = [circles[dists.index(min(dists))].pv]
    maxx, maxy = maxvert(vertices)
    diag = sqrt(maxx ** 2 + maxy ** 2)
    avail = circles[:]
    for i, v in enumerate(vertices):
        closest = min([PVector.dist(c.pv, v) + c.r for c in avail])
        while PVector.dist(v, points[-1]) > closest * 1.5:
            weights = [10 * PVector.dist(points[-1], c.pv) / diag + PVector.angleBetween(v - points[-1], c.pv - points[-1]) for c in avail]  # Distance and new angle to each potential new point
            small = (-1, 1e6)
            for j, w in enumerate(weights):
                if w < small[1] and avail[j].pv != points[-1]:
                    small = (j, w)
            if small[0] != -1:
                points.append(avail[small[0]].pv)
                avail.pop(small[0])
            else:
                break
    points.append(points[0])
    return points
