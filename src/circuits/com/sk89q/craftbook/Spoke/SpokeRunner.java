package com.sk89q.craftbook.Spoke;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

import com.google.gson.Gson;
import com.sk89q.craftbook.Spoke.SpokeLoader.CurrentDebugSetup;
import com.sk89q.craftbook.Spoke.SpokeLoader.DebugState;
import com.sk89q.craftbook.Spoke.SpokeExpressions.*;
import com.sk89q.craftbook.Spoke.SpokeInstructions.SpokeInstruction;
import com.sk89q.craftbook.Spoke.SpokeInstructions.SpokeObject;

public class SpokeRunner {
	private SpokeMethod[] Methods;
	private SpokeInternalMethod[] InternalMethods;

	private SpokeObject[] ints;
	private SpokeObject NULL = new SpokeObject(ObjectType.Null);

	private SpokeObject TRUE = new SpokeObject(true);
	private SpokeObject FALSE = new SpokeObject(false);
	private final SpokeApplication SpokeApp;
	private DebugHolder debugHolder;

	public SpokeRunner(SpokeApplication sa,
			SpokeInternalMethod[] internalMethods, SpokeMethod[] mets,
			DebugHolder dh) {
		debugHolder = dh;
		SpokeApp = sa;
		Methods = mets;
		InternalMethods = internalMethods;
		ints = new SpokeObject[100];
		for (int i = 0; i < 100; i++) {
			ints[i] = new SpokeObject(i);
		}
	}

	public void SetGlobal(SpokeObject[] globs) {
		globals = globs;
	}

	CurrentDebugSetup CDS;
	SpokeMethod currentMethod;

	public SpokeObject evaluateStaticMethodDebug(int methodIndex,
			SpokeObject[] params, CurrentDebugSetup cDS) throws SpokeException {
		currentMethod = Methods[methodIndex];
		CDS = cDS;
		SpokeObject[] variables = new SpokeObject[currentMethod.NumOfVars];

		for (int i = 0; i < params.length; i++) {
			variables[i] = params[i];
		}

		Stack.add(new SpokeStack(
				Methods[methodIndex].MethodVariableInfo.VariableNames,
				variables, Methods[methodIndex].MethodName, new SpokeException(
						currentMethod.Instructions[0].DebugToks)
						.Print(SpokeApp.Tokens)));
		SpokeObject d = evaluateInstructionsDebug(currentMethod.Instructions,
				variables);
		Stack.remove(Stack.size() - 1);
		return d;
	}

	public SpokeObject evaluateStaticMethod(int methodIndex,
			SpokeObject[] params) throws SpokeException {
		SpokeMethod fm = Methods[methodIndex];

		SpokeObject[] variables = new SpokeObject[fm.NumOfVars];

		for (int i = 0; i < params.length; i++) {
			variables[i] = params[i];
		}

		SpokeObject d = evaluateInstructions(fm.Instructions, variables);

		return d;
	}

	private SpokeObject intCache(int index) {
		if (index > 0 && index < 100) {
			return ints[index];
		}
		return new SpokeObject(index);
	}

	SpokeObject[] globals;

	SpokeObject evaluateInstructions(SpokeInstruction[] inj, int numberOfVars,
			SpokeObject[] paras) throws SpokeException {

		SpokeObject[] variables = new SpokeObject[numberOfVars];

		for (int i = 0; i < paras.length; i++) {
			variables[i] = paras[i];
		}
		return evaluateInstructions(inj, variables);
	}

