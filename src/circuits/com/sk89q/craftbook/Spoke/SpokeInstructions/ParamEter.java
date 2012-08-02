package com.sk89q.craftbook.Spoke.SpokeInstructions;

public class ParamEter {
	public String Name;
	public int Index;
	public boolean ByRef;
	public ParamEter(String n, int i, boolean b){
		Name=n;
		Index=i;
		ByRef=b;
	}
	public ParamEter(boolean byRef2, String word) {
ByRef=byRef2;
Name=word;
	}
}
