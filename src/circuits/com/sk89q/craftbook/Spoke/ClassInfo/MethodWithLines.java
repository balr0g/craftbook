package com.sk89q.craftbook.Spoke.ClassInfo;

import java.util.ArrayList;

import com.sk89q.craftbook.Spoke.SpokeMethodParameter; 
import com.sk89q.craftbook.Spoke.Tokens.LineToken;

public class MethodWithLines {
	public ArrayList<LineToken> Lines = new ArrayList<LineToken>();
	public ArrayList<SpokeMethodParameter> paramNames = new ArrayList<SpokeMethodParameter>();
	public String Name;
	public boolean Static;
}
