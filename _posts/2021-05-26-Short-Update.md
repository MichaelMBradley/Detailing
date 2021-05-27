---
layout: post
title: Short Update
---

In the past two days since the long weekend, I've been spending most of my time having marginal success with fixing the remaining bugs in the code that I showed off (in bug free manners) in my last post. While I have made some improvement, it's not yet fixed.

While I hope to have much more to show tomorrow as I was close to finishing some work today, for now I'll just show you a variation on a tree traversal method.

{: style="text-align:center"}
![A series of arcs travel around a polyline.](https://raw.githubusercontent.com/MichaelMBradley/Detailing/gh-pages/_assets/05-26/KruskalTraverse.png)

For this method, instead of including the parent node in between child nodes, I have entirely omitted them. As well, since there is only one arc connecting any two nodes it lends itself much better to a connecting method I showed off a few posts ago where I draw an arc between the nodes that represents a portion of the circumcircle these nodes were a part of due to the delaunay triangulation.

In the post I originally showed the method off I was disappointed with how it looked, but with this slightly altered method of traversing the tree I believe it is much more manageable.

That being said, I still have some ideas to smooth the corners of the above image.
