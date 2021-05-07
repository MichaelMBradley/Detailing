w = h = 600

def setup():
    global circles, vertices
    circles, vertices = preset("basic")
    size(w, h)

def draw():
    global circles, vertices
    background(255)
    for((x, y), r) in circles:
        circle(x, y, r * 2)
    for i in range(-1, len(vertices) - 1):
        line(vertices[i][0], vertices[i][1], vertices[i + 1][0], vertices[i + 1][1])

def preset(lvl):
    if lvl == "basic":
        return [((200, 200), 50), ((200, 300), 50), ((200, 400), 50), ((300, 400), 50), ((400, 400), 50), ((400, 300), 50), ((400, 200), 50), ((300, 200), 50)], [(150, 150), (150, 450), (450, 450), (450, 150)]
    elif lvl == "advanced":
        return [((200, 200), 50), ((200, 300), 50), ((190, 410), 50), ((300, 400), 50), ((400, 400), 50), ((400, 325), 25), ((400, 275), 25), ((400, 190), 50), ((300, 200), 50)], [(150, 150), (150, 400), (360, 360), (450, 200)]
    else:
        return [], []
