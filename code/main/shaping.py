# Functions to help with the shapes


def scalevert(s, vertices):
    """Scales up an array of vertices by a set amount.
    
    Parameters
    ----------
    s : integer
        The scaling factor
    vertices : array
        An array of tuples in the form (x, y) representing each vertex
    
    Returns
    -------
    array
        An array of tuples in the form (x, y) representing each vertex
    """
    
    for i, v in enumerate(vertices):
        vertices[i] = (v[0] * s, v[1] * s)
    return vertices


def maxvert(vertices):
    """Gives the maximum x and y cooridinates in the list of vertices.
    
    Parameters
    ----------
    vertices : array
        An array of tuples in the form (x, y) representing each vertex
    
    Returns
    -------
    tuple
        A tuple in the form (x, y) representing the largest x and y values in the list of vertices
    """
    return (max([v[0] for v in vertices]), max([v[1] for v in vertices]))


def minvert(vertices):
    """Gives the minimum x and y cooridinates in the list of vertices.
    
    Parameters
    ----------
    vertices : array
        An array of tuples in the form (x, y) representing each vertex
    
    Returns
    -------
    tuple
        A tuple in the form (x, y) representing the smallest x and y values in the list of vertices
    """
    return (min([v[0] for v in vertices]), min([v[1] for v in vertices]))


def polygon(vertices):
    """Turns an array of vertices into a shape.
    
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
    
    local = vertices[:]
    local.append(local[0])  # Appends initial vertex to the end of the list so that the last line is drawn
    ngon = createShape();
    ngon.beginShape();
    for (x, y) in local:
        ngon.vertex(x, y)
    ngon.endShape()
    return ngon
