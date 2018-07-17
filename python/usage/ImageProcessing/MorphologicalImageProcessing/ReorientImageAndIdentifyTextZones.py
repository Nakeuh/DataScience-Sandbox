import cv2
import numpy as np
from myLibs.ImageProcessing.MorphologicalImageProcessing import structElement as strel, morpho, myimage

imagename='Images/feuille1.png'
image = cv2.imread(imagename)
disc = strel.build("disc", 5, None)
gamma8 = strel.build("square", 5, None)
gamma4 = strel.build("diamond", 1, None)
gamma8list = strel.build_as_list("square", 1, None)

morpho.displayImage("Original Image", image)

# Unify background
image = 255-image[:,:,0]

image= image- morpho.myOpen(image,gamma8)
image= morpho.myThreshold(image,60)

morpho.displayImage("Image threshold", image)

# Seeking orientation 
sums=[]
imageGrad=np.zeros(image.shape,image.dtype)
for angle in range (0 ,90):
    line = strel.build("line", 100, angle)
    imageGrad=morpho.myOpen(image,line)
    sums.append((sum(map(sum,image-imageGrad)),angle))

orient=min(sums[:])[1]-90
print("orientation : "+str(orient))


imageOr = cv2.imread(imagename)
imageOr=255-imageOr[:,:,0]
line = strel.build("line", 10, orient)
imageline = morpho.myOpen(imageOr,line)
line = strel.build("line", 10, orient+90)
imageline = np.maximum(imageline,morpho.myOpen(imageOr,line))

morpho.displayImage("lines", imageline)

image=imageOr-imageline

morpho.displayImage("Image without lines", image)

image = morpho.myThreshold(image,50)
morpho.displayImage("Image + threshold", image)

imageorigin = cv2.imread(imagename)
for i in range(0, image.shape[0]):
    for j in range(0, image.shape[1]):
        if image[i][j] >0:
            imageorigin[i][j][2] = 255

imageorigin=myimage.rotate(imageorigin,-orient)

morpho.displayImage("Result", imageorigin)

key = cv2.waitKey(0)
cv2.destroyAllWindows()