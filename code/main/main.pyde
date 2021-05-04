# Beginning of a basic attempt at circle-packing
# TODO: Swap (x, y) to PVector?
from packing import *
from shaping import *

w = h = 300
create = []

def setup():
    size(w, h)
    noFill()  # Useful to see shapes and circle packing
    global ngon, circles, vertices
    # Square:
    # vertices = [(0, 0), (100, 0), (100, 100), (0, 100)]
    # Tilted:
    # m = 20
    # vertices = [(0, m), (100-m, 0), (100, 100-m), (m, 100)]
    # Mouse input:
    # vertices = [(0, 0), (67, 70), (8, 105), (10, 56), (30, 33)]
    # More complex vertices:
    vertices = [(0, 0), (4, 0), (4, 3), (6, 3), (4, 5), (1, 4)]
    vertices = scalevert(30, vertices)
    ngon = polygon(vertices)
    circles = randompack(vertices)


def draw():
    global ngon, circles
    background(255)
    minx, miny = minvert(vertices)
    maxx, maxy = maxvert(vertices)
    xoff = (w - (maxx - minx)) / 2  # Centers horizontally. As each term in the subtraction was to 
    yoff = (h - (maxy - miny)) / 2  # be divided by two, the entire difference was divided by two.
    shape(ngon, xoff, yoff)
    for ((x, y), r) in circles:
        circle(x + xoff, y + yoff, r * 2)  # p5's circle() accepts diameter, not radius


def keyPressed():
    global circles, vertices
    circles = randompack(vertices)  # Randomizes the edge packing

def mouseClicked():
    create.append((mouseX, mouseY))  # Adds new coordinate
    minx, miny = minvert(create)
    stable = create[:]  # Shallow copy
    for i in range(len(stable)):
        stable[i] = (stable[i][0] - minx, stable[i][1] - miny)  # ALters 'stable' array to be touching x=0 and y=0 at some vertex
    print("Raw: {}\nStable: {}\n".format(create, stable))
