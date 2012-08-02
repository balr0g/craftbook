package com.sk89q.craftbook.Spoke;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.sk89q.craftbook.Spoke.ALH.Aggregator;
import com.sk89q.craftbook.Spoke.SpokeExpressions.SpokeEqual;
import com.sk89q.craftbook.Spoke.SpokeExpressions.SpokeItem;
import com.sk89q.craftbook.Spoke.SpokeExpressions.SpokeMethod;

//Array List Helper
public class ALH {

	public static <T> T[] Resize(T[] array, int newSize) {
		T[] newArray = (T[]) Array.newInstance(array[0].getClass(), newSize);
		System.arraycopy(array, 0, newArray, 0, array.length);
		return newArray;
	}

	public static <T> boolean Contains(Collection<T> array, T value) {
		for (Iterator<T> iterator = array.iterator(); iterator.hasNext();) {
			T t = (T) iterator.next();
			if (t.equals(value)) {
				return true;
			}
		}
		return false;
	}

	public static <T> T LastOrDefault(ArrayList<T> lines) {
		if (lines.size() > 0) {

			return lines.get(lines.size() - 1);
		}
		return null;
	}

	public static <T> T FirstOrDefault(ArrayList<T> lines) {
		if (lines.size() > 0) {
			return lines.get(0);
		}
		return null;
	}

	public static <T> T Last(ArrayList<T> lines) {
		if (lines.size() > 0) {

			return lines.get(lines.size() - 1);
		}
		return null;// throw
	}

	public static <T> T First(ArrayList<T> lines) {
		if (lines.size() > 0) {
			return lines.get(0);
		}
		return null;// throw
	}

	public static <T> T LastOrDefault(ArrayList<T> lines, Finder<T> finder) {

		for (int i = lines.size() - 1; i >= 0; i--) {
			T m = lines.get(i);
			if (finder.Find(m)) {
				return m;
			}
		}
		return null;
	}

	public static <T> T FirstOrDefault(Collection<T> lines, Finder<T> finder) {
		for (Iterator<T> i = lines.iterator(); i.hasNext();) {
			T m = (T) i.next();
			if (finder.Find(m)) {
				return m;
			}
		}
		return null;
	}

	public static <T> T Last(ArrayList<T> lines, Finder<T> finder) {
		for (int i = lines.size() - 1; i >= 0; i--) {
			T m = lines.get(i);
			if (finder.Find(m)) {
				return m;
			}
		}
		return null;// throw
	}

	public static <T> T First(Collection<T> lines, Finder<T> finder) {
		for (Iterator<T> i = lines.iterator(); i.hasNext();) {
			T m = (T) i.next();
			if (finder.Find(m)) {
				return m;
			}
		}

		return null;// throw
	}
	public static <T> boolean Any(Collection<T> lines, Finder<T> finder) {
		for (Iterator<T> i = lines.iterator(); i.hasNext();) {
			T m = (T) i.next();
			if (finder.Find(m)) {
				return true;
			}
		}

		return false;// throw
	}

	public static <TSource, TAccumulate> TAccumulate Aggregate(
			ArrayList<TSource> lines, TAccumulate seed,
			Aggregator<TSource, TAccumulate> finder) {

		for (Iterator<TSource> i = lines.iterator(); i.hasNext();) {
			TSource m = (TSource) i.next();
			finder.Accumulate(seed, m);
		}
		return seed;
	}

	public interface Finder<T> {
		public boolean Find(T item);
	}

	public interface HashFinder<T1, T2> {
		public boolean Find(T1 item, T2 item2);
	}

	public interface Selector<T, T2> {
		public T2 Select(T item);
	}
	public interface ManySelector<T, T2> {
		public Collection<T2> Select(T item);
	}
	public interface DictionaryKey<T,T2> {
		public T2 Make(T item);
	}
	public interface DictionaryValue<T,T2> {
		public T2 Make(T item);
	}

	public interface Aggregator<TSource, TAccumulate> {
		public TAccumulate Accumulate(TAccumulate accumulate, TSource source);
	}

	public static <T> ArrayList<T> ArrayToList(T[] parameters) {
		ArrayList<T> ts = new ArrayList<T>(Arrays.asList(parameters));
		return ts;
	}

	public static <T, T2> ArrayList<T2> Select(Collection<T> param_,Selector<T, T2> ts) {
		ArrayList<T2> tc = new ArrayList<T2>();
		for (Iterator<T> i = param_.iterator(); i.hasNext();) {
			T m = (T) i.next();
			tc.add(ts.Select(m));
		}
		return tc;
	}
	public static <T, T2> ArrayList<T2> SelectMany(Collection<T> param_,ManySelector<T, T2> ts) {
		ArrayList<T2> tc = new ArrayList<T2>();
		for (Iterator<T> i = param_.iterator(); i.hasNext();) {
			T m = (T) i.next();
			tc.addAll(ts.Select(m));
		}
		return tc;
	}
	

	public static <T, T2,T3> HashMap<T2,T3> ToDictionary(Collection<T> param_,DictionaryKey<T,T2> k1,DictionaryValue<T,T3> v1) {
		HashMap<T2,T3> tc = new HashMap<T2,T3>();
		for (Iterator<T> i = param_.iterator(); i.hasNext();) {
			T m = (T) i.next();
			tc.put(k1.Make(m),v1.Make(m));
		}
		return tc;
	}

	public static <T1, T2> boolean All(HashMap<T1, T2> getanonMethodsEntered,
			HashFinder<T1, T2> hashFinder) {
		Set<T1> keys = getanonMethodsEntered.keySet();
		Collection<T2> vals = getanonMethodsEntered.values();

		Iterator<T2> i2 = vals.iterator();

		for (Iterator<T1> i1 = keys.iterator(); i1.hasNext() && i2.hasNext();) {

			if (!hashFinder.Find(i1.next(), i2.next())) {
				return false;
			}
		}
		return true;
	}

	public static <T1, T2> Tuple2<T1, T2> Last(
			HashMap<T1, T2> getanonMethodsEntered, HashFinder<T1, T2> hashFinder) throws Exception {
		Set<T1> keys = getanonMethodsEntered.keySet();
		Collection<T2> vals = getanonMethodsEntered.values();

		Iterator<T2> i2 = vals.iterator();
		Tuple2<T1, T2> last = null;

		for (Iterator<T1> i1 = keys.iterator(); i1.hasNext() && i2.hasNext();) {

			T1 c1;
			T2 c2;
			if (hashFinder.Find(c1 = i1.next(), c2 = i2.next())) {
				last = new Tuple2<T1, T2>(c1, c2);
			}
		}
		if(last==null){
			throw new Exception("None");
		}
		return last;
	}

	public static <T> Integer IndexOf(Collection<T> values, T val) {
		int ind=0;
		for (Iterator<T> iterator = values.iterator(); iterator.hasNext();) {
			T t = (T) iterator.next();
			if(t.equals(val))
				return ind;
			
			ind++;
		}
		return -1;
	}

}
