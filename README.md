# Hierarchical Clustering

This project implements a hierarchical clustering algorithm capable of analyzing data read from SQL relational database tables.

The algorithm employs a bottom-up agglomerative clustering approach:

1. Initialize each example (represented as real number vectors) as a single cluster
2. Iteratively merge the two clusters two at a time based on distance metrics
3. Continue until a single cluster containing all dataset examples is formed

## Distance Calculation Methods

### Distance Between Examples

Euclidean distance is used for measuring the distance between two examples $x = [x_1, x_2, ..., x_n]$ and $y = [y_1, y_2, .., y_n]:$

$$\text{dist}(x,y)=\sqrt{\sum_{i=1}^{n}{(x_{i}-y_{i})^{2}}}$$

### Distance Between Clusters

Two cluster distance methods are supported:

#### Single Link

Distance between clusters is defined as the minimum distance between any pair of examples from the two clusters:

$$D(C_{1},C_{2})=min_{t_{1}\in C_{1}, t_{2}\in C_{2}}\text{dist}(t_{1},t_{2})$$

#### Average Link

Distance between clusters calculated as the average of distances between all pairs of examples from the two clusters:

$$D(C_{1},C_{2})=\frac{\sum_{t_{1}\in C_{1},t_{2} \in C_{2}} \text{dist}(t_{1},t_{2})}{|C_{1}|\times|C_{2}|}$$

where $|C_1|$ and $|C_2|$ represent the number of examples in clusters $C_1$ and $C_2$.

## Versions

There are two versions of the project:

### CLI Version (Base Project)

Features a Client and Server architecture designed for command line usage. \
They are decoupled components functioning as independent applications.

The services offered by the server to the user via the client are:

- Consultation of a dendrogram previously serialized in a file.
- Generation from scratch of a dendrogram from a dataset, read from a database
  MySQL, to which one is previously connected. This operation is customizable
  by the user, who has the option to specify the depth of the dendrogram and the type of
  calculation of the distance between clusters. The generation of the dendrogram is always followed by
  by its saving to a file, the path to which is specified by the user

### GUI Version (Extended Project)

- Integrates JavaFX-based graphical interfaces for both client and server
- Features interactive dendrograms with adjustable depth visualization
- Node labels reference indices identifying the position of examples within the loaded dataset

## Applications

Hierarchical clustering is widely used in:

- Pattern recognition
- Bioinformatics (gene and protein classification)
- Document clustering and taxonomy creation
- Data mining
- Machine learning

## Getting Started

Refer to the documentation (currently written in Italian) in the docs directory.
