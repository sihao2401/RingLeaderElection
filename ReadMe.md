# Simulation of Ring Leader Election using Java

This code simulate the Variable Speeds algorithm in a synchronous ring. No process knows the value of n. The code (algorithm) executed by all (the n newly created threads) must be the same.

The input for this problem consists of:
1. n (the number of processes of the distributed system which is equal to the number of threads to be created 
2. one array id[n] of size n; the i th element of this array gives the unique id of the i th process or i th thread. The i th process accesses the i th element of array id[], and finds its unique id.


1.Open Termial tool, change the directory to zipped file folder.
2.Use command  ```javac MainThread.java``` to compile the code.
3.Use command ```java MainThread > output.dat``` to run this program. 
4.The script file "output.dat" will be saved in current directory.
