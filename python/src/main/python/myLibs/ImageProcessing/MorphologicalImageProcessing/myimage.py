import cv2
import numpy as np

# Callback mouse function
# event : mouse event
# x,y : mouse coordinates
# flags : mouse state during the event
# listresult : list that contains the image, the color to color pixels and a result list that will store
# colored pixels coordinates
# work with python 2 but not in python 3
def click_and_keep(event, x, y, flags, (imagecolor, color, listresult)):
    # if the left mouse button was clicked, record the starting
    # (x, y) coordinates and indicate that cropping is being
    # performed

    if event == 0 and flags == 1:
    #if event == 0 and flags == 33:
        #if flags == cv2.EVENT_FLAG_LBUTTON:
        imagecolor[y - 1:y + 1, x - 1:x + 1] = color
        cv2.imshow("Image Selection", imagecolor)
        listresult.append((y,x))



# Function to display image and get pixels selected by user
def display_and_click_image(image, color):
    marks = []
    im = np.copy(image)
    print ('Click on the image to select pixels')

    cv2.namedWindow("Image Selection")
    # Link window to clickandkeep function
    cv2.setMouseCallback("Image Selection", click_and_keep, (im, color, marks))
    cv2.imshow("Image Selection", im)
    key = cv2.waitKey(0)
    cv2.destroyAllWindows()

    return marks


# Rotate the image
# from http://stackoverflow.com/questions/9041681/opencv-python-rotate-image-by-x-degrees-around-specific-point
def rotate(image, angle_degre):
    if(len(image.shape) == 2):
        (oldY,oldX) = image.shape #note: numpy uses (y,x) convention but most OpenCV functions use (x,y)
    else:
        (oldY,oldX, t) = image.shape #note: numpy uses (y,x) convention but most OpenCV functions use (x,y)
    M = cv2.getRotationMatrix2D(center=(oldX/2,oldY/2), angle=angle_degre, scale=1.0) #rotate about center of image.

    #include this if you want to prevent corners being cut off
    r = np.deg2rad(angle_degre)
    newX,newY = (abs(np.sin(r)*oldY) + abs(np.cos(r)*oldX),abs(np.sin(r)*oldX) + abs(np.cos(r)*oldY))

    #the warpAffine function call, below, basically works like this:
    # 1. apply the M transformation on each pixel of the original image
    # 2. save everything that falls within the upper-left "dsize" portion of the resulting image.

    #So I will find the translation that moves the result to the center of that region.
    (tx,ty) = ((newX-oldX)/2,(newY-oldY)/2)
    M[0,2] += tx #third column of matrix holds translation, which takes effect after rotation.
    M[1,2] += ty

    rotatedImg = cv2.warpAffine(image, M, dsize=(int(newX),int(newY)))
    return rotatedImg