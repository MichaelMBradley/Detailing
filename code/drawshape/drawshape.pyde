# This program refuses to run in Processing 4 Alpha 4

# A simple program to create shapes to avoid typing out many arrays manually
# LMB = Place vertex
# RMB = Remove last vertex

w = h = 800
create = []
compression = 10

def setup():
    size(w, h)

def draw():
    l = len(create)
    background(255)
    if l > 0:
        stable = [(compression * int(x/compression), compression * int(y/compression)) for (x, y) in create]
    if l == 0:
        pass
    elif l == 1:
        stroke(0)
        point(create[0][0], create[0][1])
        stroke(127)
        point(stable[0][0], stable[0][1])
    else:
        stroke(0)
        line(create[-1][0], create[-1][1], create[0][0], create[0][1])
        stroke(127)
        line(stable[-1][0], stable[-1][1], stable[0][0], stable[0][1])
        if l > 2:
            for i in range(len(create) - 1):
                stroke(0)
                line(create[i][0], create[i][1], create[i+1][0], create[i+1][1])
                stroke(127)
                line(stable[i][0], stable[i][1], stable[i+1][0], stable[i+1][1])


def mouseClicked():
    if mouseButton == LEFT:
        create.append((mouseX, mouseY))  # Adds new coordinate
    elif mouseButton == RIGHT:
        if len(create) >= 1:
            create.pop(-1)
    if len(create) >= 1:
        minx, miny = min([x for (x, y) in create]), min([y for (x, y) in create])
        stable = create[:]  # Shallow copy
        for i in range(len(stable)):
            stable[i] = (stable[i][0] - minx, stable[i][1] - miny)  # Alters 'stable' array to be touching x=0 and y=0 at some vertex
        out = "{"
        for i, (x, y) in enumerate(stable):
            out += "{}{}, {}{}".format("{", int(x/compression), int(y/compression), "}")
            if i != len(stable)-1:
                out += ", "
        out += "}"
        print out
