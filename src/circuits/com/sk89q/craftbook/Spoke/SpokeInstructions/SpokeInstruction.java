package com.sk89q.craftbook.Spoke.SpokeInstructions;

import java.util.ArrayList;

import com.sk89q.craftbook.Spoke.IToken;
import com.sk89q.craftbook.Spoke.Token;
import com.sk89q.craftbook.Spoke.SpokeExpressions.SpokeBasic;
import com.sk89q.craftbook.Spoke.SpokeExpressions.SpokeObjectMethod;

public class SpokeInstruction {
	public static ArrayList<SpokeInstruction> ins;

	public static void Beginner() {
		ins = new ArrayList<SpokeInstruction>(2000);
	}

	public static SpokeInstruction[] Ender() {
		SpokeInstruction[] d = ins.toArray(new SpokeInstruction[ins.size()]);
		ins.clear();
		return d;
	}

	public SpokeInstructionType Type;
	public int Index;
	public int Index2;
	public int Index3;
	public boolean BoolVal = false;
	public float FloatVal = 0;
	public String StringVal = null; 

	public String DEBUG;
	

	@Override
	public String toString() {
		// String m = ShouldOnlyBeStack.ToString();
		String m = "";

		if (Type == SpokeInstructionType.BoolConstant) {
			return Type + "  \t" + m + "  \t " + BoolVal;
		}
		if (Type == SpokeInstructionType.StringConstant) {
			return Type + "  \t" + m + "  \t \'" + StringVal + "'";
		}
		if (Type == SpokeInstructionType.IntConstant) {
			return Type + "  \t" + m + "  \t " + Index;
		}
		if (Type == SpokeInstructionType.FloatConstant) {
			return Type + "  \t" + m + "  \t " + FloatVal;
		}
		return Type.toString() + "  \t" + m + "  \t " + Index + " " + Index2
				+ " " + Index3 + "  \t" + DEBUG + "  \t  \t"
				+ (gotoGuy != null ? gotoGuy : labelGuy) + "  "
				+ (elseGuy != null ? elseGuy : "");
	}


	public SpokeInstruction(SpokeInstructionType it) {
		Type = it;
		ins.add(this);
		if(curToks.size()>0)
			DebugToks=curToks.get(curToks.size()-1);
		else
			DebugToks=new IToken[0];
	}

	public IToken[] DebugToks;
	
	public SpokeInstruction(SpokeInstructionType getLocal, float i)

	{
		this(getLocal);
		FloatVal = i;
	}

	public SpokeInstruction(SpokeInstructionType getLocal, int i, int i2, int i3)

	{
		this(getLocal);
		Index = i;
		Index2 = i2;
		Index3 = i3;

	}

	public SpokeInstruction(SpokeInstructionType getLocal, int i) {
		this(getLocal); 
		Index = i;

	}

	public SpokeInstruction(SpokeInstructionType getLocal, int i, String debug)

	{
		this(getLocal);
		DEBUG = debug;
		Index = i;

	}

	public String gotoGuy;
	public String labelGuy;

	public SpokeInstruction(SpokeInstructionType getLocal, String i) {
		this(getLocal);
		if (getLocal == SpokeInstructionType.Goto) {
			gotoGuy = i;
		} else if (getLocal == SpokeInstructionType.IfTrueContinueElseGoto) {
			elseGuy = i;
		} else if (getLocal == SpokeInstructionType.IfEqualsContinueAndPopElseGoto) {
			elseGuy = i;
		} else if (getLocal == SpokeInstructionType.Label) {
			labelGuy = i;
		} else

			StringVal = i;

	}

	public String elseGuy;
	public SpokeObjectMethod AnonMethod;

	public SpokeInstruction(SpokeInstructionType getLocal, boolean i) {
		this(getLocal);
		BoolVal = i;
	}
 
	public SpokeInstruction(SpokeInstructionType createmethod, SpokeObjectMethod spokeObjectMethod) {
		this(createmethod);
		AnonMethod=spokeObjectMethod;		
	}

	public int StackAfter_ = -1;
	public int StackBefore_ = -1;
	public IToken[] DebugTokens;

