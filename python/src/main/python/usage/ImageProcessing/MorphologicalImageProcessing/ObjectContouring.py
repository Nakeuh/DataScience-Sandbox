import cv2
import numpy as np
from myLibs.ImageProcessing.MorphologicalImageProcessing import structElement as strel, morpho, myimage

image = cv2.imread('Images/flower.jpg')

colorMarks = (20,20,200)


marksList=[]

while 1:
    print ('Select object')
    pointMarks = myimage.display_and_click_image(image,colorMarks)
    if len(pointMarks)>0 :
        marks = np.zeros([image.shape[0],image.shape[1]],np.dtype)
        for mark in pointMarks:
            marks[mark[0],mark[1]] = 255
        marksList.append(marks)
    else :
        print ('End of Selection')
        break

image = image[:,:,0]

gamma8= strel.build("square", 1, None)
gamma8list= strel.build_as_list("square", 1, None)

gradient = morpho.myGrad(image,gamma8)

watershed = morpho.watersheds(gradient,marksList,gamma8list)

numberObjects=len(marksList)

watershed=watershed*(255/(numberObjects+1))

image = cv2.imread('Images/flower.jpg')
morpho.displayImage("Original image", image)
morpho.displayImage("After Watersheds", watershed)

key = cv2.waitKey(0)
cv2.destroyAllWindows()