import cv2
import numpy as np
from myLibs.ImageProcessing.MorphologicalImageProcessing import structElement as strel, morpho, myimage

imagename='Images/chromosomes.tif'
image = cv2.imread(imagename)[:,:,0]
disc = strel.build("disc", 5, None)
gamma8 = strel.build("square", 1, None)


morpho.displayImage("Original Image", image)

count = morpho.countElements(image)

while count > 4:
    image = morpho.myDilat(image,disc)
    count = morpho.countElements(image)

print("Clusters identified")

imagePix = np.zeros(image.shape, image.dtype)
imageFin = np.zeros(image.shape, image.dtype)

idcluster=0
for i in range(0, image.shape[0]):
    for j in range(0, image.shape[1]):
        if image[i, j] == 255:
            color = (idcluster+1)*50
            imagePix[i, j] = 255
            recInf = morpho.myReconInf(imagePix, image, gamma8)
            idcluster += 1

            image = image - recInf
            imageFin=imageFin+(recInf/255 * color)

            print("Clusters "+str(idcluster)+" : color : "+str(color))

image = cv2.imread(imagename)[:,:,0]
image=image/255*imageFin

image = cv2.applyColorMap(image, cv2.COLORMAP_JET)

morpho.displayImage("Clusters", image)

key = cv2.waitKey(0)
cv2.destroyAllWindows()
