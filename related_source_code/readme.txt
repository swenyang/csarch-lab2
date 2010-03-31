---------files explanations---------
instructions: 
	include instructions defined in this project,
	and instructions' format, content, etc
	
	
Generator.java:
	used to transform assembly file to machine instructions,
	however, for writing binary file in Java is little-endian, 
	first I have to write instructions as integers in ".int" file, 
	then use "compiler.cpp" to create big-endian binary file.
	
	
compiler.cpp:
	used to create binary file, taking advantage of Generator.java,
	as demonstrated above.
	
	
bubble_sort.s
	assembly code for bubble sort program
	
	
bubble_sort.int
	integer type of instructions of bubble sort program
	
	
bubble_sort.bin
	binary file of bubble sort program
	
	
matrix.s
	assembly code for matrix multiplication program
	
	
matrix.int
	integer type of instructions of matrix multiplication program
	
	
matrix.bin
	binary file of matrix multiplication program
