add_library('triangulate')
# Beginning of a basic attempt at circle-packing
from time import time as t
from graphing import *
from packing import *
from shaping import *
from traversing import *

w = h = 800

def setup():
    size(w, h)
    noFill()  # Useful to see shapes and circle packing
    global ngon, vertices
    # Square:
    # vertices = [(0, 0), (100, 0), (100, 100), (0, 100)]
    # Tilted:
    # m = 20
    # vertices = [(0, m), (100-m, 0), (100, 100-m), (m, 100)]
    # Mouse input:
    # vertices = [(12, 8), (25, 8), (16, 20), (0, 10), (8, 0), (25, 2)]
    # More complex vertices:
    vertices = [(0, 0), (12, 0), (12, 9), (18, 9), (12, 15), (3, 12)]
    vertices = scalevert(w / 40, vertices)
    vertices = convert(vertices)
    ngon = polygon(vertices)
    calc()


def draw():
    global ngon, circles, graphs, closest, traverse, tri
    background(255)
    minx, miny = minvert(vertices)
    maxx, maxy = maxvert(vertices)
    xoff = (w - (maxx - minx)) / 2  # Centers horizontally. As each term in the subtraction was to 
    yoff = (h - (maxy - miny)) / 2  # be divided by two, the entire difference was divided by two.
    fill(0)
    text("Circles: {}".format(len(circles)), 10, 10)
    # text("Graphs: {}".format(len(graphs)), 10, 20)
    noFill()
    shape(ngon, xoff, yoff)
    for c in circles:
        c.drw(xoff, yoff)
    stroke(255, 0, 0)
    # for ((x1, y1), (x2, y2)) in closest:
        # line(x1 + xoff, y1 + yoff, x2 + xoff, y2 + yoff)
    strokeWeight(3)
    for i in range(len(traverse) - 1):
        line(traverse[i].x + xoff, traverse[i].y + yoff, traverse[i+1].x + xoff, traverse[i+1].y + yoff)     
        # text("{}".format(i + 1), traverse[i].x + xoff, traverse[i].y + yoff) 
    stroke(0, 0, 255)
    strokeWeight(1)
    # for g in graphs:
    for n in graphs:
        if len(n.touching) > 0:
            for t in n.touching:
                line(n.x + xoff, n.y + yoff, t.x + xoff, t.y + yoff)
        else:
            point(n.x + xoff, n.y + yoff)
    stroke(0)


def keyPressed():
    calc()


def calc():
    global circles, vertices, graphs, closest, traverse, tri
    pt = t()
    circles = randomfillaware(vertices)
    print("Packing: {:.2f}".format(t()-pt))
    pt = t()
    graphs = creategraph(circles)
    print("Graphing: {:.2f}".format(t()-pt))
    pt = t()
    graphs = condense(graphs)[0]
    print("Condense: {:.2f}".format(t()-pt))
    pt = t()
    closest = closestcircle(vertices, circles)
    print("Closest: {:.2f}".format(t()-pt))
    pt = t()
    traverse = circletocircle(vertices, circles)
    print("Traverse: {:.2f}".format(t()-pt))
    print "\n"
