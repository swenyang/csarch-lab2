#include<stdio.h>
#include<stdlib.h>

int main(int argc, char* args[]){
	FILE* file = fopen("matrix.int", "r");
	FILE* output = fopen("matrix.bin", "wb");
	char str[20];
	int num;
	while(fscanf(file, "%s", str) != EOF){
		num = atoi(str);
		printf("%d\n", num);
		fwrite(&num, sizeof(int), 1, output);
	}
	fclose(file);
	fclose(output);
	return 0;
}


