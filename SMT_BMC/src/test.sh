#!/bin/bash

rm -f constraintst.smt
rm -f graph.g

for ((i=0; i<=$1; i++));
do
	python3 bmc.py examples/six_rn.py $i 1
done
