# Beginning of a basic attempt at circle-packing
# TODO: Swap (x, y) to PVector?
from graphing import *
from packing import *
from shaping import *

w = h = 400

def setup():
    size(w, h)
    noFill()  # Useful to see shapes and circle packing
    global ngon, circles, vertices, graphs
    # Square:
    # vertices = [(0, 0), (100, 0), (100, 100), (0, 100)]
    # Tilted:
    # m = 20
    # vertices = [(0, m), (100-m, 0), (100, 100-m), (m, 100)]
    # Mouse input:
    # vertices = [(12, 8), (25, 8), (16, 20), (0, 10), (8, 0), (25, 2)]
    # More complex vertices:
    vertices = [(0, 0), (12, 0), (12, 9), (18, 9), (12, 15), (3, 12)]
    vertices = scalevert(10, vertices)
    ngon = polygon(vertices)
    circles = randomfillaware(vertices)
    graphs = creategraph(circles)


def draw():
    global ngon, circles, graphs
    background(255)
    minx, miny = minvert(vertices)
    maxx, maxy = maxvert(vertices)
    xoff = (w - (maxx - minx)) / 2  # Centers horizontally. As each term in the subtraction was to 
    yoff = (h - (maxy - miny)) / 2  # be divided by two, the entire difference was divided by two.
    fill(0)
    text("Circles: {}\nGraphs: {}".format(len(circles), len(graphs)), 10, 10)
    noFill()
    shape(ngon, xoff, yoff)
    for ((x, y), r) in circles:
        circle(x + xoff, y + yoff, r * 2)  # p5's circle() accepts diameter, not radius
    for g in graphs:
        for n in g:
            if len(n.touching) > 0:
                for t in n.touching:
                    line(n.x + xoff, n.y + yoff, t.x + xoff, t.y + yoff)
            else:
                point(n.x + xoff, n.y + yoff)


def keyPressed():
    global circles, vertices, graphs
    circles = randomfillaware(vertices)  # Randomizes the edge packing
    graphs = creategraph(circles)
