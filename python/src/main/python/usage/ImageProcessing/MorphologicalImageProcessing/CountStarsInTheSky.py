import cv2
import numpy
from myLibs.ImageProcessing.MorphologicalImageProcessing import structElement as strel, morpho


image = cv2.imread('Images/comete.jpg')
image = image[:,:,0]

morpho.displayImage("Original Image", image)
gamma8 = strel.build("square", 1, None)

disc = strel.build("disc", 3, None)

relativeIntensity = 50

image = image-morpho.myHMax(image,relativeIntensity,disc)

#morpho.displayImage("Filtrage Faible Intensite", image)

threshold = 50
for i in range(0, image.shape[0]):
    for j in range(0, image.shape[1]):
        if image[i][j] < 50:
            image[i][j] = 0
        else:
            image[i][j] = 255

morpho.displayImage("After threshold", image)
print("press a key to start counting stars")
key = cv2.waitKey(0)
print("counting started. It will take some time. Image size is "+str(image.shape[0])+" x "+str(image.shape[1]))

imagePix = numpy.zeros(image.shape, image.dtype)
count = 0
for i in range(0, image.shape[0]):
    if(i%10 == 0):
        print(str((float(i)/image.shape[0])*100)+"%")
    for j in range(0, image.shape[1]):
        if image[i, j] == 255:
            imagePix[i, j] = 255
            recInf = morpho.myReconInf(imagePix, image, gamma8)
            count += 1
            image = image - recInf


print(str(count)+" stars in the sky :D")

key = cv2.waitKey(0)
cv2.destroyAllWindows()