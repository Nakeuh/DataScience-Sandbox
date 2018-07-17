import cv2
from src.main.python.myLibs.ImageProcessing.MorphologicalImageProcessing import structElement as strel, morpho

#imagePath = '../../../../resources/images/papier_60.png'
#imagePath = '../../../../resources/images/papier_15.png'
imagePath = '../../../../resources/images/papier_35.png'

image = cv2.imread(imagePath)

morpho.displayImage("Image", image)

# Keep only the red color (better for these images)
image = image[:, :, 2]

# Test for various different angles
sums = []
for angle in range (-90,90,1):
    structLigne=strel.build("line",40,angle)
    gradImage=morpho.myGrad(image,structLigne)
    sums.append((sum(map(sum,gradImage)),angle))

orient=min(sums[:])[1]

print("Orientation of the object : "+str(orient)+" degree")


key = cv2.waitKey(0)
cv2.destroyAllWindows()
