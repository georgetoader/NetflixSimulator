# Netflix Simulator

# About my implementation
More information about the project can be found [here](https://ocw.cs.pub.ro/courses/poo-ca-cd/teme/tema).
Worked on a simplified platform capable of handling user commands, queries, and producing personalized recommendations based on each customer’s preferences.

## Important notes
* I used very often the Stream API, which helped me along with sorting, filtering and ordering my
collections.
* For sorting, I used a Comparator with all the criterias chained with `comparing().thenComparing()`.
* For `Query`, I implemented 3 internal classes, that handle each object type because many methods
were implemented differently for each of them (e.g. filters).
