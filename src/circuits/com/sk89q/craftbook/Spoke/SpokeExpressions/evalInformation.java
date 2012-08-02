package com.sk89q.craftbook.Spoke.SpokeExpressions;

public class evalInformation {
	public boolean BreakBeforeEvaler;
	public boolean ResetCurrentVal;
	public boolean BreakBeforeEqual;
	public boolean EatTab;
	public int CheckMacs;
	public boolean DontEvalEquals;

	public evalInformation() {
		CheckMacs = 0;
	}

	public evalInformation(evalInformation inf) {
		// this.BreakBeforeEvaler = inf.BreakBeforeEvaler;
		// this.BreakBeforeEqual = inf.BreakBeforeEqual;
		this.EatTab = true;
		this.CheckMacs = inf.CheckMacs;

		// this.ResetCurrentVal = inf.ResetCurrentVal;
	}

	public evalInformation EatTab(boolean b) {
		EatTab = b;
		return this;
	}

	public evalInformation ResetCurrentVal(boolean b) {
		ResetCurrentVal = b;
		return this;
	}

	public evalInformation BreakBeforeEqual(boolean b) {
		BreakBeforeEqual = b;
		return this;
	}

	public evalInformation BreakBeforeEvaler(boolean b) {
		BreakBeforeEvaler = b;
		return this;
	}

	public evalInformation CheckMacs(int b) {
		CheckMacs = b;
		return this;
	}

	public evalInformation DontEvalEquals(boolean b) {
		DontEvalEquals = b;
		return this;
	}

	public boolean SkipStart = false;
	public boolean DoingMath;
	public boolean BreakBeforeMath;

	public evalInformation SkipStart(boolean b) {
		SkipStart = b;
		return this;
	}

	public evalInformation DoingMath(boolean b) {
		DoingMath = b;
		return this;
	}

	public evalInformation BreakBeforeMath(boolean b) {
		BreakBeforeMath=b;
		return this;
	}

}
