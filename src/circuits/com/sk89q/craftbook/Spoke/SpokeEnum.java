package com.sk89q.craftbook.Spoke;

import java.util.HashMap;

import com.sk89q.craftbook.Spoke.SpokeExpressions.ObjectType;
import com.sk89q.craftbook.Spoke.SpokeExpressions.SpokeType;

public class SpokeEnum {
	public String Name;
	public ObjectType Type;

	public HashMap<String, Object> Variables;

	public SpokeEnum(String name, ObjectType ot) {

		Type = ot;
		Name = name;
		Variables = new HashMap<String, Object>(); 
	}

	public void addVariable(String word, Object val) {
		Variables.put(word, val);

	}

}
