package com.sk89q.craftbook.Spoke;

import com.sk89q.craftbook.Spoke.SpokeInstructions.SpokeInstruction;
import com.sk89q.craftbook.Spoke.SpokeInstructions.SpokeObject;

public interface GetGlobalObject {
	public SpokeObject evalInstructions(SpokeInstruction[] ins);
}
