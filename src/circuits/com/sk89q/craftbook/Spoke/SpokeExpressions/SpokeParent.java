package com.sk89q.craftbook.Spoke.SpokeExpressions;

public interface SpokeParent extends SpokeItem {
	SpokeItem getParent();

	void setParent(SpokeItem item);
}
