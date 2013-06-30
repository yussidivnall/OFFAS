#!/usr/bin/python	
import sys;
import socket;
import simplejson;
import Gnuplot,Gnuplot.funcutils;
import shlex,subprocess
from numpy import *;

port=234
plot_options='set style fill solid'
x_data_points=[];
y_data_points=[];
z_data_points=[];

x_initial=-1;
y_initial=-1;
g=Gnuplot.Gnuplot(debug=1)


def set_plot_options():
	global plot_options
	g(plot_options);
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


def update_graph(json_obj):
	
	global g
        global x_data_points;
        global y_data_points;
        global z_data_points;
	global x_initial;
	global y_initial;
	#if(json_obj["sensor_type"]==1):
		#AK8976A 3-axis Accelerometer
		#print""

	xt=[json_obj["orientation_matrix"][0]][0]
        yt=[json_obj["orientation_matrix"][1]][0]
        zt=[json_obj["orientation_matrix"][2]][0]
	if (x_initial==-1):
		x_initial==xt
		y_initial==yt
	move_pointer(xt,zt);
"""
	xt=[json_obj["orientation_matrix"][0]]
        yt=[json_obj["orientation_matrix"][1]]
        zt=[json_obj["orientation_matrix"][2]]
        x_data_points=x_data_points+xt
        y_data_points=y_data_points+yt
        z_data_points=z_data_points+zt
        g.splot(x_data_points,y_data_points,z_data_points )
"""
def process_json(json_str):
	#print json_str
	json_obj = simplejson.loads(json_str);
	#print json_obj["time"],json_obj["orientation_matrix"]
	update_graph(json_obj)

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
	set_plot_options();
	listen_socket();
main()
