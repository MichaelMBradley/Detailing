---
layout: default
title: Detailing Blog
---

## What is this?

For my first-year internship at Carleton University I'm working on a piece of code that will add complexity to a given polygon.

In this blog I keep track of what I've tried, what's worked, and what hasn't.

## Posts

<ul>
  {% for post in site.posts %}
    <li>
      <h3><a href="https://michaelmbradley.github.io/Detailing{{ post.url }}">{{ post.title }}</a></h3>
      <h4>{{ post.date | date_to_string }}</h4>
      {{ post.excerpt }}
    </li>
  {% endfor %}
</ul>
