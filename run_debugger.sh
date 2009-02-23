#!/bin/bash

source set_classpath.sh
java -Xmx1024m -cp $CLASSPATH org.stepinto.redcannon.ai.log.SearchDebugger $*

