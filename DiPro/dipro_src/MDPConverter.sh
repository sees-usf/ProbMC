

#!/bin/sh

LIB=ext/lib
cpath=bin:$LIB/prism.jar:$LIB/pepa.zip:$LIB/batik.jar:$LIB/forms.jar:$LIB/gnujaxp:jar:itext-2.0.jar:$LIB/jcommon-1.0.12.jar:$LIB/jfreechart-1.0.9.jar:$LIB/jfreechart-1.0.9-experimental.jar:$LIB/jfreechart-1.0.9-swt.jar:$LIB/looks.jar:$LIB/servlet.jar:$LIB/swtgraphics2d.jar:$LIB/y.jar:$LIB/ysvg.jar:$LIB/mysql-connector-java-5.1.7-bin.jar

class="dipro.run.MDP2DTMC"

echo "java -Xmx1536m -Xss64M "$class  $*
java -Xmx1536m -Xss64M -classpath $cpath $class $*
