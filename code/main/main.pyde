# This code is temporary
# It's only meant to exist, just to have something runnable

import random


def setup():
    size(400, 400)
    global ngon
    ngon = polygon([(0, 0), (40, 0), (40, 30), (60, 30), (40, 50), (10, 40)])

xdir = random.random() * 5
ydir = random.random() * 5
x = 0
y = 0
def draw():
    global x, y, xdir, ydir
    background(255)
    shape(ngon, x, y)
    x+=xdir
    y+=ydir
    if (not (0 <= x <= 340)) or (not (0 <= y <= 350)):
        xdir *= (random.random() * -2)
        x = min(max(0, x) , 340)
        ydir *= (random.random() * -2)
        y = min(max(0, y) , 350)

def keyPressed():
    sys.exit()

def polygon(vertices):
    """Turns a set of vertices into a shape.
    
    Vertices should already represent a valid polygon.
    
    Parameters
    ----------
    vertices : array
        An array of tuples, each representing a vertex (x, y)
    
    Returns
    -------
    shape
        A shape object with the specified vertices
    
    """
    vertices.append(vertices[0])  # Appends initial vertex to the end of the list so that the last line is drawn
    ngon = createShape();
    ngon.beginShape();
    for (x, y) in vertices:
        ngon.vertex(x, y)
    ngon.endShape()
    return ngon
    
