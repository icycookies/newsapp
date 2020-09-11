import numpy as np
from bert_serving.client import BertClient
from sklearn.cluster import KMeans
import networkx as nx
import matplotlib.pyplot as plt

def get_word_vec():
    bc = BertClient()
    titles = []
    with open("titles") as file:
        for line in file:
            if (len(titles) > 38240):
                break
            titles.append(line)
    print("encoding...")
    word_vec = bc.encode(titles)
    print("finish encoding.")
    return word_vec

def get_graph(x):
    G = nx.Graph()
    for u in range(x):
        G.add_edge(u, u, weight = 1)
    with open("weighted_graph") as file:
        for line in file:
            edge = line.split("\n")[0].split(" ")
            u = int(edge[0])
            v = int(edge[1])
            w = float(edge[2])
            #if u<=1240 and v<=1240:
            G.add_edge(u, v, weight = w)
    return G

def propagate(x, G):
    y = np.zeros((G.number_of_nodes(), 12), dtype = float)
    for u, v in list(G.edges):
        y[u] += x[v] * G.edges[u, v]['weight']

    for u in range(G.number_of_nodes()):
        y[u] = x[u] * 0.4 + y[u] / G.degree[u] * 0.6

    return y;

if __name__ == "__main__":
    x = get_word_vec()
    x.resize((x.shape[0], 12, 64))
    x = x.mean(axis = 2)
    G = get_graph(x.shape[0])
    for i in range(3):
        x = propagate(x, G)
    print("finish propagation")
    pred = KMeans(n_clusters = 5, random_state = 9).fit_predict(x)
    print("finish clustering")
    plt.scatter(x[:, 0], x[:, 1], c=pred)
    plt.show()
    with open("output", "w") as file:
        for elem in pred:
            file.write(str(elem) + "\n")
    