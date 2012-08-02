package com.sk89q.craftbook.Spoke;

import com.sk89q.craftbook.Spoke.SpokeExpressions.SpokeType;
import com.sk89q.craftbook.Spoke.SpokeInstructions.SpokeObject;

public interface SpokeInternalMethod {
	public String getMethodName();

	public SpokeType getMethodType();

	public SpokeObject Evaluate(SpokeObject[] parameters);

	public Integer getMethodIndex();
}
