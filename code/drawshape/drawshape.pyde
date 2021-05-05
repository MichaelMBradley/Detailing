# A simple program to create shapes to avoid typing out many tuples manually
# LMB = Place vertex
# RMB = Remove last vertex

w = h = 400
create = []

def setup():
    size(w, h)

def draw():
    global create
    l = len(create)
    background(255)
    if l == 0:
        pass
    elif l == 1:
        point(create[0][0], create[0][1])
    else:
        line(create[-1][0], create[-1][1], create[0][0], create[0][1])
        if l > 2:
            for i in range(len(create) - 1):
                line(create[i][0], create[i][1], create[i+1][0], create[i+1][1])


def mouseClicked():
    global create
    if mouseButton == LEFT:
        create.append((mouseX, mouseY))  # Adds new coordinate
    elif mouseButton == RIGHT:
        if len(create) >= 1:
            create.pop(-1)
    if len(create) >= 1:
        minx, miny = min([x for (x, y) in create]), min([y for (x, y) in create])
        stable = create[:]  # Shallow copy
        for i in range(len(stable)):
            stable[i] = (stable[i][0] - minx, stable[i][1] - miny)  # ALters 'stable' array to be touching x=0 and y=0 at some vertex
        compression = 10
        print("Full: {}\nCompressed ({}x): {}\n".format(stable, compression, [(int(x / compression), int(y / compression)) for (x, y) in stable]))
