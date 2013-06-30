#include <stdio.h>
#include <iostream>
#include <string.h> 
//#include <X11/Xlib.h>
//#include <X11/extensions/XTest.h>
#include <ctype.h>
#include <stdlib.h>
using namespace std;

//ref: http://cboard.cprogramming.com/c-programming/108383-using-fread-stdin.html
//Must be a better way then this, don't rmemeber/know c++ that well!
string nextline(FILE * f){
    string ret;
    char buf[256] = {0};
    int i = 0;
    char t;
    
    fread( &t, 1, 1, f);
    while (t != '\n' )
    {
        buf[i++] = t;
        fread( &t, 1, 1, f);
    }
    ret = buf;
    return ret;
}


int parseargs(string s){
	if (wmemcmp("Hello",s)==0){
		fprintf("Don't you HELLO me you bastard");
	}
	return -1;
}

int main(int argc, char* argv[])
{
//   Display *dpy = XOpenDisplay (0);
   string l;
   while(l!="exit"){
	l=nextline(stdin);
	int action=parseargs(l);	
   }
//   XCloseDisplay(dpy);
   return 0; 
}
