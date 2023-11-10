package com.donearh.hearme;

import java.util.Comparator;

public class CatComparator implements Comparator<CategoryData> {

	@Override
	public int compare(CategoryData lhs, CategoryData rhs) {
		// TODO Auto-generated method stub
		return lhs.name.compareTo(rhs.name);
	}

}
