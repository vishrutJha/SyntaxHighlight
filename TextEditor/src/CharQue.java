import java.util.Vector;


class CharQue {
	
	Vector<String> words ;
	public boolean flag;
	public boolean undoOp;
	public String key;
	String [] keywords={"abstract", "do", "import", "public", "throws", "boolean",
			"double", "instanceof", "return", "transient", "break", "else", "int", "short",
			"try", "byte", "extends", "interface", "static", "void", "case", "final", "long",
			"strictfp", "volatile", "catch", "finally", "native", "super", "while", "char", "float",
			"new", "switch", "class", "for", "package", "synchronized", "continue", "if", "private",
			"this", "default", "implements", "protected", "const", "goto", "null", "true", "false"};
	
	public CharQue(){
		words = new Vector<>();
		flag=false;
		key = "";
	}

	public void add(String s) {
				
		key+=s;
		for(String bla : keywords){
			if(bla.startsWith(s)){
				flag = true;
			}
		}
		
	}
	
	public boolean checkKeyWord(){
		for(String chk : keywords){
			if(chk.equals(key)){
				flag = false;
				return true;
			}
		}
		return false;
	}

	public void reduceLength() {
		if(key.length()!=0){
		key = key.substring(0, key.length()-1);
		System.out.println("string is now "+key);
		}
	}
	
}