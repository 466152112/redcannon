#!/bin/bash

uname | grep -iq cygwin
if [ $? -eq 0 ]
then
	sep=';'
else
	sep=':'
fi

CLASSPATH=bin
for f in lib/*.jar
do
	CLASSPATH+=$sep$f
done

