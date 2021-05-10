# Algorithms for circle-packing
import random as rng
from shaping import *
from circ import *


def circlepack(vertices):  # FIXME: OLD SYSTEM
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
        available[i] = circ(available[i][0].x - offset, available[i][0].y - offset, available[i][1])
        if circlenearline(cutoff, available[i], vertices):
            fit.append(available[i])
    increase(fit)
    return fit


def circlenearline(cutoff, c, vertices):
    (x0, y0) = c.loc()
    for i in range(-1, len(vertices) - 1):
        (x1, y1, _) = vertices[i].get()
        (x2, y2, _) = vertices[i+1].get()
        if abs(PVector.angleBetween(vertices[i + 1] - vertices[i], c.pv - vertices[i])) > HALF_PI:  # Distance to endpoint if that is closest point on line segment
            distance = PVector.dist(vertices[i], c.pv)
        elif abs(PVector.angleBetween(vertices[i] - vertices[i + 1], c.pv - vertices[i + 1])) > HALF_PI:
            distance = PVector.dist(vertices[i + 1], c.pv)
        else:
            distance = abs((x2-x1)*(y1-y0)-(x1-x0)*(y2-y1))/dist(x1, y1, x2, y2)  # Distance to infinite line described by adjacent vertices
        if distance - c.r <= cutoff:  # If circle is close to edge
            return True
    return False


def randomfillaware(vertices):
    maxx, maxy = maxvert(vertices)
    minradius = max(maxx, maxy) / 60
    maxradius = minradius * 4
    offset = maxradius * 2
    cutoff = ((maxx + maxy + offset * 4) / 2) / 30  # How far from the line segment is acceptable for a circle to be included
    circles = []
    consecutivefailed = 0
    while consecutivefailed < 1000:
        r = rng.uniform(minradius, maxradius) # Generate random size and location
        x = rng.uniform(r - offset, maxx + offset - r)
        y = rng.uniform(r - offset, maxy + offset - r)
        try:
            maxrad = min([c.raddist(x, y) for c in circles])
        except ValueError:
            maxrad = 1e6
        if maxrad < minradius:
            consecutivefailed += 1
        else:
            c = circ(x, y, min(r, maxrad))
            if circlenearline(cutoff, c, vertices):
                circles.append(c)
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
        try:
            maxrad = min([c.raddist(x, y) for c in circles])
        except ValueError:
            maxrad = 1e6
        if maxrad < minradius:
            consecutivefailed += 1
        else:
            circles.append(circ(x, y, min(r, maxrad)))
            consecutivefailed = 0
            
    increase(circles)
    return circles


def increase(circles):
    for i, c in enumerate(circles[:-1]):
        c.r += min([c.circdist(o) for o in circles[:i] + circles[i+1:]])  # Increase radius to touch closest circle
    return circles
