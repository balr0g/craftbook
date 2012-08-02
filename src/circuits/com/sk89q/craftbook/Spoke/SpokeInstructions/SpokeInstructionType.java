package com.sk89q.craftbook.Spoke.SpokeInstructions;

public enum SpokeInstructionType {
	EMPTY, StoreLocalInt, StoreLocalFloat, StoreLocalObject, StoreLocalBool, StoreLocalString, StoreLocalMethod, StoreFieldInt, StoreFieldFloat, StoreFieldObject, StoreFieldBool, StoreFieldString,
	StoreGlobalInt, StoreGlobalFloat, StoreGlobalObject, StoreGlobalBool, StoreGlobalString,
	
	StoreFieldMethod,

	CreateReference, CreateArray, CreateMethod,

	Label, Goto, CallMethod, CallInternal,

	BreakpointInstruction, Return, IfTrueContinueElseGoto,IfEqualsContinueAndPopElseGoto, Or, And,

	AddToArray,

	// StoreLocal,
	StoreLocalRef,
	// StoreField,
	StoreToReference,

	GetField, GetLocal, PopStack, Not, LengthOfArray, ArrayElem, AddStringInt, AddIntString, IntConstant, BoolConstant, FloatConstant, StringConstant, InsertToArray, RemoveToArray,

	Null, AddIntInt, AddIntFloat, AddFloatInt, AddFloatFloat, AddFloatString, AddStringFloat, AddStringString, SubtractIntInt, SubtractIntFloat, SubtractFloatInt, SubtractFloatFloat, MultiplyIntInt, MultiplyIntFloat, MultiplyFloatInt, MultiplyFloatFloat, DivideIntInt, DivideIntFloat, DivideFloatInt, DivideFloatFloat, GreaterIntInt, GreaterIntFloat, GreaterFloatInt, GreaterFloatFloat, LessIntInt, LessIntFloat, LessFloatInt, LessFloatFloat, GreaterEqualIntInt, GreaterEqualIntFloat, GreaterEqualFloatInt, GreaterEqualFloatFloat, LessEqualIntInt, LessEqualIntFloat, LessEqualFloatInt, LessEqualFloatFloat, Equal, AddRangeToArray, StoreArrayElem, CallMethodFunc, Comment, Mod, GetGlo, GetGlobal, AddObjectString,AddStringObject, AddStringBool, AddBoolString,  CallAnonMethod, AddMethodString, AddStringMethod
}
