import numpy as np
import math
from operator import itemgetter

# This function is used to construct a structuring element
# type can be either disc, square, diamond or line
# size should be a positive integer
# angle should be set when type=line
# The function return the structuring element as a list of coordinates
def build_as_list(type, size, angle):
    if (type  == 'disc'):
        strel = []

        for x in range(-size,size+1):
            for y in range(-size,size+1):
                if( (abs(x)-0.5)*(abs(x)-0.5) + (abs(y)-0.5)*(abs(y)-0.5) <= size*size ): # test if the point is on the disc
                    strel.append((y,x))

    elif (type == 'square'):
        strel = [(y,x) for x in range(-size, size+1) for y in range(-size, size+1)]

    elif (type == 'diamond'):
        strel = [(i,j) for i in range(-size,size+1) for j in range(-size+abs(i), size-abs(i)+1)]


    elif (type == 'line'):
        strel = []
        d=size
        a=angle

        #Make sur angle is between -90 and 90
        while (a > 90):
            a = a - 180
        while (a < -90):
            a = a + 180

        #If angle is over 45, we remove 90 and we will turn the result
        rot=0
        if (a>45):
            a = a-90
            rot=1
        elif (a<-45):
            a = a+90
            rot=3;

        #Angle to radians
        a=a*math.pi/180

        #Invert angle to so it depend of the bottom left corner (and not top left corener)
        a=-a

        #For lenth of line
        lx = int(math.ceil(d/math.sqrt(1+math.tan(a)*math.tan(a))))
        if(lx==0):
            lx = 1

        strel = []

        #Bresenham Algorithm
        for x in range(-lx,lx+1):
            y = math.tan(a)*x;
            strel.append((int(round(y)), x))

        if(rot>0):
            #Rotate line if necessary
            for i in range(0, len(strel)):
                strel[i] = (-strel[i][1], strel[i][0])

    else:
        assert(False), type+" is nod a valid structuring element type for this function."

    return strel


# This function is used to construct a structuring element
# type can be either disc, square, diamond or line
# size should be a positive integer
# angle should be set when type=line
# The function return the structuring element as an image
def build(type, size, angle):
    return toImage(build_as_list(type, size, angle))


# This function is used to build an image from a list of coordinates
def toImage(strel):
    max_i = max(strel, key=itemgetter(0))[0]
    min_i = min(strel, key=itemgetter(0))[0]
    max_j = max(strel, key=itemgetter(1))[1]
    min_j = min(strel, key=itemgetter(1))[1]


    #On alloue l'image de sortie
    image = np.zeros([max_i-min_i+1, max_j-min_j+1, 1], np.uint8)

    for (i,j) in strel:
        image[i+abs(min_i), j+abs(min_j)] = 255

    return image

