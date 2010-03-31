import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class Generator {
	static final int DATA_SEG = -1;
	static final int DATA_END = -2;
	static final int CODE_SEG = -3;
	static final int CODE_END = -4;
	static final int JUMP = -5;
	
	static boolean data = false;      //mark if in data segment
	static int[] inst = new int[1000];    //store instructions 
	static int inst_number = 0;
	static int data_number = 2;
	static String instruction;   //used to read a line
	static HashMap<String, Integer> labels = new HashMap<String, Integer>();
	static ArrayList<jump_branch> jumps = new ArrayList<jump_branch>();
	
	public static void main(String[] args) throws IOException{
		File file = new File("matrix.s");
		FileReader fread = new FileReader(file);
		BufferedReader bread = new BufferedReader(fread);

		int temp;

		while(true){
			temp = read_line(bread);
			if(temp == DATA_SEG) break;
		}
		data = true;
		while(true){
			temp = read_line(bread);
			if(temp == DATA_END) break;
		}
		data = false;
		inst[1] = 0x00000200 + (data_number - 2) * 4;
		inst_number = data_number;
		while(true){
			temp = read_line(bread);
			if(temp == CODE_SEG) break;
		}
		while(true){
			temp = read_line(bread);
			if(temp == CODE_END) break;
			inst[inst_number++] = temp;
		}
		inst[0] = inst_number;
		bread.close();
		
		for(int i = 0; i < jumps.size(); i++){
			jump_branch jb = jumps.get(i);
			if(jb.type == 0){
				int addr = labels.get(jb.label);
				addr = 512 + (addr - 1) * 4;
				addr >>= 2;
				addr &= 0x001FFFFF;
				jb.reg <<= 21;
				int j = 0;
				j |= 0x1C000000;
				j |= jb.reg;
				j |= addr;
				inst[jb.offset] = j;
			}
			else{
				int addr = labels.get(jb.label);
				addr = 512 + (addr - 1) * 4;
				addr >>= 2;
				addr &= 0x03FFFFFF;
				int j = 0;
				j |= 0x20000000;
				j |= addr;
				inst[jb.offset] = j;
			}
		}
		
		
		//write instructions into a file
		File output = new File("matrix.int");
		FileWriter fwrite = new FileWriter(output);
		for(int i = 0; i < inst_number; i++)
			fwrite.write(inst[i] + "\n");
		fwrite.close();
		
	}
	
	//read a line and analyze its instruction
	static int read_line(BufferedReader bread) throws IOException{
		instruction = bread.readLine();
		while(instruction.startsWith("#") || instruction.equals("") || instruction.equals("\t"))
			instruction = bread.readLine();
		if(instruction.startsWith("CODE SEG")) return CODE_SEG;
		else if(instruction.startsWith("CODE END")) return CODE_END;
		else if(instruction.startsWith("DATA SEG")) return DATA_SEG;
		else if(instruction.startsWith("DATA END")) return DATA_END;
		else if(instruction.contains(":")){
			labels.put(instruction.substring(0, instruction.indexOf(":")), inst_number - 1);
			instruction = bread.readLine();
		}
		
		if(data){
			String regex = "\\s+";
			String[] str = instruction.split(regex);
			for(int i = 0; i < str.length; i++){
				if(str[i].equals("")) continue;
				inst[data_number++] = Integer.parseInt(str[i]);
			}
			return 0;
		}
		
		
		int i = 0;
		while(instruction.charAt(i) == '\t' || instruction.charAt(i) == ' ') i++;
		if(i != 0) instruction = instruction.substring(i);
		int index = instruction.indexOf(" ");
		int index2;
		String op = instruction.substring(0, index);
		if(op.equals("add") || op.equals("sub") || op.equals("mult") 
				|| op.equals("slet") || op.equals("lwr") || op.equals("swr")){
			int rs = get_first_number();
			int rt = get_first_number();
			int rd = get_first_number();
			i = 0;
			rs <<= 21;
			rt <<= 16;
			rd <<= 11;
			i |= rs;
			i |= rt;
			i |= rd;
			if(op.equals("add")) i |= 0;
			else if(op.equals("sub")) i |= 0x08000000;
			else if(op.equals("mult")) i |= 0x0C000000;
			else if(op.equals("slet")) i |= 0x18000000;
			else if(op.equals("lwr")) i |= 0x24000000;
			else if(op.equals("swr")) i |= 0x28000000;
			return i;
		}
		else if(op.equals("addi")){
			int rs = get_first_number();
			int rt = get_first_number();
			int imm = get_first_number();
			i = 0;
			rs <<= 21;
			rt <<= 16;
			imm &= 0x0000FFFF;
			i |= rs;
			i |= rt;
			i |= imm;
			i |= 0x04000000;
			return i;
		}
		else if(op.equals("lw") || op.equals("sw")){
			int rt = get_first_number();
			int imm = get_first_number();
			int rs = get_first_number();
			i = 0;
			rs <<= 21;
			rt <<= 16;
			imm &= 0x0000FFFF;
			i |= rs;
			i |= rt;
			i |= imm;
			if(op.equals("lw")) i |= 0x10000000;
			else if(op.equals("sw")) i |= 0x14000000;
			return i;
		}
		else if(op.equals("swi")){
			int num = get_first_number();
			if(num == 1) return 0xFC000001;
			else return 0xFC000002;
		}
		else if(op.equals("beqz")){
			jump_branch jb = new jump_branch();
			jb.type = 0;
			jb.offset = inst_number;
			jb.reg = get_first_number();
			for(i = 0; i < instruction.length(); i++){
				if((instruction.charAt(i) >= 'a' && instruction.charAt(i) <= 'z') || 
						(instruction.charAt(i) >= 'A' && instruction.charAt(i) <= 'Z'))
					break;
			}
			int j;
			for(j = i + 1; j < instruction.length(); j++)
				if(!((instruction.charAt(i) >= 'a' && instruction.charAt(i) <= 'z') || 
						(instruction.charAt(i) >= 'A' && instruction.charAt(i) <= 'Z')))
					break;
			jb.label = instruction.substring(i, j);
			jumps.add(jb);
			return JUMP;
		}
		else if(op.equals("jump")){
			jump_branch jb = new jump_branch();
			jb.type = 1;
			jb.offset = inst_number;
			for(i = 4; i < instruction.length(); i++){
				if((instruction.charAt(i) >= 'a' && instruction.charAt(i) <= 'z') || 
						(instruction.charAt(i) >= 'A' && instruction.charAt(i) <= 'Z'))
					break;
			}
			int j;
			for(j = i + 1; j < instruction.length(); j++)
				if(!((instruction.charAt(i) >= 'a' && instruction.charAt(i) <= 'z') || 
						(instruction.charAt(i) >= 'A' && instruction.charAt(i) <= 'Z')))
					break;
			jb.label = instruction.substring(i, j);
			jumps.add(jb);
			return JUMP;
		}
		System.out.print(op);
		return -10;
	}
	
	static int get_first_number(){
		int i;
		boolean neg = false;
		for(i = 0; i < instruction.length(); i++){
			if(instruction.charAt(i) >= '0' && instruction.charAt(i) <= '9') break;
			if(instruction.charAt(i) == '-') neg = true;
		}
		int j;
		for(j = i + 1; j < instruction.length(); j++)
			if(!(instruction.charAt(j) >= '0' && instruction.charAt(j) <= '9')) break;
		int num = Integer.parseInt(instruction.substring(i, j));
		if(j != instruction.length())
			instruction = instruction.substring(j + 1);
		if(neg) return -num;
		return num;
	}
}

class jump_branch{
	int type;
	int reg;
	String label;
	int offset;
	jump_branch(){}
	jump_branch(int t, int r, String l, int o){
		type = t;
		reg = r;
		label = l;
		offset = o;
	}
}
