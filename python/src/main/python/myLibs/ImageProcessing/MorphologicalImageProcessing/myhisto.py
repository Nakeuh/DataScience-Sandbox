import cv2
import numpy as np

# return new image with equalized histogram
def equalizeHist(image):
    newImage = np.zeros(image.shape,image.dtype)
    # calcHist : image, channel, masque, histSize, ranges
    histo = cv2.calcHist([image], [0], None, [256], [0, 256])
    nbCase = image.shape[0]*image.shape[1]

    for i in range(0,image.shape[0]):
        for j in range(0,image.shape[1]):
            greyScale = image[i,j]
            sum = np.sum(histo[0:greyScale])
            newImage[i,j]=sum*255/nbCase

    return newImage