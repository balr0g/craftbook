package com.sk89q.craftbook.Spoke.SpokeExpressions;

public interface SpokeMathItem{
	public void PushItem(SpokeItem it);
	public int getWeight();
	SpokeItem[] getItems();
}