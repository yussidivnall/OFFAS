#!/usr/bin/python	
import math;
import socket;
import simplejson;
import Gnuplot,Gnuplot.funcutils;
from numpy import *;

port=234
plot_options='set style fill solid; set style lines 10;set style data linespoints; set title "acceleration time graph";set xlabel "time"; set ylabel "acceleration"; set terminal x10 0xff0000'



data_points=[];

g=Gnuplot.Gnuplot(debug=1)
max_points=200
start_time=-1
def set_plot_options():
	global plot_options
	g(plot_options);
def update_graph(json_obj):
	
	global g
        global data_points;
	global mat_points;
	global start_time;
	if(json_obj["sensor_type"]==1):  #AK8976A 3-axis Accelerometer
		xt=[json_obj["values"][0]]
        	yt=[json_obj["values"][1]]
	        zt=[json_obj["values"][2]]
		time=json_obj["time"]
		if(start_time==-1): start_time=time
		time=time-start_time
		mag=[time,math.sqrt(xt[0]*xt[0]+yt[0]*yt[0]+zt[0]*zt[0])-9.74]
		data_points.append(mag)
		if(len(data_points)>max_points): #too many points trucating
			buff=[]
			buff=data_points[len(data_points)-max_points:len(data_points)]
			data_points=buff
		
	        g.plot(data_points)
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
	#parse_arguments();
	set_plot_options();
	listen_socket();
main()
