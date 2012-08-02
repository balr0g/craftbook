package com.sk89q.craftbook.Spoke.SpokeExpressions;

public class SpokeMethodType {
	public SpokeType[] Params;
	public SpokeType Return;

	public SpokeMethodType(SpokeMethodType methodType) {
		Params = (SpokeType[]) methodType.Params.clone();
		Return = new SpokeType(methodType.Return);

	}

	public SpokeMethodType(SpokeType[] p, SpokeType r) {
		Params = p;
		Return = r;

	}

	public boolean CompareTo(SpokeMethodType grb, boolean allowLeftUnset) {
		if (grb == null) {
			return false;
		}
		if (grb.Params.length != Params.length)
			return false;
		if (!grb.Return.CompareTo(Return, allowLeftUnset))
			return false;

		for (int index = 0; index < Params.length; index++) {
			SpokeType spokeType = Params[index];
			if (!spokeType.CompareTo(grb.Params[index], allowLeftUnset)) {
				return false;
			}
		}

		return true;
	}
}
