---------file explanations-------
myisa.def:
	instructions' definition, include 
	add, addi, sub, mult, lw, sw, slt, beqz, jump, lwr, swr
	

myisa.h
	modified the definition of IMME, for convenience (mostly for addi negative) 
		I extend immediate with symbol;
	and I add the following two definitions:
		JUMP_ADDR   get the jump address of jump instruction,
					from 25-0
		BEQZ_ADDR  	get the beqz address of beqz instruction,
					from 20-0
					
					

to simulate binary files, just put the two files in directory "target-myisa" 
with myisa.c and then make them
