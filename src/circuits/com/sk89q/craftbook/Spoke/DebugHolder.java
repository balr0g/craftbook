package com.sk89q.craftbook.Spoke;

import com.sk89q.craftbook.Spoke.SpokeExpressions.SpokeMethod;

public interface DebugHolder {
	public void writeDebugInfo(SpokeStackTrace spokeStackTrace,SpokeMethod offendingMethod);
}
