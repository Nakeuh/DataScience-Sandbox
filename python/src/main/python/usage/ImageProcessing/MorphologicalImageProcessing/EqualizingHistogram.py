import cv2
from src.main.python.myLibs.ImageProcessing.MorphologicalImageProcessing import morpho, myhisto

imagename = "../../../../resources/images/chat.jpg"

image = cv2.imread(imagename)

morpho.displayImage("Original Image", image)

# Objectif : reequalize only the light

# Use of the colorspace HSV (Hue, Saturation, VLight? :D)
image = cv2.cvtColor(image,cv2.COLOR_BGR2HSV)
image[:,:,2]=myhisto.equalizeHist(image[:,:,2])

image = cv2.cvtColor(image,cv2.COLOR_HSV2BGR)
morpho.displayImage("Image equalized", image)

key = cv2.waitKey(0)
cv2.destroyAllWindows()