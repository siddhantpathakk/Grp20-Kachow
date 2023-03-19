import queue

def greedy_sort(coordinates):
    path = []
    current_point = coordinates[0]
    while coordinates:
        closest_point = min(coordinates,
                            key=lambda x: ((x[0] - current_point[0]) ** 2 + (x[1] - current_point[1]) ** 2) ** 0.5)
        path.append(closest_point)
        current_point = closest_point
        coordinates.remove(closest_point)
    return path


def heuristic(nodeA, nodeB):
    (xA, yA, AD) = nodeA  # gets the coodinates of X and Y of current node
    (xB, yB, BD) = nodeB  # gets the coordinates of X and Y of Destination node
    return abs(xA - xB) + abs(yA - yB)


# A*-Search (A*S) with Priority Queue
def astar_search(mazeGraph, start, goal):
    ''' Function to perform A*S to find path in a graph
        Input  : Graph with the start and goal vertices
        Output : Dict of explored vertices in the graph
    '''
    frontier = queue.PriorityQueue()  # Priority Queue for Frontier

    # initialization
    frontier.put((0, start))  # Add the start node to frontier with priority 0
    explored = {}  # Dict of explored nodes {node : parentNode}
    explored[start] = None  # start node has no parent node
    pathcost = {}  # Dict of cost from start to node
    pathcost[start] = 0  # start to start cost should be 0
    processed = 0  # Count of total nodes processed

    while not frontier.empty():
        currentNode = frontier.get()[1]
        processed += 1

        # stop when goal is reached
        if currentNode in goal:
            if (processed != 1):
                break

        # explore every single neighbor of current node
        for nextNode, weight, action in mazeGraph.neighbors(currentNode):

            # compute the new cost for the node based on the current node/ Calculating g(n)
            newcost = pathcost[currentNode] + weight

            if (nextNode not in explored) or (
                    newcost < pathcost[nextNode]):  # if the newcost is smaller then the nextNode
                # priority= #f(n) = h(n) + g(n);
                priority = heuristic(nextNode, goal[0]) + newcost
                # put new node in frontier with priority
                frontier.put((priority, nextNode))

                # Stores the parent node of the nextnode into explored
                explored[nextNode] = currentNode, action

                # updates g(n) for the nextNode
                pathcost[nextNode] = newcost

    return explored, pathcost, processed, currentNode


def reconstruct_path(explored, start, goal):
    currentNode = goal  # start at the goal node
    path = []  # initiate the blank path
    actions = []
    cantreachgoal = False
    # stop when backtrack reaches start node
    try:
        actions += explored[currentNode][1]
    except TypeError:
        return
    while currentNode != start:
        # grow the path backwards and backtrack
        flag = 0
        path.append(currentNode)
        currentNode = explored[currentNode][0]
        try:
            actions += explored[currentNode][1]
        except TypeError:
            continue

            # Try Changing to left or right
        # currentNode = explored[currentNode]
    path.append(start)  # append start node for completeness
    path.reverse()  # reverse the path from start to goal

    actions.append
    actions.reverse()
    return path, cantreachgoal, actions
