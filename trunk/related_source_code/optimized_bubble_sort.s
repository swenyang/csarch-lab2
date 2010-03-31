#swi 1		for system halt
#swi 2 		for output memory
#$1, $2 for swi, $3 = Memory base, $4 = length, $0 = 0
#$5 = i, $6 = j, $7 = number1, $8 = number2, $9 = temp
#$10 = length - 2, $11 = length - i - 1
#$12 = Memory[j - 1]

DATA SEG:
	10 5 8 3 2 1 9 4 6 0 7
DATA END


CODE SEG:
	addi $0, $3, 512
	lw $4, 0($3)
	addi $3, $1, 4
	addi $0, $0, $5
	addi $4, $10, -2
	add $0, $4, $2
	swi 2

outer:
	slet $5, $10, $9
	beqz $9, exit

	sub $4, $5, $11
	add $0, $3, $12
	addi $0, $6, 1 	
	addi $12, $12, 4
	addi $11, $11, -1

inner:
	slet $6, $11, $9
	beqz $9, next_i
	
	lw $7, 4($12)	
	lw $8, 0($12)
	addi $6, $6, 1
	addi $12, $12, 4
	slet $7, $8, $9
	beqz $9, next_j
	
	add $0, $8, $9
	add $0, $7, $8
	sw $8, 0($12)
	add $0, $9, $7
	sw $7, 4($12)

next_j:
	jump inner

next_i:
	addi $5, $5, 1
	jump outer

exit:
	addi $3, $1, 4
	add $0, $4, $2
	swi 2
	swi 1 
CODE END