	public int StackAfter() throws Exception {
		switch (Type) {
		case EMPTY:
			return 0;
			// todo: bad
		case Comment:
			return 0;
		case CreateReference:
		case CreateArray:
		case CreateMethod:
			return 1;

		case Label:
		case Goto:
			return 0;

		case CallMethod:
			return 0 - Index3 + 1;
		case CallInternal:
			return 0 - Index3 + 1;
		case CallMethodFunc:
			return 0 - Index3 + 1;
		case CallAnonMethod:
			return 0 - (Index3+1) + 1;

		case BreakpointInstruction:
			return 0;

		case Return:
			return 0;

		case IfTrueContinueElseGoto:
			return 0;
		case IfEqualsContinueAndPopElseGoto:
			return -1;

		case AddToArray:
			return -1;

		case StoreLocalBool:
		case StoreLocalInt:
		case StoreLocalFloat:
		case StoreLocalMethod:
		case StoreLocalObject:
		case StoreLocalString:
			return 0;

		case StoreLocalRef:
			return 0;

		case StoreFieldBool:
		case StoreFieldInt:
		case StoreFieldFloat:
		case StoreFieldMethod:
		case StoreFieldObject:
		case StoreFieldString:
			return -1;

		case StoreToReference:
			return 0;

		case GetField:
			return 0;
		case GetGlobal:
			return 1;

		case GetLocal:
			return 1;

		case PopStack:
			return -1;

		case Not:
			return 0;

		case LengthOfArray:
			return 0;

		case ArrayElem:
			return 0;

		case IntConstant:
		case BoolConstant:
		case FloatConstant:
		case StringConstant:
		case Null:
			return 1;
		case InsertToArray:
			return -1;
		case RemoveToArray:
			return -1;

		case Mod:
		case AddStringInt:
		case AddBoolString:
		case AddStringBool:
		case AddObjectString:
		case AddStringObject:
		case AddMethodString:
		case AddStringMethod:
		case AddIntString:
		case AddIntInt:
		case AddIntFloat:
		case AddFloatInt:
		case AddFloatFloat:
		case AddFloatString:
		case AddStringFloat:
		case AddStringString:
		case SubtractIntInt:
		case SubtractIntFloat:
		case SubtractFloatInt:
		case SubtractFloatFloat:
		case MultiplyIntInt:
		case MultiplyIntFloat:
		case MultiplyFloatInt:
		case MultiplyFloatFloat:
		case DivideIntInt:
		case DivideIntFloat:
		case DivideFloatInt:
		case DivideFloatFloat:
		case GreaterIntInt:
		case GreaterIntFloat:
		case GreaterFloatInt:
		case GreaterFloatFloat:
		case LessIntInt:
		case LessIntFloat:
		case LessFloatInt:
		case LessFloatFloat:
		case GreaterEqualIntInt:
		case GreaterEqualIntFloat:
		case GreaterEqualFloatInt:
		case GreaterEqualFloatFloat:
		case LessEqualIntInt:
		case LessEqualIntFloat:
		case LessEqualFloatInt:
		case LessEqualFloatFloat:
		case Equal:
		case Or:
		case And:
			return 0;
		case AddRangeToArray:
			return -1;

		case StoreArrayElem:
			return -3;

		default: 
			throw new Exception("Very bad "+Type);

		}
	}

	public int StackBefore() throws Exception {
		switch (Type) {
		case EMPTY:
			// TODO: bad
			return 0;
		case Comment:
			return 0;

		case CreateReference:
		case CreateArray:
		case CreateMethod:
			return 0;

		case Label:
			return 0;

		case Goto:
			return 0;

		case CallMethod:
			return 0;
		case CallInternal:
			return 0;
		case CallAnonMethod:
			return 0;

		case BreakpointInstruction:
			return 0;

		case Return:
			return -1;

		case IfTrueContinueElseGoto:
			return -1;
		case IfEqualsContinueAndPopElseGoto:
			return 0;
		case AddToArray:
			return 0;

		case StoreLocalBool:
		case StoreLocalInt:
		case StoreLocalFloat:
		case StoreLocalMethod:
		case StoreLocalObject:
		case StoreLocalString:
			return -1;

		case StoreLocalRef:
			return -1;

		case StoreFieldBool:
		case StoreFieldInt:
		case StoreFieldFloat:
		case StoreFieldMethod:
		case StoreFieldObject:
		case StoreFieldString:
			return -1;

		case StoreToReference:
			return -1;

		case GetField:
			return 0;
		case GetGlobal:
			return 0;

		case GetLocal:
			return 0;

		case PopStack:
			return 0;

		case Not:
			return 0;

		case LengthOfArray:
			return 0;

		case ArrayElem:
			return -1;

		case IntConstant:
		case BoolConstant:
		case FloatConstant:
		case StringConstant:
		case Null:
			return 0;

		case InsertToArray:
			return 0;
		case Mod:
		case AddStringInt:
		case AddBoolString:
		case AddStringBool:
		case AddObjectString:
		case AddStringObject:
		case AddMethodString:
		case AddStringMethod:
		case AddIntString:
		case AddIntInt:
		case AddIntFloat:
		case AddFloatInt:
		case AddFloatFloat:
		case AddFloatString:
		case AddStringFloat:
		case AddStringString:
		case SubtractIntInt:
		case SubtractIntFloat:
		case SubtractFloatInt:
		case SubtractFloatFloat:
		case MultiplyIntInt:
		case MultiplyIntFloat:
		case MultiplyFloatInt:
		case MultiplyFloatFloat:
		case DivideIntInt:
		case DivideIntFloat:
		case DivideFloatInt:
		case DivideFloatFloat:
		case GreaterIntInt:
		case GreaterIntFloat:
		case GreaterFloatInt:
		case GreaterFloatFloat:
		case LessIntInt:
		case LessIntFloat:
		case LessFloatInt:
		case LessFloatFloat:
		case GreaterEqualIntInt:
		case GreaterEqualIntFloat:
		case GreaterEqualFloatInt:
		case GreaterEqualFloatFloat:
		case LessEqualIntInt:
		case LessEqualIntFloat:
		case LessEqualFloatInt:
		case LessEqualFloatFloat:
		case Equal:
		case Or:
		case And:
			return -1;
		case CallMethodFunc:
			return 0;
	
		case AddRangeToArray:
			return 0;

		case StoreArrayElem:
			return 0;

		default: 
			throw new Exception("Very bad "+Type);
		}
	}
	
	static ArrayList<IToken[]> curToks=new ArrayList<IToken[]>();

	public static void RaiseDebugging(SpokeBasic spokeBasic) {
		if(spokeBasic.getTokens()==null){
			curToks.add(new IToken[0]);
			return;
		}
		ArrayList<IToken> toks=new ArrayList<IToken>();
		for (IToken iToken : spokeBasic.getTokens()) {
			if(!(iToken.getType()==Token.NewLine)){
				toks.add(iToken);
			}
		}
		curToks.add(toks.toArray(new IToken[toks.size()]));
	}
	public static void LowerDebugging() {
		curToks.remove(curToks.size()-1);
	}
}
