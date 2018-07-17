import cv2
import numpy as np
from myLibs.ImageProcessing.MorphologicalImageProcessing import structElement as strel

def displayImage(name,image):
    cv2.imshow(name, image)

# Implementation for learning purpose. Do not use
def myErodeHandmade(image,struct):
    image = image[:,:,0]
    im2=np.zeros((image.shape[0],image.shape[1]),np.uint8)

    #For each pixel of the image
    for i in range(0,image.shape[0]):
        for j in range (0,image.shape[1]):
            l = []
            #For each pixel of the structuring element
            for (x,y) in struct :
                #check if we are still in the image
                if i+x >= 0 and i+x < image.shape[0] and j+y >= 0 and j+y < image.shape[1]:
                    l.append(image[x+i,y+j])
                else:
                    l.append(255)
            # set the image pixel to the darker (minimal) color
            im2[i][j] = min(l)

    return im2

def myErode(image,struct):
    return cv2.erode(image,struct)

# Implementation for learning purpose. Do not use
def myDilatHandmade(image,struct):
    structi=[]
    for (x,y) in struct :
        structi.append((-x,-y))

    # Do an erosion on the inverted image and invert the result again
    return 255 - myErodeHandmade(255-image,structi)
    return im2

def myDilat(image,struct):
    return cv2.dilate(image,struct)

def myGrad(image, struct):
    return myDilat(image,struct)-myErode(image,struct)

def myOpen(image, struct):
    return myDilat(myErode(image,struct),struct)

def myClose(image, struct):
    return myErode(myDilat(image,struct),struct)

def myCondDilat(imageM, imageI, struct):
    return np.minimum(cv2.dilate(imageM,struct), imageI)

def myCondErode(imageM, imageI, struct):
    return np.maximum(cv2.erode(imageM,struct), imageI)

def myReconInf(imageM, imageI, struct):
    old=imageM
    count=0

    while 1 :
        tmp = myCondDilat(old,imageI,struct)

        if not np.array_equal(tmp,old):
            old=np.copy(tmp)
            count+=1
        else :
            return tmp

def myReconSup(imageM, imageI, struct):
    old=imageM
    count=0

    while 1 :
        tmp = myCondErode(old,imageI,struct)

        if not np.array_equal(tmp,old):
            old=np.copy(tmp)
            count+=1
        else :
            return tmp

def myReconOpen(imageI ,structOpen, structRecon) :
    return myReconInf(myOpen(imageI,structOpen),imageI,structRecon)

def myReconClose(imageI ,structOpen, structRecon) :
    return myReconSup(myClose(imageI,structOpen),imageI,structRecon)

def myHMax(image,intensiteRelative,struct):
    gamma8 = strel.build("square", 1, None)

    for i in range(0, image.shape[0]):
        for j in range(0, image.shape[1]):
            if (image[i][j] - intensiteRelative >= 0):
                image[i][j] = image[i][j] - intensiteRelative
            else:
                image[i][j] = 0

    return myReconOpen(image, struct, gamma8)

def myHMin(image,intensiteRelative,struct):
    gamma8 = strel.build("square", 1, None)

    for i in range(0, image.shape[0]):
        for j in range(0, image.shape[1]):
            if (image[i][j] + intensiteRelative <= 255):
                image[i][j] = image[i][j]+ intensiteRelative
            else:
                image[i][j] = 255

    return myReconClose(image, struct, gamma8)

def watersheds(imageG, listMarks, struct):
    print ('Compute watersheds')
    print (str(len(listMarks))+ ' objects to contour')

    ## Initialization
    # newImage will be the output image
    newImage = np.zeros(imageG.shape, imageG.dtype)

    # marks will be used to sort marks by colors (original colors) and to assign a color for each one (watersheds color)
    marks=[[] for i in range (0,256)]
    #sizes will contains an histogram of shades of gray in marks
    sizes=np.zeros([256],np.int)

    # Iterate over the image
    for i in range(0,imageG.shape[0]):
        for j in range(0, imageG.shape[1]):
            # For each input mark
            for k in range(0, len(listMarks)):
                if listMarks[k][i,j]>0:
                    # Associate a color (k+1) to the mark
                    marks[imageG[i,j]].append(((i,j),k+1))
                    # Add pixels from original image that match this color in sizes
                    sizes[imageG[i,j]]+=1
                    # Color marks with the color (k+1) in the output image
                    newImage[i,j]=k+1

    print ('End of Initialization')

    count=0
    # while there is still pixels to color, we continue
    while np.sum(sizes)>0:

        if count%1000 == 0:
            print('Watersheds in progress. Iteration '+str(count))
            print(str(np.sum(sizes))+' pixels to color.')

        # Store colors where there is still pixels to color
        tmp=np.where(sizes>0)[0]
        # tmp[0] is the first color (shade of gray) for which we still have pixel to color
        # Get the tmp[0] color from marks and remove one pixel from marks (pop)
        # color is the watersheds mark color
        (posx,posy),color = marks[tmp[0]].pop()

        # Decrease number of pixels for color tmp[0] (because of the previous pop from marks)
        sizes[tmp[0]]=sizes[tmp[0]]-1

        # !! It's the fact that we always use the color tmp[0] that make the watersheds work correctly. The flood is done from the bottom !!

        for i,j in struct:
            #Calculate structuring element pixel coordinate depending of the position of the mark
            x=posx+i
            y=posy+j

            # if the pixel is in the image
            if x>=0 and x<newImage.shape[0] and y>=0 and y<newImage.shape[1] and newImage[x,y]==0:
                # We add the pixel to the mark for the good shade of gray index to propagate the flood with the good watersheds mark color
                marks[imageG[x,y]].append(((x,y),color))
                # Update the sizes array because we have a new mark
                sizes[imageG[x,y]]+=1
                # And we color the output image
                newImage[x,y]=color
        count +=1

    print ('End of Watersheds')

    return newImage

def myThreshold(image,threshold):
    tmp = np.copy(image)
    tmp[image<threshold]=0
    tmp[image>= threshold] = 255

    return tmp

# Count number of white distinct elements in a black and white image
def countElements(image):
    gamma8 = strel.build("square", 1, None)

    imagePix = np.zeros(image.shape, image.dtype)
    count = 0
    for i in range(0, image.shape[0]):
        for j in range(0, image.shape[1]):
            if image[i, j] == 255:
                imagePix[i, j] = 255
                recInf = myReconInf(imagePix, image, gamma8)
                count += 1
                image = image - recInf

    return count

# Remove all white elements connected to the edge of the image on a black and white image
def filterElementsOnEdge(image):
    gamma8 = strel.build("square", 1, None)

    imagePix = np.zeros(image.shape, image.dtype)
    for i in range(0, image.shape[0]):
        for j in range(0, image.shape[1]):
            if image[i, j] == 255 and (i == 0 or i == image.shape[0] - 1 or j == 0 or j == image.shape[1] - 1):
                imagePix[i, j] = 255
                recInf = myReconInf(imagePix, image, gamma8)
                image = image - recInf

    return image











