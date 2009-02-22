#!/bin/bash

source set_classpath.sh
java -cp $CLASSPATH org.stepinto.redcannon.ai.test.EndgameTest $*

