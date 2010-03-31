#procedure matrix multiplication(matrix A, matrix B)
#C(m, n) <- A(m, l) * B(l, n)
#for i <- 0 to m - 1
#{
#	for j <- 0 to n - 1
#	{
#		c[i][j] <- 0
#		for k <- 0 to l - 1
#			c[i][j] <- c[i][j] + a[i][k] * b[k][j]
#	}
#}

#$0 = 0, $1 = A base, $2 = B base, $3 = C base
#$4 = m - 1, $5 = n - 1, $6 = l - 1
#$7 = i, $8 = j, $9 = k
#$10 = temp, $11 = temp, $12 = temp

#for instance
#     [1 2 3]       [1 2]     [22 28]
# A = [4 5 6] * B = [3 4]  = C[49 64]
#                   [5 6] 

DATA SEG:
	2 3 1 2 3 4 5 6
	3 2 1 2 3 4 5 6
	1 1 1 1
DATA END

CODE SEG:
	addi $0, $1, 512       #matrix A base
	lw $4, 0($1)
	addi $4, $4, -1
	lw $6, 4($1)
	addi $6, $6, -1
	addi $1, $1, 8

	addi $0, $2, 544        #matrix B base
	lw $5, 4($2)
	addi $5, $5, -1
	addi $2, $2, 8
	
	addi $0, $3, 576       #matrix C base
	
	add $0, $0, $7         #0x00000280, 32
first_loop:
	slet $7, $4, $10
	beqz $10, exit
	
	add $0, $0, $8
second_loop:
	slet $8, $5, $10
	beqz $10, next_i
	
	addi $5, $12, 1         #c[i][j] = 0
	mult $12, $7, $10
	add $10, $8, $10
	addi $0, $13, 4
	mult $13, $10, $10
	swr $3, $10, $0
	
	add $0, $0, $9         #0x000002b0, 44
third_loop:
	slet $9, $6, $10
	beqz $10, next_j
	
	addi $6, $12, 1          #a[i][k]
	mult $7, $12, $10
	add $10, $9, $10
	addi $0, $13, 4
	mult $13, $10, $10
	
	addi $5, $13, 1          #b[k][j]
	mult $9, $13, $11
	add $11, $8, $11
	addi $0, $13, 4
	mult $13, $11, $11
	
	lwr $1, $10, $10         #0x000002e4 ,57
	lwr $2, $11, $11
	mult $10, $11, $11       #a[i][k] * b[k][j]
	
	addi $5, $12, 1           #c[i][j]
	mult $7, $12, $10
	add $10, $8, $10
	addi $0, $13, 4
	mult $13, $10, $10
	
	lwr $3, $10, $12        #c[i][j] <- c[i][j] + a[i][k] * b[k][j]
	add $12, $11, $11
	swr $3, $10, $11
	
	addi $9, $9, 1
	jump third_loop

next_j:
	addi $8, $8, 1
	jump second_loop
	
next_i:
	addi $7, $7, 1
	jump first_loop
	
exit:
	add $0, $3, $1
	addi $0, $2, 4
	swi 2
	
	swi 1
CODE END


