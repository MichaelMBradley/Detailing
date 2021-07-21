---
layout: post
title: Traversal
---

I started out by fixing the issue I had previously where my limited spanning trees were sometimes not trees. The problem was that my artificial restriction on size sometimes resulted in one tree thinking it had combined with another, but the other did not combine with it. In any case, I fixed it.

{: style="text-align:center"}
![A set of trees connect circles around a polyline.](https://raw.githubusercontent.com/MichaelMBradley/Detailing/main/docs/_assets/05-13/SmallKruskalFixed.png)

After this, I finished an algorithm that created an order in which to traverse the trees. This is not the final algorithm, but it's good enough for now.

{: style="text-align:center"}
![A line connects the trees around a polyline.](https://raw.githubusercontent.com/MichaelMBradley/Detailing/main/docs/_assets/05-13/KruskalTraverse.png)

The algorithm works by assigning each tree to a point on the edge closest to it, then saves the trees in the order that they appear along each edge. This has some issues, mainly that two consecutive trees can be a bit far from each other, and that it assumes the order of the trees should be based purely on the start point, but it can be improved on later.

{: style="text-align:center"}
![Trees connecting circles pop up in order around a polyline.](https://raw.githubusercontent.com/MichaelMBradley/Detailing/main/docs/_assets/05-13/TreeSelection.gif)

One strategy for drawing a curve around the circles in order is to do something like the following:

{: style="text-align:center"}
![A drawing of a circle packing is turned int a line.](https://raw.githubusercontent.com/MichaelMBradley/Detailing/main/docs/_assets/05-13/GraphToLine.jpeg)

In this image I swap from tree to tree, just drawing a line around the perimeter of the tree. I am also looking into a strategy of using the circumcircle connecting any two nodes (and an unknown third node), but it's still unclear to me how to deal with leaves in this strategy.

The tree traversal strategy in this image is roughly this:

{: style="text-align:center"}
![Several tree traversal strategies.](https://raw.githubusercontent.com/MichaelMBradley/Detailing/main/docs/_assets/05-13/GraphTraversal.jpeg)

In the first two trees, I traverse to a leaf, and then traverse across leaves in order until there are none left, and then return to the root. In the third strategy, I visit every node while swapping between leaves. This is the strategy I have implemented as a beginning.

Going clockwise around the polyline, here is the order in which my first algorithm plans to visit the nodes:

{: style="text-align:center"}
![Each node in a circle packing is visited.](https://raw.githubusercontent.com/MichaelMBradley/Detailing/main/docs/_assets/05-13/CompleteTraverse.gif)

My algorithm still has bugs. As mentioned earlier, the tree selection algorithm is not great. As well, I attempt to traverse every exterior tree in a clockwise manner and every interior tree in a counter-clockwise manner but it seems like my current implementation is bugged.

Along the way I also fixed a bug preventing circles from generating at a small distance away from the exterior of the polyline. The circles can now be packed properly.

{: style="text-align:center"}
![A very tight circle packing of a polyline.](https://raw.githubusercontent.com/MichaelMBradley/Detailing/main/docs/_assets/05-13/TightPacking.png)
