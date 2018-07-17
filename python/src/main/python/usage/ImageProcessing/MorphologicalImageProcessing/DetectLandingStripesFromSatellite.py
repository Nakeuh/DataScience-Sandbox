import cv2
import numpy
from myLibs.ImageProcessing.MorphologicalImageProcessing import structElement as strel, morpho

image = cv2.imread('Images/aeroport2.png')
image = image[:,:,0]
morpho.displayImage("Original", image)

gamma8= strel.build("square", 1, None)

imageFiltered=numpy.ones(image.shape,image.dtype)
imageFiltered=imageFiltered*255

for angle in range (-90,90,1):
    structLigne=strel.build("line",50,angle)
    closeImage=morpho.myClose(image,structLigne)
    imageFiltered=numpy.minimum(imageFiltered,closeImage)

morpho.displayImage("After filter", imageFiltered)


imageFiltered = morpho.myThreshold(imageFiltered,40)

morpho.displayImage("After threshold", imageFiltered)

structLigne = strel.build("square", 2, None)
imageFiltered=morpho.myClose(imageFiltered,structLigne)
morpho.displayImage("After close", imageFiltered)

image=morpho.myGrad(imageFiltered,gamma8)
morpho.displayImage("After gradient", image)

threshold = 128
imageCol=numpy.zeros((image.shape[0],image.shape[1],3))
for i in range(0, image.shape[0]):
    for j in range(0, image.shape[1]):
        if image[i][j] > threshold:
            imageCol[i][j] = [0,0,255]

morpho.displayImage("After Coloring", imageCol)

key = cv2.waitKey(0)
cv2.destroyAllWindows()