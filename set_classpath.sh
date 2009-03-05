#!/bin/bash

uname | grep -iq cygwin
if [ $? -eq 0 ]
then
	swt=swt-win32.jar
	sep=';'
else
	swt=swt-gtk.jar
	sep=':'
fi

CLASSPATH=bin${sep}lib/$swt
for f in lib/*.jar
do
	CLASSPATH+=$sep$f
done

