#!/usr/bin/python	
import sys;
import socket;
import simplejson;
import Gnuplot,Gnuplot.funcutils;
from numpy import *;

port=234
plot_options='set style fill solid'



x_data_points=[];
y_data_points=[];
z_data_points=[];
g=Gnuplot.Gnuplot(debug=1)
max_points=200

def set_plot_options():
	global plot_options
	g(plot_options);
def update_graph(json_obj):
	
	global g
        global x_data_points;
        global y_data_points;
        global z_data_points;
	global mat_points;
	xt=[json_obj["rotation_matrix"][0]]
       	yt=[json_obj["rotation_matrix"][1]]
	zt=[json_obj["rotation_matrix"][2]]
	x_data_points=x_data_points+xt
	y_data_points=y_data_points+yt
	z_data_points=z_data_points+zt
	if(len(x_data_points)>max_points): #too many points trucating
		buff=[]
		buff=x_data_points[len(x_data_points)-max_points:len(x_data_points)]
		x_data_points=buff
		buff=y_data_points[len(y_data_points)-max_points:len(y_data_points)]
                y_data_points=buff
		buff=z_data_points[len(z_data_points)-max_points:len(z_data_points)]
                z_data_points=buff
	g.splot(x_data_points,y_data_points,z_data_points )
		#g.splot(xt,zt)
def process_json(json_str):
	#print json_str
	json_obj = simplejson.loads(json_str);
	print json_obj["time"],json_obj["orientation_matrix"]
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
