#include "cv.h"
//#include "highgui.h"
#include <stdio.h>
#include <ctype.h>
#include <stdlib.h>

#include <X11/Xlib.h>
#include <X11/extensions/XTest.h>

void usage(){
	fprintf(stderr,"Usage: xtesttool <x> <y>\n");
	fprintf(stderr,"Positions mouse pointer at x,y \n");
}

int main(int argc, char* argv[] ){
	Display *dpy = XOpenDisplay (0);
	if (argc < 3){
		usage();
		exit(1);
	}
	int x=atoi(argv[1]);
	int y=atoi(argv[2]);
	if (!x || !y){
		usage();
		return -1
	}
	

	//Only works with this loop, why?
	for (int i=0; i< 500; i++){
		//XTestFakeRelativeMotionEvent(dpy,screenx,screeny,0.2);
		XTestFakeMotionEvent(dpy,DefaultScreen(dpy),x, y, 0);
	}
	return 0;
	}