	SpokeObject evaluateInstructions(SpokeInstruction[] inj,
			SpokeObject[] variables) throws SpokeException {

		SpokeObject lastStack;
		int stackIndex = 0;
		SpokeObject[] stack = new SpokeObject[3000];

		for (int index = 0; index < inj.length; index++) {
			SpokeInstruction ins = inj[index];
			SpokeObject[] sps;
			SpokeObject bm;

			switch (ins.Type) {
			case CreateReference:
				stack[stackIndex++] = new SpokeObject(
						new SpokeObject[ins.Index]);
				break;
			case CreateArray:
				stack[stackIndex++] = new SpokeObject(
						new ArrayList<SpokeObject>(20));
				break;
			case CreateMethod:
				stack[stackIndex++] = new SpokeObject(ObjectType.Method,
						ins.AnonMethod, variables);
				break;
			case Label:
				// throw new NotImplementedException("");
				break;
			case Goto:

				index = ins.Index;

				break;
			case Comment:

				break;
			case CallMethod:
				sps = new SpokeObject[ins.Index3];
				for (int i = ins.Index3 - 1; i >= 0; i--) {
					sps[i] = stack[--stackIndex];
				}

				stack[stackIndex++] = evaluateInstructions(
						Methods[ins.Index].Instructions,
						Methods[ins.Index].NumOfVars, sps);
				break;
			case CallAnonMethod:
				SpokeObject cur = stack[--stackIndex];
				for (int i = ins.Index3 - 1; i >= 0; i--) {
					cur.CurrentVariableStack[i + ins.Index] = stack[--stackIndex];
				}
				stack[stackIndex++] = evaluateInstructions(
						cur.AnonMethod.Instructions, cur.CurrentVariableStack);
				break;
			case CallMethodFunc:
				sps = new SpokeObject[ins.Index3];
				for (int i = ins.Index3 - 1; i >= 0; i--) {
					sps[i] = stack[--stackIndex];
				}

				stack[stackIndex++] = Methods[ins.Index].MethodFunc
						.Evaluate(sps);
				break;
			case CallInternal:
				sps = new SpokeObject[ins.Index3];
				for (int i = ins.Index3 - 1; i >= 0; i--) {
					sps[i] = stack[--stackIndex];
				}
				stack[stackIndex++] = InternalMethods[ins.Index].Evaluate(sps);
				break;
			case BreakpointInstruction:
				System.out.println("BreakPoint");
				break;
			case Return:
				return stack[--stackIndex];

			case IfTrueContinueElseGoto:

				if (stack[--stackIndex].BoolVal)
					continue;

				index = ins.Index;

				break;
			case IfEqualsContinueAndPopElseGoto:

				if (SpokeObject.Compare(stack[stackIndex - 2],
						stack[stackIndex - 1])) {
					stackIndex = stackIndex - 2;
					continue;
				}

				stackIndex = stackIndex - 1;
				index = ins.Index;
				break;
			case Or:
				stack[stackIndex - 2] = (stack[stackIndex - 2].BoolVal || stack[stackIndex - 1].BoolVal) ? TRUE
						: FALSE;
				stackIndex--;
				break;
			case And:
				stack[stackIndex - 2] = (stack[stackIndex - 2].BoolVal && stack[stackIndex - 1].BoolVal) ? TRUE
						: FALSE;
				stackIndex--;
				break;
			case StoreLocalInt:
				lastStack = stack[--stackIndex];
				variables[ins.Index] = new SpokeObject(lastStack.IntVal);
				break;
			case StoreLocalFloat:
				lastStack = stack[--stackIndex];
				variables[ins.Index] = new SpokeObject(lastStack.FloatVal);
				break;
			case StoreLocalBool:
				lastStack = stack[--stackIndex];
				variables[ins.Index] = lastStack.BoolVal ? TRUE : FALSE;
				break;
			case StoreLocalString:
				lastStack = stack[--stackIndex];
				variables[ins.Index] = new SpokeObject(lastStack.StringVal);
				break;

			case StoreLocalMethod:
			case StoreLocalObject:
				lastStack = stack[--stackIndex];
				bm = variables[ins.Index];
				variables[ins.Index] = lastStack;
				break;
			case StoreGlobalInt:
				lastStack = stack[--stackIndex];
				globals[ins.Index] = new SpokeObject(lastStack.IntVal);
				break;
			case StoreGlobalFloat:
				lastStack = stack[--stackIndex];
				globals[ins.Index] = new SpokeObject(lastStack.FloatVal);
				break;
			case StoreGlobalBool:
				lastStack = stack[--stackIndex];
				globals[ins.Index] = lastStack.BoolVal ? TRUE : FALSE;
				break;
			case StoreGlobalString:
				lastStack = stack[--stackIndex];
				globals[ins.Index] = new SpokeObject(lastStack.StringVal);
				break;
			case StoreGlobalObject:
				lastStack = stack[--stackIndex];
				bm = globals[ins.Index];
				globals[ins.Index] = lastStack;
				break;
			case StoreLocalRef:
				lastStack = stack[--stackIndex];
				bm = variables[ins.Index];
				bm.Variables = lastStack.Variables;
				bm.ArrayItems = lastStack.ArrayItems;
				bm.StringVal = lastStack.StringVal;
				bm.IntVal = lastStack.IntVal;
				bm.BoolVal = lastStack.BoolVal;
				bm.FloatVal = lastStack.FloatVal;
				break;

			case StoreFieldBool:
				lastStack = stack[--stackIndex];
				lastStack.Variables[ins.Index] = stack[--stackIndex].BoolVal ? TRUE
						: FALSE;

				break;
			case StoreFieldInt:
				lastStack = stack[--stackIndex];
				lastStack.Variables[ins.Index] = new SpokeObject(
						stack[--stackIndex].IntVal);

				break;
			case StoreFieldFloat:
				lastStack = stack[--stackIndex];
				lastStack.Variables[ins.Index] = new SpokeObject(
						stack[--stackIndex].FloatVal);

				break;
			case StoreFieldString:
				lastStack = stack[--stackIndex];
				lastStack.Variables[ins.Index] = new SpokeObject(
						stack[--stackIndex].StringVal);
				break;

			case StoreFieldMethod:
			case StoreFieldObject:
				lastStack = stack[--stackIndex];
				lastStack.Variables[ins.Index] = stack[--stackIndex];

				break;

			case StoreToReference:

				lastStack = stack[--stackIndex];
				stack[stackIndex - 1].Variables[ins.Index] = lastStack;
				break;
			case GetField:

				stack[stackIndex - 1] = stack[stackIndex - 1].Variables[ins.Index];

				break;
			case GetLocal:

				stack[stackIndex++] = variables[ins.Index];
				break;
			case GetGlobal:
				stack[stackIndex++] = globals[ins.Index];
				break;
			case PopStack:
				stackIndex--;
				break;
			case Not:
				stack[stackIndex - 1] = stack[stackIndex - 1].BoolVal ? FALSE
						: TRUE;
				break;

			case AddStringMethod:
				stack[stackIndex - 2] = new SpokeObject(
						stack[stackIndex - 2].StringVal
								+ stack[stackIndex - 1].AnonMethod);
				stackIndex--;

				break;

			case AddStringInt:
				stack[stackIndex - 2] = new SpokeObject(
						stack[stackIndex - 2].StringVal
								+ stack[stackIndex - 1].IntVal);
				stackIndex--;

				break;

			case AddStringObject:
				stack[stackIndex - 2] = new SpokeObject(
						stack[stackIndex - 2].StringVal + stack[stackIndex - 1]);
				stackIndex--;

				break;
			case AddMethodString:
				stack[stackIndex - 2] = new SpokeObject(
						stack[stackIndex - 2].AnonMethod
								+ stack[stackIndex - 1].StringVal);
				stackIndex--;

				break;
			case AddObjectString:
				stack[stackIndex - 2] = new SpokeObject(
						stack[stackIndex - 2].toString()
								+ stack[stackIndex - 1].StringVal);
				stackIndex--;

				break;

			case AddIntString:

				stack[stackIndex - 2] = new SpokeObject(
						stack[stackIndex - 2].IntVal
								+ stack[stackIndex - 1].StringVal);
				stackIndex--;
				break;
			case AddBoolString:
				stack[stackIndex - 2] = new SpokeObject(
						stack[stackIndex - 2].BoolVal
								+ stack[stackIndex - 1].StringVal);
				stackIndex--;
				break;
			case AddStringBool:

				stack[stackIndex - 2] = new SpokeObject(
						stack[stackIndex - 2].StringVal
								+ stack[stackIndex - 1].BoolVal);
				stackIndex--;
				break;
			case IntConstant:
				stack[stackIndex++] = intCache(ins.Index);
				break;
			case BoolConstant:

				stack[stackIndex++] = ins.BoolVal ? TRUE : FALSE;
				break;
			case FloatConstant:
				stack[stackIndex++] = new SpokeObject(ins.FloatVal);
				break;
			case StringConstant:

				stack[stackIndex++] = new SpokeObject(ins.StringVal);
				break;

			case Null:
				stack[stackIndex++] = NULL;
				break;
			case AddIntInt:
				stack[stackIndex - 2] = intCache(stack[stackIndex - 2].IntVal
						+ stack[stackIndex - 1].IntVal);
				stackIndex--;
				break;
			case AddIntFloat:
				break;
			case AddFloatInt:
				break;
			case AddFloatFloat:
				break;
			case AddFloatString:
				stack[stackIndex - 2] = new SpokeObject(
						stack[stackIndex - 2].FloatVal
								+ stack[stackIndex - 1].StringVal);
				stackIndex--;
				break;
			case AddStringFloat:
				stack[stackIndex - 2] = new SpokeObject(
						stack[stackIndex - 2].StringVal
								+ stack[stackIndex - 1].FloatVal);
				stackIndex--;
				break;
			case AddStringString:
				stack[stackIndex - 2] = new SpokeObject(
						stack[stackIndex - 2].StringVal
								+ stack[stackIndex - 1].StringVal);
				stackIndex--;
				break;
			case SubtractIntInt:
				stack[stackIndex - 2] = intCache(stack[stackIndex - 2].IntVal
						- stack[stackIndex - 1].IntVal);
				stackIndex--;

				break;
			case SubtractIntFloat:
				break;
			case SubtractFloatInt:
				break;
			case SubtractFloatFloat:
				break;
			case MultiplyIntInt:

				stack[stackIndex - 2] = intCache(stack[stackIndex - 2].IntVal
						* stack[stackIndex - 1].IntVal);
				stackIndex--;
				break;
			case Mod:
				stack[stackIndex - 2] = intCache(stack[stackIndex - 2].IntVal
						% stack[stackIndex - 1].IntVal);
				stackIndex--;
				break;
			case MultiplyIntFloat:
				break;
			case MultiplyFloatInt:
				break;
			case MultiplyFloatFloat:
				break;
			case DivideIntInt:

				stack[stackIndex - 2] = new SpokeObject(
						(stack[stackIndex - 2].IntVal)
								/ (stack[stackIndex - 1].IntVal));
				stackIndex--;
				break;
			case DivideIntFloat:
				break;
			case DivideFloatInt:
				break;
			case DivideFloatFloat:
				break;

			case GreaterIntInt:
				stack[stackIndex - 2] = (stack[stackIndex - 2].IntVal > stack[stackIndex - 1].IntVal) ? TRUE
						: FALSE;
				stackIndex = stackIndex - 1;
				break;
			case GreaterIntFloat:
				stack[stackIndex - 2] = (stack[stackIndex - 2].IntVal > stack[stackIndex - 1].FloatVal) ? TRUE
						: FALSE;
				stackIndex = stackIndex - 1;
				break;
			case GreaterFloatInt:
				stack[stackIndex - 2] = (stack[stackIndex - 2].FloatVal > stack[stackIndex - 1].IntVal) ? TRUE
						: FALSE;
				stackIndex = stackIndex - 1;
				break;
			case GreaterFloatFloat:
				stack[stackIndex - 2] = (stack[stackIndex - 2].FloatVal > stack[stackIndex - 1].FloatVal) ? TRUE
						: FALSE;
				stackIndex = stackIndex - 1;
				break;
			case LessIntInt:
				stack[stackIndex - 2] = (stack[stackIndex - 2].IntVal < stack[stackIndex - 1].IntVal) ? TRUE
						: FALSE;
				stackIndex = stackIndex - 1;
				break;
			case LessIntFloat:
				stack[stackIndex - 2] = (stack[stackIndex - 2].IntVal < stack[stackIndex - 1].FloatVal) ? TRUE
						: FALSE;
				stackIndex = stackIndex - 1;
				break;
			case LessFloatInt:
				stack[stackIndex - 2] = (stack[stackIndex - 2].FloatVal < stack[stackIndex - 1].IntVal) ? TRUE
						: FALSE;
				stackIndex = stackIndex - 1;
				break;
			case LessFloatFloat:
				stack[stackIndex - 2] = (stack[stackIndex - 2].FloatVal < stack[stackIndex - 1].FloatVal) ? TRUE
						: FALSE;
				stackIndex = stackIndex - 1;
				break;
			case GreaterEqualIntInt:
				stack[stackIndex - 2] = (stack[stackIndex - 2].IntVal >= stack[stackIndex - 1].IntVal) ? TRUE
						: FALSE;
				stackIndex = stackIndex - 1;
				break;
			case GreaterEqualIntFloat:
				stack[stackIndex - 2] = (stack[stackIndex - 2].IntVal >= stack[stackIndex - 1].FloatVal) ? TRUE
						: FALSE;
				stackIndex = stackIndex - 1;
				break;
			case GreaterEqualFloatInt:
				stack[stackIndex - 2] = (stack[stackIndex - 2].FloatVal >= stack[stackIndex - 1].IntVal) ? TRUE
						: FALSE;
				stackIndex = stackIndex - 1;
				break;
			case GreaterEqualFloatFloat:
				stack[stackIndex - 2] = (stack[stackIndex - 2].FloatVal >= stack[stackIndex - 1].FloatVal) ? TRUE
						: FALSE;
				stackIndex = stackIndex - 1;
				break;
			case LessEqualIntInt:
				stack[stackIndex - 2] = (stack[stackIndex - 2].IntVal <= stack[stackIndex - 1].IntVal) ? TRUE
						: FALSE;
				stackIndex = stackIndex - 1;
				break;
			case LessEqualIntFloat:
				stack[stackIndex - 2] = (stack[stackIndex - 2].IntVal <= stack[stackIndex - 1].FloatVal) ? TRUE
						: FALSE;
				stackIndex = stackIndex - 1;
				break;
			case LessEqualFloatInt:
				stack[stackIndex - 2] = (stack[stackIndex - 2].FloatVal <= stack[stackIndex - 1].IntVal) ? TRUE
						: FALSE;
				stackIndex = stackIndex - 1;
				break;
			case LessEqualFloatFloat:
				stack[stackIndex - 2] = (stack[stackIndex - 2].FloatVal <= stack[stackIndex - 1].FloatVal) ? TRUE
						: FALSE;
				stackIndex = stackIndex - 1;
				break;
			case Equal:
				stack[stackIndex - 2] = SpokeObject.Compare(
						stack[stackIndex - 2], stack[stackIndex - 1]) ? TRUE
						: FALSE;
				stackIndex = stackIndex - 1;
				break;
			case InsertToArray:
				break;
			case RemoveToArray:
				break;
			case AddToArray:
				lastStack = stack[--stackIndex];
				stack[stackIndex - 1].AddArray(lastStack);
				break;
			case AddRangeToArray:
				lastStack = stack[--stackIndex];
				stack[stackIndex - 1].AddRangeArray(lastStack);
				break;
			case LengthOfArray:
				break;

			case ArrayElem:

				lastStack = stack[--stackIndex];
				stack[stackIndex - 1] = stack[stackIndex - 1].ArrayItems
						.get(lastStack.IntVal);

				break;
			case StoreArrayElem:

				SpokeObject indexs = stack[--stackIndex];
				SpokeObject ars = stack[--stackIndex];

				ars.ArrayItems.set(indexs.IntVal, stack[--stackIndex]);

				break;

			}

		}

		return null;

	}

