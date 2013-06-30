#!/usr/bin/python	
import sys;
import socket;
import simplejson;
import shlex,subprocess
from numpy import *;

port=234
plot_options='set style fill solid'
x_data_points=[];
y_data_points=[];
z_data_points=[];

x_initial=-1;
y_initial=-1;


def move_pointer(x,y):
	global x_initial;
	global y_initial;
	print 
#	if (x_initial ==-1 or y_initial==-1):return
	xi=int(x_initial-x*500)
	yi=int(y_initial-y*500)
        print "%s %s"%(xi,yi)

	args=shlex.split("./xtesttool "+str(500+xi)+" "+str(500+yi))
	stdin=subprocess.Popen(args,shell=False,stdout=subprocess.PIPE).stdout;



def dump(json_obj,fltr):
	return;
def dump_matrix(mat,w,h):
	size=len(mat[0][1]) * len(mat[0]);
	if size!=w*h:
		print "Can't print that";
		return;
	for i in range(0,h):
		for j in range(0,w):
			print mat[i][j]	

def check_facedown(orient):
	print "Facing down";	

def updateState(json_obj):
	
	#print "Orientation:";
	orient =[json_obj["orientation_matrix"]][0]
	print round(float(orient[2]),1)
	if (round(float(orient[2]),1)==3.1 or round(float(orient[2]),1)==-3.1):print "Facing down";
	if (round(float(orient[2]),1)==0):print "Facing up";
	print "%f \n %f \n %f \n " %(orient[0],orient[1],orient[2]);
	
	rotate =[json_obj["rotation_matrix"]]
	#check_facedown(rotate);
	xrot=rotate[0][0];
	yrot=rotate[0][1];
	zrot=rotate[0][2];
	print "Rotation";
	print "%f %f %f" %(xrot[0]*1.0,xrot[1],xrot[2])
	print "%f %f %f" %(yrot[0],yrot[1],yrot[2])
	print "%f %f %f" %(zrot[0],zrot[1],zrot[2])
	#dump_matrix(rotate,3,3);
	print ""

	

def process_json(json_str):
	json_obj = simplejson.loads(json_str);
	updateState(json_obj);

def listen_socket():
	global port;
	HOST='';
	PORT=port;
	bracket_context=0; #Got to be a better way then this
	json_str=""
	s=socket.socket(socket.AF_INET,socket.SOCK_STREAM);
	s.bind((HOST,PORT));
	s.listen(1);
	while 1:	
		conn,addr = s.accept()
		while 1:
			data=conn.recv(1);
			if(not data):break;
			json_str+=data
			if(data=="{"):bracket_context+=1;
			if(data=="}"):bracket_context-=1;
			if(bracket_context==0 and len(json_str) > 0):
				process_json(json_str)
				json_str="";	
		conn.close();

def parse_arguments():
	for arg_num in range(1,len(sys.argv)):
		print sys.argv[arg_num];
def main():
	parse_arguments();
	#set_plot_options();
	listen_socket();
if __name__ == "__main__":main()
