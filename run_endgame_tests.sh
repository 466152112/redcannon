#!/bin/bash

source set_classpath.sh
java -server -Xmx1024m -cp $CLASSPATH org.stepinto.redcannon.ai.test.EndgameTest $*

