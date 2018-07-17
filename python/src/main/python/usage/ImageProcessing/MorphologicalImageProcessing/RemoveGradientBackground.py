import cv2
from src.main.python.myLibs.ImageProcessing.MorphologicalImageProcessing import structElement as strel, morpho

imagePath = '../../../../resources/images/numbers.png'
image = cv2.imread(imagePath)
morpho.displayImage("Original", image)

struct= strel.build("disc", 5, None)
open= morpho.myOpen(image, struct)
morpho.displayImage("Open", open)

image=image-open

image = morpho.myThreshold(image,50)

morpho.displayImage("Final Image", image)

key = cv2.waitKey(0)
cv2.destroyAllWindows()