# Algorithms for circle-packing
import random as rng
from shaping import *


def circlepack(vertices):
    """As of right now, this function does not work.
    
    Produces an array of parameters representing the circle-packing of a given set of vertices.
    Assumes vertices are given in order (two adjacent vertices represent an edge).
    
    Parameters
    ----------
    vertices : array
        An array of tuples, each representing a vertex (x, y)
    
    Returns
    -------
    array
        An array of nested tuples in the form ((x, y), r)
    """
    
    packing = []
    radius = 10
    sign = lambda x: x/abs(x) if x != 0 else 0  # Returns the sign of x
    for i in range(-1, len(vertices) - 1):  #FIXME: Doesn't work and isn't commented
        (x1, y1) = vertices[i]
        (x2, y2) = vertices[i+1]
        lineangle = atan2(y2-y1, x2-x1)
        inside = lineangle + PI/2
        outside = lineangle - PI/2
        print("{}, {}, {}, {}: {}".format(x1, y1, x2, y2, lineangle))
        linecircles = (dist(x1, y1, x2, y2) / radius) - 1
        xdiff = (x2 - x1) / linecircles
        ydiff = (y2 - y1) / linecircles
        for j in range(int(linecircles)):
            x = x1 + (sign(xdiff) * radius / 2) + (j * xdiff) + (cos(inside) * radius / 2)
            y = y1 + (sign(ydiff) * radius / 2) + (j * ydiff) + (sin(inside) * radius / 2)
            if True not in [dist(x, y, p[0][0], p[0][1]) < radius for p in packing]:
                packing.append(((x, y), radius))
    return packing


def randompack(vertices):
    """Produces an array of parameters representing random circles around the edges of a polygon.
    
    Assumes vertices are given in order (two adjacent vertices represent an edge).
    Will run more efficiently if described shape touches x and y axes.
    
    Parameters
    ----------
    vertices : array
        An array of tuples, each representing a vertex (x, y)
    
    Returns
    -------
    array
        An array of nested tuples in the form ((x, y), r)
    """
    
    offset = 20
    maxx, maxy = maxvert(vertices)
    maxx += (offset * 2)  # Generate circles all around shape
    maxy += (offset * 2)
    available = randomfill(maxx, maxy)
    cutoff = ((maxx + maxy) / 2) / 30  # How far from the line segment is acceptable for a circle to be included
    fit = []
    for i in range(len(available)):
        available[i] = ((available[i][0][0] - offset, available[i][0][1] - offset), available[i][1])
        if circlenearline(cutoff, available[i], vertices):
            fit.append(available[i])
    increase(fit)
    return fit


def within(p0, p1, p2, r, cutoff):
    '''Helper function for circlenearline()'''
    return min(p1, p2) - cutoff - r <= p0 <= max(p1, p2) + cutoff + r  # Used to determine if circle is in box around line segment


def circlenearline(cutoff, c, vertices):
    (x0, y0) = c[0]
    for i in range(-1, len(vertices) - 1):
        (x1, y1) = vertices[i]
        (x2, y2) = vertices[i+1]
        distance = abs((x2-x1)*(y1-y0)-(x1-x0)*(y2-y1))/dist(x1, y1, x2, y2)  # Distance to infinite line described by adjacent vertices
        if distance - c[1] <= cutoff and within(x0, x1, x2, c[1], cutoff) and within(y0, y1, y2, c[1], cutoff):  # If circle is close to edge
            # This isn't a great solution as it checks if the circle is near the infinite line, and then checks if it's near the specific segment
            # TODO: There is probably a more direct way to calculate the distance from a point to a line segment
            return True
    return False


def randomfillaware(vertices):
    # FIXME: Issues with graph generation system can be tested with:
    # return [((20, 20), 20), ((60, 20), 20), ((140, 20), 20), ((180, 20), 20), ((140, 100), 20), ((180, 60), 20), ((180, 100), 20), ((20, 60), 20), ((100, 20), 20), ((100, 60), 20), ((100, 100), 20), ((20, 100), 20), ((60, 100), 20), ((40, 80), 8), ((80, 40), 8), ((80, 80), 8), ((40, 40), 8), ((170, 170), 20), ((210, 170), 20)]
    maxx, maxy = maxvert(vertices)
    minradius = max(maxx, maxy) / 60
    maxradius = minradius * 4
    offset = maxradius * 2
    cutoff = ((maxx + maxy + offset * 4) / 2) / 30  # How far from the line segment is acceptable for a circle to be included
    circles = []
    consecutivefailed = 0
    while consecutivefailed < 3000:
        r = rng.uniform(minradius, maxradius) # Generate random size ond location
        x = rng.uniform(r - offset, maxx + offset - r)
        y = rng.uniform(r - offset, maxx + offset - r)
        if True not in [dist(x, y, p[0][0], p[0][1]) < p[1] + r for p in circles]:  # Checks if new circle occludes previous circles
            if circlenearline(cutoff, ((x, y), r), vertices):
                circles.append(((x, y), r))
                consecutivefailed = 0
        else:
            consecutivefailed += 1
    increase(circles)
    return circles


def randomfill(w, h):
    """Fills the given space with many non-overlapping circles.
    
    Parameters
    ----------
    w : integer
        The width of the space to fill
    h : integer
        The height of the space to fill
    
    Returns
    -------
    array
        An array of nested tuples in the form ((x, y), r)
    """
    minradius = 3
    maxradius = 10
    consecutivefailed = 0
    circles = []
    while consecutivefailed < 300:
        r = rng.uniform(minradius, maxradius) # Generate random size ond location
        x = rng.uniform(r, w - r)
        y = rng.uniform(r, h - r)
        if True not in [dist(x, y, p[0][0], p[0][1]) < p[1] + r for p in circles]:  # Checks if new circle occludes previous circles
            circles.append(((x, y), r))
            consecutivefailed = 0
        else:
            consecutivefailed += 1
    increase(circles)
    return circles


def increase(circles):
    for i, c in enumerate(circles[:-1]):
        lowest = ((0,0),0)
        lowestdist = 1e6  # Arbitrary
        for o in circles[:i] + circles[i+1:]:  # Find closest circle (not including self)
            testdist = dist(c[0][0], c[0][1], o[0][0], o[0][1]) - (c[1] + o[1])
            if testdist < lowestdist:
                lowest = o
                lowestdist = testdist
        circles[i] = ((c[0][0], c[0][1]), c[1] + lowestdist)  # Increase radius to touch closest circle
    return circles
