#!/bin/bash
 
ps -ef | grep "node" | awk '{print $2}' | xargs kill -9

done