# Function to convert a maze to a graph
from Algorithm.utils.mazegraph import MazeGraph

def maze_to_graph(mazeGrid):
    ''' Converts a 2D binary maze to corresponding graph
        Input : 2D NumPy array with 0 and 1 as elements
        Output : MazeGraph corresponding to input maze
    '''

    directions = ["N", "S", "E", "W"]
    mazeGraph = MazeGraph()  # this initialize the class to mazeGraph
    (height, width) = mazeGrid.shape  # numpy array dimensions into a tuple?

    fwleftarr = ["FL090", "FW016"]
    fwrightarr = ["FR090", "FW016"]
    bkrightarr = ["BW014", "BR090"]
    bkleftarr = ["BW014", "BL090"]

    ObstBoundary = 0.7

    for i in range(height):
        for j in range(width):

            # Only consider blank cells as nodes
            # Instead of consdiering blank cells as nodes, try to allow obstacles to be nodes.
            if mazeGrid[i, j] != 1:

                # Adjacent cell : Top

                for d in directions:
                    neighbors = []

                    if (d == "N"):
                        # move forward
                        if (i > 1) and mazeGrid[i - 2, j] != 1 and mazeGrid[i - 2, j + 1] != 1 and mazeGrid[
                                i - 2, j - 1] != 1:
                            if mazeGrid[i - 2, j] == ObstBoundary or mazeGrid[i - 2, j + 1] == ObstBoundary or mazeGrid[
                                    i - 2, j - 1] == ObstBoundary:
                                neighbors.append(
                                    ((i - 1, j, d), 500, ["FW010"]))
                            else:
                                neighbors.append(((i - 1, j, d), 1, ["FW010"]))

                        # move backwards
                        if (i < height - 2) and mazeGrid[i + 2, j] != 1 and mazeGrid[i + 2, j + 1] != 1 and mazeGrid[
                                i + 2, j - 1] != 1:
                            if mazeGrid[i + 2, j] == 0.7 or mazeGrid[i + 2, j + 1] == 0.7 or mazeGrid[
                                    i + 2, j - 1] == 0.7:
                                neighbors.append(
                                    ((i + 1, j, d), 500, ["BW010"]))
                            else:
                                neighbors.append(((i + 1, j, d), 1, ["BW010"]))

                        # forward left turn
                        exit = False
                        if (j > 4 and i > 4):  # ensure that it is within the border
                            for row in range(2, 5):
                                if exit == True:
                                    break
                                for col in range(2, 5):
                                    if (mazeGrid[i - row, j - col] == 1):
                                        exit = True
                                        break
                                    if (mazeGrid[i - row, j - 1] == 1 or mazeGrid[i - row, j] == 1 or mazeGrid[
                                            i - row, j + 1] == 1):
                                        exit = True
                                        break

                        else:
                            exit = True

                        if (exit == False):
                            denote = False
                            for row in range(2, 5):
                                if denote == True:
                                    break
                                for col in range(2, 5):
                                    if (mazeGrid[i - row, j - col] == 0.7):
                                        neighbors.append(
                                            ((i - 3, j - 3, "W"), 1000, fwleftarr))
                                        denote = True
                                        break
                            if denote == False:
                                neighbors.append(
                                    ((i - 3, j - 3, "W"), 100, fwleftarr))  # increase weights to reduce turning

                        # forward right turning
                        exit = False
                        if (i > 4 and j < width - 5):
                            for row in range(2, 5):
                                if exit == True:
                                    break
                                for col in range(2, 5):
                                    if (mazeGrid[i - row, j + col] == 1):
                                        exit = True
                                        break
                                    if (mazeGrid[i - row, j - 1] == 1 or mazeGrid[i - row, j] == 1 or mazeGrid[
                                            i - row, j + 1] == 1):
                                        exit = True
                                        break
                        else:
                            exit = True

                        if exit == False:
                            denote = False
                            for row in range(2, 5):
                                if denote == True:
                                    break
                                for col in range(2, 5):
                                    if (mazeGrid[i - row, j + col] == 0.7):
                                        neighbors.append(
                                            ((i - 3, j + 3, "E"), 1000, fwrightarr))
                                        denote = True
                                        break
                            if denote == False:
                                neighbors.append(
                                    ((i - 3, j + 3, "E"), 100, fwrightarr))  # increase weights to reduce turning

                        # Backward left Turning
                        exit = False
                        if (i < height - 5 and j > 4):
                            for row in range(2, 5):
                                if exit == True:
                                    break
                                for col in range(2, 5):
                                    if (mazeGrid[i + row, j - col] == 1):
                                        exit = True
                                        break
                                    if (mazeGrid[i + row, j - 1] == 1 or mazeGrid[i + row, j] == 1 or mazeGrid[
                                            i + row, j + 1] == 1):
                                        exit = True
                                        break
                        else:
                            exit = True

                        if exit == False:
                            denote = False
                            for row in range(2, 5):
                                if denote == True:
                                    break
                                for col in range(2, 5):
                                    if (mazeGrid[i + row, j - col] == 0.7):
                                        neighbors.append(((i + 3, j - 3, "E"), 1000,
                                                          bkleftarr))  # increase weights to reduce turning
                                        denote = True
                                        break
                            if denote == False:
                                neighbors.append(((i + 3, j - 3, "E"), 100,
                                                  bkleftarr))  # increase weights to reduce turning

                        # Backwards Right Turning
                        exit = False
                        if (i < height - 5 and j < width - 5):
                            for row in range(2, 5):
                                if exit == True:
                                    break
                                for col in range(2, 5):
                                    if (mazeGrid[i + row, j + col] == 1):
                                        exit = True
                                        break
                                    if (mazeGrid[i + row, j - 1] == 1 or mazeGrid[i + row, j] == 1 or mazeGrid[
                                            i + row, j + 1] == 1):
                                        exit = True
                                        break
                        else:
                            exit = True

                        if exit == False:
                            denote = False
                            for row in range(2, 5):
                                if denote == True:
                                    break
                                for col in range(2, 5):
                                    if (mazeGrid[i + row, j + col] == 0.7):
                                        neighbors.append(((i + 3, j + 3, "W"), 1000,
                                                          bkrightarr))  # increase weights to reduce turning
                                        denote = True
                                        break
                            if denote == False:
                                neighbors.append(
                                    ((i + 3, j + 3, "W"), 100, bkrightarr))  # increase weights to reduce turning

                        # Insert edges in the graph
                        if len(neighbors) > 0:  # if there exist neighbors
                            mazeGraph.edges[(i, j, d)] = neighbors

                    if (d == "S"):  # Vehicle is currently facing south
                        # move forwards
                        if (i < height - 2) and mazeGrid[i + 2, j] != 1 and mazeGrid[i + 2, j + 1] != 1 and mazeGrid[
                                i + 2, j - 1] != 1:
                            if mazeGrid[i + 2, j] == 0.7 or mazeGrid[i + 2, j + 1] == 0.7 or mazeGrid[
                                    i + 2, j - 1] == 0.7:
                                neighbors.append(
                                    ((i + 1, j, d), 500, ["FW010"]))
                            else:
                                neighbors.append(((i + 1, j, d), 1, ["FW010"]))

                        # move backwards
                        if (i > 1) and mazeGrid[i - 2, j] != 1 and mazeGrid[i - 2, j + 1] != 1 and mazeGrid[
                                i - 2, j - 1] != 1:
                            if mazeGrid[i - 2, j] == 0.7 or mazeGrid[i - 2, j + 1] == 0.7 or mazeGrid[
                                    i - 2, j - 1] == 0.7:
                                neighbors.append(
                                    ((i - 1, j, d), 500, ["BW010"]))
                            else:
                                neighbors.append(((i - 1, j, d), 1, ["BW010"]))

                        # move forward left turning
                        exit = False
                        if (i < height - 5 and j < width - 5):
                            for row in range(2, 5):
                                if exit == True:
                                    break
                                for col in range(2, 5):
                                    if (mazeGrid[i + row, j + col] == 1):
                                        exit = True
                                        break
                                    if (mazeGrid[i + row, j - 1] == 1 or mazeGrid[i + row, j] == 1 or mazeGrid[
                                            i + row, j + 1] == 1):
                                        exit = True
                                        break
                        else:
                            exit = True

                        if exit == False:
                            denote = False
                            for row in range(2, 5):
                                if denote == True:
                                    break
                                for col in range(2, 5):
                                    if (mazeGrid[i + row, j + col] == 0.7):
                                        neighbors.append(((i + 3, j + 3, "E"), 1000,
                                                          fwleftarr))  # increase weights to reduce turning
                                        denote = True
                                        break
                            if denote == False:
                                neighbors.append(
                                    ((i + 3, j + 3, "E"), 100, fwleftarr))  # increase weights to reduce turning

                        # move forward right turning
                        exit = False
                        if (i < height - 5 and j > 4):
                            for row in range(2, 5):
                                if exit == True:
                                    break
                                for col in range(2, 5):
                                    if (mazeGrid[i + row, j - col] == 1):
                                        exit = True
                                        break
                                    if (mazeGrid[i + row, j - 1] == 1 or mazeGrid[i + row, j] == 1 or mazeGrid[
                                            i + row, j + 1] == 1):
                                        exit = True
                                        break
                        else:
                            exit = True

                        if exit == False:
                            denote = False
                            for row in range(2, 5):
                                if denote == True:
                                    break
                                for col in range(2, 5):
                                    if (mazeGrid[i + row, j - col] == 0.7):
                                        neighbors.append(((i + 3, j - 3, "W"), 1000,
                                                          fwrightarr))  # increase weights to reduce turning
                                        denote = True
                                        break
                            if denote == False:
                                neighbors.append(
                                    ((i + 3, j - 3, "W"), 100, fwrightarr))  # increase weights to reduce turning

                        # move backwards left turning
                        exit = False
                        if (i > 4 and j < width - 5):
                            for row in range(2, 5):
                                if exit == True:
                                    break
                                for col in range(2, 5):
                                    if (mazeGrid[i - row, j + col] == 1):
                                        exit = True
                                        break
                                    if (mazeGrid[i - row, j - 1] == 1 or mazeGrid[i - row, j] == 1 or mazeGrid[
                                            i - row, j + 1] == 1):
                                        exit = True
                                        break
                        else:
                            exit = True

                        if exit == False:
                            denote = False
                            for row in range(2, 5):
                                if denote == True:
                                    break
                                for col in range(2, 5):
                                    if (mazeGrid[i - row, j + col] == 0.7):
                                        neighbors.append(((i - 3, j + 3, "W"), 1000,
                                                          bkleftarr))  # increase weights to reduce turning
                                        denote = True
                                        break
                            if denote == False:
                                neighbors.append(
                                    ((i - 3, j + 3, "W"), 100, bkleftarr))  # increase weights to reduce turning

                        # move backwards right turning
                        exit = False
                        if (j > 4 and i > 4):
                            for row in range(2, 5):
                                if exit == True:
                                    break
                                for col in range(2, 5):
                                    if (mazeGrid[i - row, j - col] == 1):
                                        exit = True
                                        break
                                    if (mazeGrid[i - row, j - 1] == 1 or mazeGrid[i - row, j] == 1 or mazeGrid[
                                            i - row, j + 1] == 1):
                                        exit = True
                                        break
                        else:
                            exit = True

                        if exit == False:
                            denote = False
                            for row in range(2, 5):
                                if denote == True:
                                    break
                                for col in range(2, 5):
                                    if (mazeGrid[i - row, j - col] == 0.7):
                                        neighbors.append(((i - 3, j - 3, "E"), 1000,
                                                          bkrightarr))  # increase weights to reduce turning
                                        denote = True
                                        break
                            if denote == False:
                                neighbors.append(
                                    ((i - 3, j - 3, "E"), 100, bkrightarr))  # increase weights to reduce turning

                        # Insert edges in the graph
                        if len(neighbors) > 0:  # if there exist neighbors
                            mazeGraph.edges[(i, j, d)] = neighbors

                    if (d == "W"):  # facing the west direction
                        # move forwards
                        if (j > 1) and mazeGrid[i, j - 2] != 1 and mazeGrid[i - 1, j - 2] != 1 and mazeGrid[
                                i + 1, j - 2] != 1:
                            if (mazeGrid[i, j - 2] == 0.7 or mazeGrid[i - 1, j - 2] == 0.7 or mazeGrid[
                                    i + 1, j - 2] == 0.7):
                                neighbors.append(
                                    ((i, j - 1, d), 500, ["FW010"]))
                            else:
                                neighbors.append(((i, j - 1, d), 1, ["FW010"]))

                        # move backwards
                        if (j < width - 2) and mazeGrid[i, j + 2] != 1 and mazeGrid[i - 1, j + 2] != 1 and mazeGrid[
                                i + 1, j + 2] != 1:
                            if mazeGrid[i, j + 2] == 0.7 or mazeGrid[i - 1, j + 2] == 0.7 or mazeGrid[
                                    i + 1, j + 2] == 0.7:
                                neighbors.append(
                                    ((i, j + 1, d), 500, ["BW010"]))
                            else:
                                neighbors.append(((i, j + 1, d), 1, ["BW010"]))

                        # move forward left turn
                        exit = False
                        if (i < height - 5 and j > 4):
                            for row in range(2, 5):
                                if exit == True:
                                    break
                                for col in range(2, 5):
                                    if (mazeGrid[i + row, j - col] == 1):
                                        exit = True
                                        break
                                    if (mazeGrid[i, j - col] == 1 or mazeGrid[i + 1, j - col] == 1 or mazeGrid[
                                            i - 1, j - col] == 1):
                                        exit = True
                                        break
                        else:
                            exit = True

                        if exit == False:
                            denote = False
                            for row in range(2, 5):
                                if denote == True:
                                    break
                                for col in range(2, 5):
                                    if (mazeGrid[i + row, j - col] == 0.7):
                                        neighbors.append(((i + 3, j - 3, "S"), 1000,
                                                          fwleftarr))  # increase weights to reduce turning
                                        denote = True
                                        break
                            if denote == False:
                                neighbors.append(
                                    ((i + 3, j - 3, "S"), 100, fwleftarr))  # increase weights to reduce turning

                        # move forward right turn
                        exit = False
                        if (j > 4 and i > 4):
                            for row in range(2, 5):
                                if exit == True:
                                    break
                                for col in range(2, 5):
                                    if (mazeGrid[i - row, j - col] == 1):
                                        exit = True
                                        break
                                    if (mazeGrid[i, j - col] == 1 or mazeGrid[i - 1, j - col] == 1 or mazeGrid[
                                            i + 1, j - col] == 1):
                                        exit = True
                                        break
                        else:
                            exit = True

                        if exit == False:
                            denote = False
                            for row in range(2, 5):
                                if denote == True:
                                    break
                                for col in range(2, 5):
                                    if (mazeGrid[i - row, j - col] == 0.7):
                                        neighbors.append(((i - 3, j - 3, "N"), 1000,
                                                          fwrightarr))  # increase weights to reduce turning
                                        denote = True
                                        break
                            if denote == False:
                                neighbors.append(
                                    ((i - 3, j - 3, "N"), 100, fwrightarr))  # increase weights to reduce turning

                        # move backwards left turn
                        exit = False
                        if (i < height - 5 and j < width - 5):
                            for row in range(2, 5):
                                if exit == True:
                                    break
                                for col in range(2, 5):
                                    if (mazeGrid[i + row, j + col] == 1):
                                        exit = True
                                        break
                                    if (mazeGrid[i, j + col] == 1 or mazeGrid[i + 1, j + col] == 1 or mazeGrid[
                                            i - 1, j + col] == 1):
                                        exit = True
                                        break
                        else:
                            exit = True

                        if exit == False:
                            denote = False
                            for row in range(2, 5):
                                if denote == True:
                                    break
                                for col in range(2, 5):
                                    if (mazeGrid[i + row, j + col] == 0.7):
                                        neighbors.append(((i + 3, j + 3, "N"), 1000,
                                                          bkleftarr))  # increase weights to reduce turning
                                        denote = True
                                        break
                            if denote == False:
                                neighbors.append(
                                    ((i + 3, j + 3, "N"), 100, bkleftarr))  # increase weights to reduce turning

                        # move backwards right turn
                        exit = False
                        if (i > 4 and j < width - 5):
                            for row in range(2, 5):
                                if exit == True:
                                    break
                                for col in range(2, 5):
                                    if (mazeGrid[i - row, j + col] == 1):
                                        exit = True
                                        break
                                    if (mazeGrid[i, j + col] == 1 or mazeGrid[i - 1, j + col] == 1 or mazeGrid[
                                            i - 1, j + col] == 1):
                                        exit = True
                                        break

                        else:
                            exit = True

                        if exit == False:
                            denote = False
                            for row in range(2, 5):
                                if denote == True:
                                    break
                                for col in range(2, 5):
                                    if (mazeGrid[i - row, j + col] == 0.7):
                                        neighbors.append(((i - 3, j + 3, "S"), 1000,
                                                          bkrightarr))  # increase weights to reduce turning
                                        denote = True
                                        break
                            if denote == False:
                                neighbors.append(
                                    ((i - 3, j + 3, "S"), 100, bkrightarr))  # increase weights to reduce turning

                        # Insert edges in the graph
                        if len(neighbors) > 0:  # if there exist neighbors
                            mazeGraph.edges[(i, j, d)] = neighbors

                    if (d == "E"):
                        # move forward
                        if (j < width - 2) and mazeGrid[i, j + 2] != 1 and mazeGrid[i - 1, j + 2] != 1 and mazeGrid[
                                i + 1, j + 2] != 1:
                            if mazeGrid[i, j + 2] == 0.7 or mazeGrid[i - 1, j + 2] == 0.7 or mazeGrid[
                                    i + 1, j + 2] == 0.7:
                                neighbors.append(
                                    ((i, j + 1, d), 500, ["FW010"]))
                            else:
                                neighbors.append(((i, j + 1, d), 1, ["FW010"]))

                        # move backwards
                        if (j > 1) and mazeGrid[i, j - 2] != 1 and mazeGrid[i - 1, j - 2] != 1 and mazeGrid[
                                i + 1, j - 2] != 1:
                            if mazeGrid[i, j - 2] == 0.7 or mazeGrid[i - 1, j - 2] == 0.7 or mazeGrid[
                                    i + 1, j - 2] == 0.7:
                                neighbors.append(
                                    ((i, j - 1, d), 500, ["BW010"]))
                            else:
                                neighbors.append(((i, j - 1, d), 1, ["BW010"]))

                        # move forward right turn
                        exit = False
                        if (i < height - 5 and j < width - 5):
                            for row in range(2, 5):
                                if exit == True:
                                    break
                                for col in range(2, 5):
                                    if (mazeGrid[i + row, j + col] == 1):
                                        exit = True
                                        break
                                    if (mazeGrid[i, j + col] == 1 or mazeGrid[i - 1, j + col] == 1 or mazeGrid[
                                            i + 1, j + col] == 1):
                                        exit = True
                                        break
                        else:
                            exit = True

                        if exit == False:
                            denote = False
                            for row in range(2, 5):
                                if denote == True:
                                    break
                                for col in range(2, 5):
                                    if (mazeGrid[i + row, j + col] == 0.7):
                                        neighbors.append(((i + 3, j + 3, "S"), 1000,
                                                          fwrightarr))  # increase weights to reduce turning
                                        denote = True
                                        break
                            if denote == False:
                                neighbors.append(
                                    ((i + 3, j + 3, "S"), 100, fwrightarr))  # increase weights to reduce turning

                        # move forward left turn
                        exit = False
                        if (i > 4 and j < width - 5):
                            for row in range(2, 5):
                                if exit == True:
                                    break
                                for col in range(2, 5):
                                    if (mazeGrid[i - row, j + col] == 1):
                                        exit = True
                                        break
                                    if (mazeGrid[i, j + col] == 1 or mazeGrid[i + 1, j + col] == 1 or mazeGrid[
                                            i - 1, j + col] == 1):
                                        exit = True
                                        break

                        else:
                            exit = True
                        if exit == False:
                            denote = False
                            for row in range(2, 5):
                                if denote == True:
                                    break
                                for col in range(2, 5):
                                    if (mazeGrid[i - row, j + col] == 0.7):
                                        neighbors.append(((i - 3, j + 3, "N"), 1000,
                                                          fwleftarr))  # increase weights to reduce turning
                                        denote = True
                                        break
                            if denote == False:
                                neighbors.append(
                                    ((i - 3, j + 3, "N"), 100, fwleftarr))  # increase weights to reduce turning

                        # move backwards left turn
                        exit = False
                        if (j > 4 and i > 4):
                            for row in range(2, 5):
                                if exit == True:
                                    break
                                for col in range(2, 5):
                                    if (mazeGrid[i - row, j - col] == 1):
                                        exit = True
                                        break
                                    if (mazeGrid[i, j - col] == 1 or mazeGrid[i - 1, j - col] == 1 or mazeGrid[
                                            i + 1, j - col] == 1):
                                        exit = True
                                        break
                        else:
                            exit = True

                        if exit == False:
                            denote = False
                            for row in range(2, 5):
                                if denote == True:
                                    break
                                for col in range(2, 5):
                                    if (mazeGrid[i - row, j - col] == 0.7):
                                        neighbors.append(((i - 3, j - 3, "S"), 1000,
                                                          bkleftarr))  # increase weights to reduce turning
                                        denote = True
                                        break
                            if denote == False:
                                neighbors.append(
                                    ((i - 3, j - 3, "S"), 100, bkleftarr))  # increase weights to reduce turning

                        # move backwards right turn
                        exit = False
                        if (i < height - 5 and j > 4):
                            for row in range(2, 5):
                                if exit == True:
                                    break
                                for col in range(2, 5):
                                    if (mazeGrid[i + row, j - col] == 1):
                                        exit = True
                                        break
                                    if (mazeGrid[i, j - col] == 1 or mazeGrid[i + 1, j - col] == 1 or mazeGrid[
                                            i - 1, j - col] == 1):
                                        exit = True
                                        break
                        else:
                            exit = True

                        if exit == False:
                            denote = False
                            for row in range(2, 5):
                                if denote == True:
                                    break
                                for col in range(2, 5):
                                    if (mazeGrid[i + row, j - col] == 0.7):
                                        neighbors.append(((i + 3, j - 3, "N"), 1000,
                                                          bkrightarr))  # increase weights to reduce turning
                                        denote = True
                                        break
                            if denote == False:
                                neighbors.append(
                                    ((i + 3, j - 3, "N"), 100, bkrightarr))  # increase weights to reduce turning

                        if len(neighbors) > 0:  # if there exist neighbors
                            mazeGraph.edges[(i, j, d)] = neighbors

    return mazeGraph
