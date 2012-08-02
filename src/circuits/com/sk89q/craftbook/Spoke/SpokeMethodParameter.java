package com.sk89q.craftbook.Spoke;

import com.sk89q.craftbook.Spoke.SpokeExpressions.SpokeType;

public class SpokeMethodParameter {
	public String Name;
	public SpokeType Type;
	public SpokeMethodParameter(SpokeType t,String n){
		Name=n;
		Type=t;
	}	public SpokeMethodParameter(String n,SpokeType t){
		Name=n;
		Type=t;
	}
}