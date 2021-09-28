#!/bin/bash

rm -f srn-2s.output
rm -f constraints_output.smt

for ((i=1; i<=$1; i++));
do
	python3 main.py srn-2s $i
done
