from Algorithm.utils.logic import astar_search, greedy_sort, reconstruct_path
from Algorithm.utils.maze import maze_to_graph
from Algorithm.utils.parser import ReadWriteConvert, fix_Commands, write_json


def finalmain():
    maze, ObstacleList, GOALLIST = ReadWriteConvert()
    GOALLIST = greedy_sort(GOALLIST)
    Obstaclevisit = []
    print(GOALLIST)
    for i in range(len(GOALLIST)):
        if (i == 0):
            GOALLIST[i].pop()
            GOALLIST[i] = tuple(GOALLIST[i])
        else:
            Obstaclevisit.append("AND|OBS-" + str(GOALLIST[i].pop()))
            GOALLIST[i] = tuple(GOALLIST[i])
    write_json(Obstaclevisit, filename="ObjectIDsequence.json")

    for i in range(1, len(GOALLIST)):
        temp = []
        current = GOALLIST[i]

        temp.append(current)
        if current[2] == "E" or current[2] == "W":
            if (current[0] > 2):
                tempSideGoal = list(current)
                tempSideGoal[0] = tempSideGoal[0] - 1
                temp.append(tuple(tempSideGoal))
                maze[tempSideGoal[0], tempSideGoal[1]] = 0.5

            if (current[0] < 19):
                tempSideGoal = list(current)
                tempSideGoal[0] = tempSideGoal[0] + 1
                temp.append(tuple(tempSideGoal))
                maze[tempSideGoal[0], tempSideGoal[1]] = 0.5

        elif current[2] == "N" or current[2] == "S":
            if (current[1] > 2):
                tempSideGoal = list(current)
                tempSideGoal[1] = tempSideGoal[1] - 1
                temp.append(tuple(tempSideGoal))
                maze[tempSideGoal[0], tempSideGoal[1]] = 0.5

            if (current[1] < 19):
                tempSideGoal = list(GOALLIST[i])
                tempSideGoal[1] = tempSideGoal[1] + 1
                temp.append(tuple(tempSideGoal))
                maze[tempSideGoal[0], tempSideGoal[1]] = 0.5
        GOALLIST[i] = temp

    print(GOALLIST)

    # Convert the maze to a graph
    mazegraph = maze_to_graph(maze)

    # Print the edges with weights
    mazegraph.all_edges()

    ActionsWCamera = []
    cantreachgoal = False
    
    # Run the A*S algorithm for path finding
    lol = []
    FinalActions = []
    for i in range(len(GOALLIST) - 1):
        nodesExplored, pathsExplored, nodesProcessed, currentNode = astar_search(mazegraph, start=GOALLIST[i],
                                                                                 goal=GOALLIST[i + 1])
        GOALLIST[i + 1] = currentNode
        path, cantreachgoal, actions = reconstruct_path(
            nodesExplored, start=GOALLIST[i], goal=currentNode)
        lol.append(path)
        FinalActions += actions
        ActionsWCamera += actions
        ActionsWCamera.append('Camera')

    i = 0
    while (True):
        if "FW" in ActionsWCamera[i] and "FW" in ActionsWCamera[i + 1]:
            Total = int(ActionsWCamera[i][2:]) + int(ActionsWCamera[i + 1][2:])
            if Total >= 100:
                ActionsWCamera[i + 1] = "FW" + str(Total)
            else:
                ActionsWCamera[i + 1] = "FW0" + str(Total)
            del (ActionsWCamera[i])
            continue
        if "BW" in ActionsWCamera[i] and "BW" in ActionsWCamera[i + 1]:
            Total = int(ActionsWCamera[i][2:]) + int(ActionsWCamera[i + 1][2:])
            if Total >= 100:
                print(Total)
                ActionsWCamera[i + 1] = "BW" + str(Total)
            else:
                print(Total)
                ActionsWCamera[i + 1] = "BW0" + str(Total)
            del (ActionsWCamera[i])
            continue

        if (i == len(ActionsWCamera) - 1):
            break
        i += 1

    print("Concat Actions:", ActionsWCamera)
    data = ActionsWCamera
    write_json(data, filename="testing234.json")
    return data, Obstaclevisit


def RunMain():
    data1, obsVisit = finalmain()
    data = fix_Commands(data1)
    print("\n\n\n\n", data)
    return data, obsVisit