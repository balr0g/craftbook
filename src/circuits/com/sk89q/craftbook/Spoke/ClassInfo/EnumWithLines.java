package com.sk89q.craftbook.Spoke.ClassInfo;

import java.util.ArrayList;

import com.sk89q.craftbook.Spoke.Tokens.LineToken;

public class EnumWithLines {

	public ArrayList<LineToken> Variables = new ArrayList<LineToken>();// only needed in the creation process
	public String Name;

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("enum " + Name);
		return sb.toString();
	}
}