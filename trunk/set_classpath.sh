#!/bin/bash

uname | grep -iq 'cygwin'
if [ $? -eq 0 ]
then
	swt=swt-win32.jar
	sep=';'
else
	arch | grep -iq 'x86_64'
	if [ $? -eq 0 ]
	then
		swt=swt-gtk-amd64.jar
	else
		swt=swt-gtk.jar
	fi
	sep=':'
fi

CLASSPATH=bin${sep}lib/$swt
for f in lib/*.jar
do
	CLASSPATH+=$sep$f
done

