import cv2
import numpy as np
from myLibs.ImageProcessing.MorphologicalImageProcessing import structElement as strel, morpho, myimage

image = cv2.imread('Images/bloodcells.png')
disc = strel.build("disc", 5, None)
gamma8 = strel.build("square", 1, None)
gamma4 = strel.build("diamond", 1, None)

square2 = strel.build("square", 2, None)

gamma8list = strel.build_as_list("square", 1, None)

morpho.displayImage("Original Image", image)
image = image[:,:,0]
relativeIntensity = 50

couleurMarqueur = (20,20,200)

image2 = image-morpho.myHMax(image,relativeIntensity,disc)

image2 = morpho.myThreshold(image,relativeIntensity)


border = np.zeros(image.shape, image.dtype)
border[0,:]=255
border[border.shape[0]-1,:]=255
border[:,0]=255
border[:,border.shape[1]-1]=255

image3=morpho.myReconInf(border,image,gamma8)

image3 = morpho.myThreshold(image3,90)

recons=image3

image3=morpho.myDilat(image3,gamma8)

#morpho.displayImage("Test 2.4 : Reconstruction + seuil + erode", image3)

for i in range(0, image2.shape[0]):
    for j in range(0, image2.shape[1]):
        if image2[i][j] == 255 and image3[i][j] == 0:
            image2[i][j] = 0
        else:
            image2[i][j] = 255

image2=morpho.myErode(image2,gamma8)
morpho.displayImage("Cells nucleus", image2)

recons = morpho.myErode(recons,square2)
marksList=[]
marksList.append(recons)

image2=255-image2
imagePix = np.zeros(image.shape, image.dtype)
for i in range(0, image2.shape[0]):
    for j in range(0, image2.shape[1]):
        if image2[i, j] == 255:
            imagePix[i, j] = 255
            recInf = morpho.myReconInf(imagePix, image2, gamma8)
            image2 = image2 - recInf
            marksList.append(recInf)
            
image2=morpho.watersheds(morpho.myGrad(image,gamma4),marksList,gamma8list)
objectsNumber=len(marksList)

for i in range(0, image2.shape[0]):
    for j in range(0, image2.shape[1]):
        if image2[i,j]>1:
            image2[i,j]= image2[i,j] * ((255 - 30) / (objectsNumber + 1)) + 30
        else :
            image2[i, j]=0

coloredImage=cv2.applyColorMap(image2, cv2.COLORMAP_JET)
morpho.displayImage("Watersheds",coloredImage)

while(1):
    image = cv2.imread('Images/bloodcells.png')
    print ('Select first cell')
    pointMarks = myimage.display_and_click_image(image,couleurMarqueur)
    marks1 = np.zeros([image.shape[0],image.shape[1]],np.dtype)
    for mark in pointMarks:
        marks1[mark[0],mark[1]] = 255

    print ('Select second cell')
    pointMarks = myimage.display_and_click_image(image,couleurMarqueur)
    marks2 = np.zeros([image.shape[0],image.shape[1]],np.dtype)
    for mark in pointMarks:
        marks2[mark[0],mark[1]] = 255

    print ('Calculating distance (can take few seconds)')

    colorCel1 = []
    for i in range(0, marks1.shape[0]):
        for j in range(0, marks1.shape[1]):
            if marks1[i,j]==255 and image2[i,j] not in colorCel1:
                colorCel1.append(image2[i,j])
    #tmp1 = np.where(image2 in colorCel1)[0]

    colorCel2 = []
    for i in range(0, marks2.shape[0]):
        for j in range(0, marks2.shape[1]):
            if marks2[i,j]==255 and image2[i,j] not in colorCel2:
                colorCel2.append(image2[i,j])
    #tmp2 = np.where(image2 in colorCel2)[0]

    dist=image2.shape[0]*image2.shape[0]
    for i in range(0, image2.shape[0]):
        for j in range(0, image2.shape[1]):
            if image2[i,j] in colorCel1 :
                for k in range(0, image2.shape[0]):
                    for l in range(0, image2.shape[1]):

                        if image2[k, l] in colorCel2:
                            distTmp=np.sqrt(np.square(i-k)+np.square(j-l))
                            if distTmp<dist:
                                dist=distTmp

    print("Distance between two cells : "+str(dist))


key = cv2.waitKey(0)
cv2.destroyAllWindows()