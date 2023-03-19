import json
import numpy as np


def ReadWriteConvert():
    file = r"C:\Users\siddh\Desktop\mdp-g20\rpi\rpi\algorithm_rpi\mapFromAndroid.json"
    maze = []
    for i in range(22):
        inner = []
        for j in range(22):
            if (i == 0 or i == 21):
                inner.append(1)
                continue
            if (j == 0 or j == 21):
                inner.append(1)
                continue
            inner.append(0)
        maze.append(inner)
    obstacles = []
    with open(file) as json_file:
        data = json.load(json_file)
        obs = data[1:len(obstacles) - 1]
        obs = obs.split('],')

        for ob in obs:
            print("ob:", ob)
            if ob[-1] == ']':
                obj = ob[1:-1]
            else:
                obj = ob[1:]
            print("obj1:", obj)
            obj = obj.split(',')
            obj[0] = int(obj[0])
            obj[1] = int(obj[1])
            obj[2] = obj[2][1]
            obj[3] = int(obj[3])
            print("obj:", obj)

            # print(obj[2][0])
            obstacles.append(obj)

    GOALLIST = []
    GOALLIST.append([2, 2, 'E', 0])
    ObstacleList = []
    # Convert the list of obstacles to fit the tree
    for i in range(len(obstacles)):
        obstacles[i][0] = int(obstacles[i][0])+1
        obstacles[i][1] = int(obstacles[i][1]) + 1
        # obstacles[i][0] += 1
        # obstacles[i][1] += 1

        if (obstacles[i][2] == "E"):
            obstacles[i][2] = "S"

        elif (obstacles[i][2] == "N"):
            obstacles[i][2] = "E"

        elif (obstacles[i][2] == "S"):
            obstacles[i][2] = "W"

        elif (obstacles[i][2] == "W"):
            obstacles[i][2] = "N"

    print(obstacles)

    goalincrement = 3

    for i in range(len(obstacles)):
        Direction = obstacles[i][2]
        Xcoords = obstacles[i][0]
        Ycoords = obstacles[i][1]
        obstacleid = obstacles[i][3]

        maze[Xcoords][Ycoords] = 1
        maze[Xcoords - 1][Ycoords + 1] = 0.7  # topleft
        maze[Xcoords][Ycoords + 1] = 0.7  # top
        maze[Xcoords + 1][Ycoords + 1] = 0.7  # top right
        maze[Xcoords + 1][Ycoords] = 0.7  # right
        maze[Xcoords + 1][Ycoords - 1] = 0.7  # bottom right
        maze[Xcoords][Ycoords - 1] = 0.7  # bottom
        maze[Xcoords - 1][Ycoords - 1] = 0.7  # bottomleft
        maze[Xcoords - 1][Ycoords] = 0.7  # left

        if (Direction == "N"):
            maze[Xcoords - goalincrement][Ycoords] = 0.5
            GOALLIST.append(
                [Xcoords - goalincrement, Ycoords, "S", obstacleid])
            ObstacleList.append((Xcoords - 1, Ycoords - 1, "N"))

        elif (Direction == "S"):
            maze[Xcoords + goalincrement][Ycoords] = 0.5

            GOALLIST.append(
                [Xcoords + goalincrement, Ycoords, "N", obstacleid])
            ObstacleList.append((Xcoords - 1, Ycoords - 1, "S"))

        elif (Direction == "E"):
            maze[Xcoords][Ycoords + goalincrement] = 0.5
            GOALLIST.append(
                [Xcoords, Ycoords + goalincrement, "W", obstacleid])
            ObstacleList.append((Xcoords - 1, Ycoords - 1, "E"))

        elif (Direction == "W"):
            maze[Xcoords][Ycoords - goalincrement] = 0.5
            GOALLIST.append(
                [Xcoords, Ycoords - goalincrement, "E", obstacleid])
            ObstacleList.append((Xcoords - 1, Ycoords - 1, "W"))

        else:
            ObstacleList.append([Xcoords - 1, Ycoords - 1, "NIL", obstacleid])

    print("Obstaclelist=", ObstacleList)
    print("Goallist=", GOALLIST)

    for i in range(22):
        maze[0][i] = 1
        maze[21][i] = 1
        maze[i][0] = 1
        maze[i][21] = 1

    maze = np.array(maze)

    return maze, ObstacleList, GOALLIST


def write_json(data, filename):
    with open(filename, "w") as f:
        json.dump(data, f, indent=4)


def fix_Commands(commands):
    print("Commands before fixing:\n", commands)
    cmds = []
    for i in commands:
        if i == 'Camera':
            cmds.append("RPI|TOCAM")
        elif 'AND|' in i:
            cmds.append(i)
        else:
            cmds.append("STM|" + i)

    # cmds.append("RPI_END|0")  # add stop word
    return cmds
