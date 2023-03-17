class MazeGraph(object):

    def __init__(self):
        self.edges = {}  

    def all_edges(self):
        return self.edges

    def neighbors(self, node):
        return self.edges[node]
