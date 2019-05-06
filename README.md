# kotlin-native-test

work in progress using kotlin native on linux/windows. 

growing pains
* v0.3.4 - limited math module, so I'm using sin/cos lookup tables.
* v0.4 - unable to compile time(null), and runtime error on exit.
* v0.5 - cinterop fails 
* v0.6 - all working :+1:
* v0.7.1 - working
* v0.8 - rough upgrade 
        - problems discovering dll's -- installed CLion, it started working...
        - Singleton objects are frozen after creation, and shared between threads
            a good idea, but results in kfun:konan.worker.InvalidMutabilityException
            solution: no trivial singletons
* v0.9 - shape of int, uint, short changed. 
* v1.3.0-rc-146


vscode
* ctrl-B to build
* F5 to run (uses cpp extension)

prototype

use the embedded cppdemo


