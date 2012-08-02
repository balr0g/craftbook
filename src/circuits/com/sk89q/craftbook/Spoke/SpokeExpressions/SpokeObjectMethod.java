package com.sk89q.craftbook.Spoke.SpokeExpressions;

import com.sk89q.craftbook.Spoke.SpokeInstructions.ParamEter;
import com.sk89q.craftbook.Spoke.SpokeInstructions.SpokeInstruction;

public class SpokeObjectMethod {
	public SpokeVariableInfo CurrentStack;

	public SpokeObjectMethod(SpokeLine[] lines2, ParamEter[] parameters2, boolean hasReturn2, boolean hasYield2, boolean hasYieldReturn2, SpokeVariableInfo variables) {
		Lines = lines2;
		Parameters = parameters2;
		HasReturn = hasReturn2;
		HasYield = hasYield2;
		HasYieldReturn = hasYieldReturn2;

		CurrentStack = variables;
	}
	public boolean HasYield;
	public boolean HasYieldReturn;
	public boolean HasReturn;
	public SpokeLine[] Lines;
	public ParamEter[] Parameters;
	public SpokeInstruction[] Instructions;
	public SpokeVariable ReturnYield;
	public SpokeType ReturnType; 
}