	SpokeObject evaluateInstructionsDebug(SpokeInstruction[] inj,
			int numberOfVars, SpokeObject[] paras,
			SpokeInstruction curentInstruction) throws SpokeException {

		SpokeObject[] variables = new SpokeObject[numberOfVars];

		for (int i = 0; i < paras.length; i++) {
			variables[i] = paras[i];
		}

		SpokeObject df = evaluateInstructionsDebug(inj, variables);
		Stack.remove(Stack.size() - 1);
		return df;
	}

	public ArrayList<SpokeStack> Stack = new ArrayList<SpokeStack>();

	SpokeObject evaluateInstructionsDebug(SpokeInstruction[] inj,
			SpokeObject[] variables) throws SpokeException {

		SpokeObject lastStack;
		int stackIndex = 0;
		SpokeObject[] stack = new SpokeObject[3000];

		boolean out = false;

		for (int index = 0; index < inj.length; index++) {
			SpokeInstruction ins = inj[index];
			SpokeObject[] sps;

			SpokeObject bm;

			boolean over = false;

			if (!out && CDS.AppBroken) {
				if (ins.DebugToks != null && ins.DebugToks.length > 0)
					debugHolder.writeDebugInfo(GenerateStackTrace(ins),
							currentMethod);

			}

			switch (CDS.State) {
			case StepOver:
				over = true;
				break;
			case StepOut:
				CDS.AppBroken = false;
				out = true;
				break;
			case Run:
				out = false;
				CDS.AppBroken = false;
				break;
			}

			try {
				switch (ins.Type) {
				case CreateReference:
					stack[stackIndex++] = new SpokeObject(
							new SpokeObject[ins.Index]);
					break;
				case CreateArray:
					stack[stackIndex++] = new SpokeObject(
							new ArrayList<SpokeObject>(20));
					break;
				case CreateMethod:
					stack[stackIndex++] = new SpokeObject(ObjectType.Method,
							ins.AnonMethod, variables);
					break;
				case Label:
					// throw new NotImplementedException("");
					break;
				case Goto:

					index = ins.Index;

					break;
				case Comment:

					break;
				case CallMethod:
					sps = new SpokeObject[ins.Index3];
					for (int i = ins.Index3 - 1; i >= 0; i--) {
						sps[i] = stack[--stackIndex];
					}

					if (over) {
						CDS.State = DebugState.Run;
						CDS.AppBroken = false;
					}

					Stack.add(new SpokeStack(
							(currentMethod = Methods[ins.Index]).MethodVariableInfo.VariableNames,
							variables,
							Methods[ins.Index].MethodName,
							new SpokeException(
									Methods[ins.Index].Instructions[0].DebugToks)
									.Print(SpokeApp.Tokens)));

					stack[stackIndex++] = evaluateInstructionsDebug(
							Methods[ins.Index].Instructions,
							Methods[ins.Index].NumOfVars, sps, ins);

					if (over) {
						CDS.State = DebugState.StepOver;
						CDS.AppBroken = true;
					}
					break;
				case CallAnonMethod:
					SpokeObject cur = stack[--stackIndex];
					for (int i = ins.Index3 - 1; i >= 0; i--) {
						cur.CurrentVariableStack[i + ins.Index] = stack[--stackIndex];
					}
					Stack.add(new SpokeStack(new String[0],
							cur.CurrentVariableStack, "anon method",
							new SpokeException(ins.DebugToks)
									.Print(SpokeApp.Tokens)));
					if (over) {
						CDS.State = DebugState.Run;
						CDS.AppBroken = false;
					}

					stack[stackIndex++] = evaluateInstructionsDebug(
							cur.AnonMethod.Instructions,
							cur.CurrentVariableStack);
					Stack.remove(Stack.size() - 1);
					if (over) {
						CDS.State = DebugState.StepOver;
						CDS.AppBroken = true;
					}

					Stack.remove(Stack.size() - 1);
					break;
				case CallMethodFunc:
					sps = new SpokeObject[ins.Index3];
					for (int i = ins.Index3 - 1; i >= 0; i--) {
						sps[i] = stack[--stackIndex];
					}

					stack[stackIndex++] = Methods[ins.Index].MethodFunc
							.Evaluate(sps);
					break;
				case CallInternal:
					sps = new SpokeObject[ins.Index3];
					for (int i = ins.Index3 - 1; i >= 0; i--) {
						sps[i] = stack[--stackIndex];
					}
					stack[stackIndex++] = InternalMethods[ins.Index]
							.Evaluate(sps);
					break;
				case BreakpointInstruction:
					System.out.println("BreakPoint");
					break;
				case Return:
					if (CDS.State == DebugState.StepOut) {
						CDS.AppBroken = true;
					}
					return stack[--stackIndex];

				case IfTrueContinueElseGoto:

					if (stack[--stackIndex].BoolVal)
						continue;

					index = ins.Index;

					break;
				case Or:
					stack[stackIndex - 2] = (stack[stackIndex - 2].BoolVal || stack[stackIndex - 1].BoolVal) ? TRUE 
							: FALSE;
					stackIndex--;
					break;
				case And:
					stack[stackIndex - 2] = (stack[stackIndex - 2].BoolVal && stack[stackIndex - 1].BoolVal) ? TRUE
							: FALSE;
					stackIndex--;
					break;
				case StoreLocalInt:
					lastStack = stack[--stackIndex];
					variables[ins.Index] = new SpokeObject(lastStack.IntVal);
					break;
				case StoreLocalFloat:
					lastStack = stack[--stackIndex];
					variables[ins.Index] = new SpokeObject(lastStack.FloatVal);
					break;
				case StoreLocalBool:
					lastStack = stack[--stackIndex];
					variables[ins.Index] = lastStack.BoolVal ? TRUE : FALSE;
					break;
				case StoreLocalString:
					lastStack = stack[--stackIndex];
					variables[ins.Index] = new SpokeObject(lastStack.StringVal);
					break;

				case StoreLocalMethod:
				case StoreLocalObject:
					lastStack = stack[--stackIndex];
					bm = variables[ins.Index];
					variables[ins.Index] = lastStack;
					break;
				case StoreGlobalInt:
					lastStack = stack[--stackIndex];
					globals[ins.Index] = new SpokeObject(lastStack.IntVal);
					break;
				case StoreGlobalFloat:
					lastStack = stack[--stackIndex];
					globals[ins.Index] = new SpokeObject(lastStack.FloatVal);
					break;
				case StoreGlobalBool:
					lastStack = stack[--stackIndex];
					globals[ins.Index] = lastStack.BoolVal ? TRUE : FALSE;
					break;
				case StoreGlobalString:
					lastStack = stack[--stackIndex];
					globals[ins.Index] = new SpokeObject(lastStack.StringVal);
					break;
				case StoreGlobalObject:
					lastStack = stack[--stackIndex];
					bm = globals[ins.Index];
					globals[ins.Index] = lastStack;
					break;
				case StoreLocalRef:
					lastStack = stack[--stackIndex];
					bm = variables[ins.Index];
					bm.Variables = lastStack.Variables;
					bm.ArrayItems = lastStack.ArrayItems;
					bm.StringVal = lastStack.StringVal;
					bm.IntVal = lastStack.IntVal;
					bm.BoolVal = lastStack.BoolVal;
					bm.FloatVal = lastStack.FloatVal;
					break;

				case StoreFieldBool:
					lastStack = stack[--stackIndex];
					lastStack.Variables[ins.Index] = stack[--stackIndex].BoolVal ? TRUE
							: FALSE;

					break;
				case StoreFieldInt:
					lastStack = stack[--stackIndex];
					lastStack.Variables[ins.Index] = new SpokeObject(
							stack[--stackIndex].IntVal);

					break;
				case StoreFieldFloat:
					lastStack = stack[--stackIndex];
					lastStack.Variables[ins.Index] = new SpokeObject(
							stack[--stackIndex].FloatVal);

					break;
				case StoreFieldString:
					lastStack = stack[--stackIndex];
					lastStack.Variables[ins.Index] = new SpokeObject(
							stack[--stackIndex].StringVal);
					break;

				case StoreFieldMethod:
				case StoreFieldObject:
					lastStack = stack[--stackIndex];
					lastStack.Variables[ins.Index] = stack[--stackIndex];

					break;

				case StoreToReference:

					lastStack = stack[--stackIndex];
					stack[stackIndex - 1].Variables[ins.Index] = lastStack;
					break;
				case GetField:

					stack[stackIndex - 1] = stack[stackIndex - 1].Variables[ins.Index];

					break;
				case GetLocal:

					stack[stackIndex++] = variables[ins.Index];
					break;
				case GetGlobal:
					stack[stackIndex++] = globals[ins.Index];
					break;
				case PopStack:
					stackIndex--;
					break;
				case Not:
					stack[stackIndex - 1] = stack[stackIndex - 1].BoolVal ? FALSE
							: TRUE;
					break;

				case AddStringMethod:
					stack[stackIndex - 2] = new SpokeObject(
							stack[stackIndex - 2].StringVal
									+ stack[stackIndex - 1].AnonMethod);
					stackIndex--;

					break;

				case AddStringInt:
					stack[stackIndex - 2] = new SpokeObject(
							stack[stackIndex - 2].StringVal
									+ stack[stackIndex - 1].IntVal);
					stackIndex--;

					break;

				case AddStringObject:
					stack[stackIndex - 2] = new SpokeObject(
							stack[stackIndex - 2].StringVal
									+ stack[stackIndex - 1]);
					stackIndex--;

					break;
				case AddMethodString:
					stack[stackIndex - 2] = new SpokeObject(
							stack[stackIndex - 2].AnonMethod
									+ stack[stackIndex - 1].StringVal);
					stackIndex--;

					break;
				case AddObjectString:
					stack[stackIndex - 2] = new SpokeObject(
							stack[stackIndex - 2].toString()
									+ stack[stackIndex - 1].StringVal);
					stackIndex--;

					break;

				case AddIntString:

					stack[stackIndex - 2] = new SpokeObject(
							stack[stackIndex - 2].IntVal
									+ stack[stackIndex - 1].StringVal);
					stackIndex--;
					break;
				case AddBoolString:
					stack[stackIndex - 2] = new SpokeObject(
							stack[stackIndex - 2].BoolVal
									+ stack[stackIndex - 1].StringVal);
					stackIndex--;
					break;
				case AddStringBool:

					stack[stackIndex - 2] = new SpokeObject(
							stack[stackIndex - 2].StringVal
									+ stack[stackIndex - 1].BoolVal);
					stackIndex--;
					break;
				case IntConstant:
					stack[stackIndex++] = intCache(ins.Index);
					break;
				case BoolConstant:

					stack[stackIndex++] = ins.BoolVal ? TRUE : FALSE;
					break;
				case FloatConstant:
					stack[stackIndex++] = new SpokeObject(ins.FloatVal);
					break;
				case StringConstant:

					stack[stackIndex++] = new SpokeObject(ins.StringVal);
					break;

				case Null:
					stack[stackIndex++] = NULL;
					break;
				case AddIntInt:
					stack[stackIndex - 2] = intCache(stack[stackIndex - 2].IntVal
							+ stack[stackIndex - 1].IntVal);
					stackIndex--;
					break;
				case AddIntFloat:
					break;
				case AddFloatInt:
					break;
				case AddFloatFloat:
					break;
				case AddFloatString:
					stack[stackIndex - 2] = new SpokeObject(
							stack[stackIndex - 2].FloatVal
									+ stack[stackIndex - 1].StringVal);
					stackIndex--;
					break;
				case AddStringFloat:
					stack[stackIndex - 2] = new SpokeObject(
							stack[stackIndex - 2].StringVal
									+ stack[stackIndex - 1].FloatVal);
					stackIndex--;
					break;
				case AddStringString:
					stack[stackIndex - 2] = new SpokeObject(
							stack[stackIndex - 2].StringVal
									+ stack[stackIndex - 1].StringVal);
					stackIndex--;
					break;
				case SubtractIntInt:
					stack[stackIndex - 2] = intCache(stack[stackIndex - 2].IntVal
							- stack[stackIndex - 1].IntVal);
					stackIndex--;

					break;
				case SubtractIntFloat:
					break;
				case SubtractFloatInt:
					break;
				case SubtractFloatFloat:
					break;
				case MultiplyIntInt:

					stack[stackIndex - 2] = intCache(stack[stackIndex - 2].IntVal
							* stack[stackIndex - 1].IntVal);
					stackIndex--;
					break;
				case Mod:
					stack[stackIndex - 2] = intCache(stack[stackIndex - 2].IntVal
							% stack[stackIndex - 1].IntVal);
					stackIndex--;
					break;
				case MultiplyIntFloat:
					break;
				case MultiplyFloatInt:
					break;
				case MultiplyFloatFloat:
					break;
				case DivideIntInt:

					stack[stackIndex - 2] = new SpokeObject(
							(stack[stackIndex - 2].IntVal)
									/ (stack[stackIndex - 1].IntVal));
					stackIndex--;
					break;
				case DivideIntFloat:
					break;
				case DivideFloatInt:
					break;
				case DivideFloatFloat:
					break;

				case GreaterIntInt:
					stack[stackIndex - 2] = (stack[stackIndex - 2].IntVal > stack[stackIndex - 1].IntVal) ? TRUE
							: FALSE;
					stackIndex = stackIndex - 1;
					break;
				case GreaterIntFloat:
					stack[stackIndex - 2] = (stack[stackIndex - 2].IntVal > stack[stackIndex - 1].FloatVal) ? TRUE
							: FALSE;
					stackIndex = stackIndex - 1;
					break;
				case GreaterFloatInt:
					stack[stackIndex - 2] = (stack[stackIndex - 2].FloatVal > stack[stackIndex - 1].IntVal) ? TRUE
							: FALSE;
					stackIndex = stackIndex - 1;
					break;
				case GreaterFloatFloat:
					stack[stackIndex - 2] = (stack[stackIndex - 2].FloatVal > stack[stackIndex - 1].FloatVal) ? TRUE
							: FALSE;
					stackIndex = stackIndex - 1;
					break;
				case LessIntInt:
					stack[stackIndex - 2] = (stack[stackIndex - 2].IntVal < stack[stackIndex - 1].IntVal) ? TRUE
							: FALSE;
					stackIndex = stackIndex - 1;
					break;
				case LessIntFloat:
					stack[stackIndex - 2] = (stack[stackIndex - 2].IntVal < stack[stackIndex - 1].FloatVal) ? TRUE
							: FALSE;
					stackIndex = stackIndex - 1;
					break;
				case LessFloatInt:
					stack[stackIndex - 2] = (stack[stackIndex - 2].FloatVal < stack[stackIndex - 1].IntVal) ? TRUE
							: FALSE;
					stackIndex = stackIndex - 1;
					break;
				case LessFloatFloat:
					stack[stackIndex - 2] = (stack[stackIndex - 2].FloatVal < stack[stackIndex - 1].FloatVal) ? TRUE
							: FALSE;
					stackIndex = stackIndex - 1;
					break;
				case GreaterEqualIntInt:
					stack[stackIndex - 2] = (stack[stackIndex - 2].IntVal >= stack[stackIndex - 1].IntVal) ? TRUE
							: FALSE;
					stackIndex = stackIndex - 1;
					break;
				case GreaterEqualIntFloat:
					stack[stackIndex - 2] = (stack[stackIndex - 2].IntVal >= stack[stackIndex - 1].FloatVal) ? TRUE
							: FALSE;
					stackIndex = stackIndex - 1;
					break;
				case GreaterEqualFloatInt:
					stack[stackIndex - 2] = (stack[stackIndex - 2].FloatVal >= stack[stackIndex - 1].IntVal) ? TRUE
							: FALSE;
					stackIndex = stackIndex - 1;
					break;
				case GreaterEqualFloatFloat:
					stack[stackIndex - 2] = (stack[stackIndex - 2].FloatVal >= stack[stackIndex - 1].FloatVal) ? TRUE
							: FALSE;
					stackIndex = stackIndex - 1;
					break;
				case LessEqualIntInt:
					stack[stackIndex - 2] = (stack[stackIndex - 2].IntVal <= stack[stackIndex - 1].IntVal) ? TRUE
							: FALSE;
					stackIndex = stackIndex - 1;
					break;
				case LessEqualIntFloat:
					stack[stackIndex - 2] = (stack[stackIndex - 2].IntVal <= stack[stackIndex - 1].FloatVal) ? TRUE
							: FALSE;
					stackIndex = stackIndex - 1;
					break;
				case LessEqualFloatInt:
					stack[stackIndex - 2] = (stack[stackIndex - 2].FloatVal <= stack[stackIndex - 1].IntVal) ? TRUE
							: FALSE;
					stackIndex = stackIndex - 1;
					break;
				case LessEqualFloatFloat:
					stack[stackIndex - 2] = (stack[stackIndex - 2].FloatVal <= stack[stackIndex - 1].FloatVal) ? TRUE
							: FALSE;
					stackIndex = stackIndex - 1;
					break;
				case Equal:
					stack[stackIndex - 2] = SpokeObject.Compare(
							stack[stackIndex - 2], stack[stackIndex - 1]) ? TRUE
							: FALSE;
					stackIndex = stackIndex - 1;
					break;
				case InsertToArray:
					break;
				case RemoveToArray:
					break;
				case AddToArray:
					lastStack = stack[--stackIndex];
					stack[stackIndex - 1].AddArray(lastStack);
					break;
				case AddRangeToArray:
					lastStack = stack[--stackIndex];
					stack[stackIndex - 1].AddRangeArray(lastStack);
					break;
				case LengthOfArray:
					break;

				case ArrayElem:

					lastStack = stack[--stackIndex];
					stack[stackIndex - 1] = stack[stackIndex - 1].ArrayItems
							.get(lastStack.IntVal);

					break;
				case StoreArrayElem:

					SpokeObject indexs = stack[--stackIndex];
					SpokeObject ars = stack[--stackIndex];

					ars.ArrayItems.set(indexs.IntVal, stack[--stackIndex]);

					break;

				default:
					throw new Exception("bad");
				}
			} catch (SpokeException ec) {
				throw ec;

			} catch (Exception ec) {
				throw new SpokeException(ec, ins.DebugToks);
			}
		}
		return null;

	}

	private SpokeStackTrace GenerateStackTrace(SpokeInstruction ins) {

		return new SpokeStackTrace(Stack,
				new SpokeException(ins.DebugToks).Print(SpokeApp.Tokens),
				SpokeApp);

		// return new SpokeException(ins.DebugToks).Print(SpokeApp.Tokens);
	}
}