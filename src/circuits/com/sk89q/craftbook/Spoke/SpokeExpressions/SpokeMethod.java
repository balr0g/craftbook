package com.sk89q.craftbook.Spoke.SpokeExpressions;

import com.sk89q.craftbook.Spoke.SpokeInternalMethod;
import com.sk89q.craftbook.Spoke.SpokeMethodParameter;
import com.sk89q.craftbook.Spoke.SpokePreparse.SpokeMethodParse;
import com.sk89q.craftbook.Spoke.Tuple2;
import com.sk89q.craftbook.Spoke.SpokeInstructions.SpokeInstruction;

public class SpokeMethod {
	public SpokeLine[] Lines;
	public SpokeClass Class;
	public SpokeType returnType;
	public String MethodName;

	
	public SpokeMethod(String name, SpokeClass cl, SpokeType spokeType, SpokeMethodParameter[] params, SpokeInternalMethod spokeInternalMethod) {
		MethodName = name;
		Class = cl;
		returnType = spokeType;
		Parameters = params;
		MethodFunc = spokeInternalMethod;
		
	}

	public SpokeMethod(String name) {
		MethodName = name;
	}

	public String getCleanMethodName() {
		return MethodName.replace(".", "");
	}

	public SpokeMethodParameter[] Parameters;
	public boolean HasYield;
	public boolean HasReturn;

	public SpokeInternalMethod MethodFunc;
	public boolean HasYieldReturn;
	public int NumOfVars;
	public SpokeInstruction[] Instructions;
	public boolean Evaluated;
	public SpokeVariableInfo ParentVariableRefs;
	public boolean Static;
	public SpokeMethodParse OnlyMethodParse;
	public SpokeVariableInfo MethodVariableInfo;
	
}
