import cv2
from myLibs.ImageProcessing.MorphologicalImageProcessing import structElement as strel, morpho

image = cv2.imread('Images/numbers.png')
morpho.displayImage("Original", image)

struct= strel.build("disc", 5, None)
open= morpho.myOpen(image, struct)
morpho.displayImage("Open", open)

image=image-open

image = morpho.myThreshold(image,50)

morpho.displayImage("Final Image", image)

key = cv2.waitKey(0)
cv2.destroyAllWindows()