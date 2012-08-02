package com.sk89q.craftbook.Spoke.SpokeExpressions;
 
import com.sk89q.craftbook.Spoke.Tuple3;

public class SpokeType {
	public ObjectType Type;

	public SpokeType ArrayItemType;
	public SpokeMethodType MethodType;
	public SpokeVariableInfo Variables;
	public String ClassName;
	public boolean ByRef;

	public SpokeObjectMethod AnonMethod;
	public SpokeType CanOnlyBe;

	public SpokeType(SpokeType t) {
		Type = t.Type;
		ClassName = t.ClassName;
		ArrayItemType = t.ArrayItemType;
		ByRef = t.ByRef;
		if (t.Variables != null)
			Variables = new SpokeVariableInfo(t.Variables);
		if (t.MethodType != null)
			MethodType = new SpokeMethodType(t.MethodType);
	}

	public SpokeType(ObjectType t, SpokeType arrayType) {
		this(t);
		ArrayItemType = arrayType;
	}

	public SpokeType(ObjectType t) {
		Type = t;
		if (t == ObjectType.Array) {
			ClassName = "Array";
			ArrayItemType = new SpokeType(ObjectType.Unset);
		}
	}

	public SpokeType(ObjectType object, String name) {
		this(object);
		ClassName = name;
	}

	@Override
	public String toString() {
		return Type.toString();
	}

	public boolean CompareTo(SpokeType grb, boolean allowLeftUnset) {
		return CompareTo(grb, allowLeftUnset, false);
	}

	public boolean CompareTo(SpokeType grb, boolean allowLeftUnset, boolean arrayRules) {
		if (Type == ObjectType.Unset) {
			return allowLeftUnset;
		}

		if (arrayRules && grb.Type == ObjectType.Unset) {

			grb.CanOnlyBe = this;

			return true;
		}

		if ((Type == ObjectType.Null && grb.Type == ObjectType.Object) || Type == ObjectType.Object && grb.Type == ObjectType.Null) {
			return true;
		}
 
		if (Type.equals(grb.Type)) {
			switch (Type) {
			case Null:
				return true;
			case Int:
			case Float:
			case String:
			case Bool:
				return true;
			case Array:
				return ArrayItemType.CompareTo(grb.ArrayItemType, true, true);

			case Object:
				
				
				for (Tuple3<String, Integer, SpokeType> v : Variables.allVariables) {
					if(!grb.Variables.allVariables.get(v.Item2).Item1.equals(v.Item1) && grb.Variables.allVariables.get(v.Item2).Item3.CompareTo(v.Item3, true)){
						return false;
					}
				}
				return true;
			case Method:
				return MethodType.CompareTo(grb.MethodType, false);

			default:
				return false;
				// throw new Exception("bad");
			}

		}
		return false;
	}
}
