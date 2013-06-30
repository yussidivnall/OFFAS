//#include "highgui.h"
#include <stdio.h>
#include <ctype.h>
#include <stdlib.h>
#include <string.h>
#include <strings.h>
#include <X11/Xlib.h>
#include <X11/extensions/XTest.h>
using namespace std;
int LEFT_BUTTON=3;
int RIGHT=2;
int PRESS=4;
int RELEASE=8;
void usage(){
	fprintf(stderr,"Usage: click <left|middle|right> <press|release>\n");
	fprintf(stderr,"fake mouse button \n");
	fprintf(stderr,"eg. click left press\n");
}

int main(int argc, char* argv[] ){
	Display *dpy = XOpenDisplay (0);
	
	if (argc < 3){
		usage();
		exit(1);
	}
	if(strcmp(argv[1],"left")==0  && strcmp(argv[2],"press")==0){
		fprintf(stdout,"Press\n");
		XTestFakeButtonEvent (dpy, 1, True,  CurrentTime);  
	}
	if(strcmp(argv[1],"left")==0  && strcmp(argv[2],"release")==0){
		fprintf(stdout,"release\n");
                XTestFakeButtonEvent (dpy, 1, True,  CurrentTime); 
	}
	
        if(strcmp(argv[1],"right")==0  && strcmp(argv[2],"press")==0){
                fprintf(stdout,"Press\n");
                XTestFakeButtonEvent (dpy, 2, True,  CurrentTime);
        }
        if(strcmp(argv[1],"right")==0  && strcmp(argv[2],"release")==0){
                fprintf(stdout,"release\n");
                XTestFakeButtonEvent (dpy, 2, True,  CurrentTime);
        }
	if(strcmp(argv[1],"middle")==0  && strcmp(argv[2],"press")==0){
                fprintf(stdout,"Press\n");
                XTestFakeButtonEvent (dpy, 2, True,  CurrentTime);
        }
        if(strcmp(argv[1],"middle")==0  && strcmp(argv[2],"release")==0){
                fprintf(stdout,"release\n");
                XTestFakeButtonEvent (dpy, 2, True,  CurrentTime);
        }

	XSync(dpy, 0); 
	return 0;
	}
