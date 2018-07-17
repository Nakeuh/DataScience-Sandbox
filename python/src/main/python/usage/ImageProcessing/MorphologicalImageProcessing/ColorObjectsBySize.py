import cv2
import numpy
from myLibs.ImageProcessing.MorphologicalImageProcessing import structElement as strel, morpho

# return width of the bigest grain in the image
def biggestGrain(image):
    for width in range(1, 100):
        struct = strel.build("disc", width, None)
        openImage = morpho.myOpen(image, struct)
        sums = sum(map(sum, openImage))
        if sums == 0:
            break

    return width*2

# convert a width to a length (ok it's not the best function of the world)
def lengthGrain(width):
    return width*2.7

# return width and length of the first grain in the image
def firstGrainSizes(image):
    width = biggestGrain(image)
    return(width,lengthGrain(width))

# return width and length of all the grain in the image
def allGrainsSizes(image):
    gamma8 = strel.build("square", 1, None)
    outputImage = numpy.zeros(image.shape,image.dtype)
    imagePix = numpy.zeros(image.shape, image.dtype)
    mesures = []
    for i in range(0, image.shape[0]):
        for j in range(0, image.shape[1]):
            if image[i, j] == 255:
                imagePix[i, j] = 255
                recInf = morpho.myReconInf(imagePix, image, gamma8)
                image = image - recInf
                (width,length) = firstGrainSizes(recInf)

                outputImage=outputImage+(recInf/255 *(255-width*10))
                mesures.append((i,j,width,length))


    return (outputImage,mesures)

image = cv2.imread('Images/rice.png')
morpho.displayImage("Original Image", image)

image = image[:,:,0]

gamma8= strel.build("square", 1, None)

struct = strel.build("disc", 5, None)

open = morpho.myOpen(image, struct)

image = image - open

image = morpho.myThreshold(image,50)

morpho.displayImage("Image filtered", image)

image = morpho.filterElementsOnEdge(image)

morpho.displayImage("Filter sides", image)

image=allGrainsSizes(image)[0]
morpho.displayImage("Not Colored", image)

image = cv2.applyColorMap(image, cv2.COLORMAP_JET) # work on python 2 but not on python 3
morpho.displayImage("Colored By Width", image)


key = cv2.waitKey(0)
cv2.destroyAllWindows()