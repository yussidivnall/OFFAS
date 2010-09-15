OFfA- Orientation Functionality for Android

A simple interface for Android's Sensors, uses sensor events and writes them as xml to a socket, to be used by other application, can be used to override the mouse, or through Blender python or an array of other application

At the moment only orientation is implemented, and it uses an officialy deprecated but still usable onSensorChange().

Usgae:
On any networked machine
netcat -lp 234
bring up Offa
set address to machine's address
connect

you should see a stream of xml statements about the current sensor orientation.
to run on a phone, it must be rooted, and with netcat or any other application that listen on the correct port and understands the data

to use through the USB cable, so far I use a hack, i start nc on debian through the machine

i have a script called sensor.sh which handles this, and i use: adb shell "/sd-ext/sensor.sh

#!/system/bin/sh
#sensor.sh
mount -o bind /sdcard /sd-ext/debian/media/sdcard
mount -t sysfs sysfs /sd-ext/debian/sys
mount -t devpts devpts /sd-ext/debian/dev/pts
mount -t proc proc /sd-ext/debian/proc
export HOME=/root
export TERM=linux
export USER=root
#sysctl -w net.ipv4.ip_forward=1
chroot /sd-ext/debian /bin/nc -lp 234
#This is actually stolen from some rooting guide but i don't remember which

then offa on 127.0.0.1 port 234

there's probably a better way to do this, let me know if you think of one

bugs:
there are a few, sometimes it crashes unexpectadly, I don't handle the socket open/close right, so for example if you press connect, but don't recieve a "connected" message don't press disconnect. actually connect automatically disconnect old connection without crashing for some reason.

Usage conditions:
You are not allowed to implement a windows client to this, this is beacuse i f**kin hate windows, and despise all windows users, and i will never (again) make an application for windows, if you are Micro$oftly disabled a recommend debian.org, if you don't want to stop using windows, well you're a twat and no one loves you.
Also, OK it's a bit tacky , but don't be evil! aside from that, just credit me somewhere in the documentation and you can do what you want with it, and have fun.
Oh and if you happen to make a bit of money using this, well I'm broke, really really broke so every penny will help me out a lot, but this is not a condition, just a plea for charity. Thanks

UC.dev.null@gmail.com
