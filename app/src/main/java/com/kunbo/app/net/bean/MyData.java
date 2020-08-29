package com.kunbo.app.net.bean;

import java.util.ArrayList;

public class MyData extends ArrayList<MyRow> {

	private static final long serialVersionUID = 1L;


	/* (non-Javadoc)
	 * @see java.util.ArrayList#get(int)
	 * 防止数组越界
	 */
	public MyRow get(int index) {
		MyRow row;
		try {

			if (index >= size()) {// 防止数组越界
				row = new MyRow();
				return row;
			} else {
				return super.get(index);
			}
		} catch (Exception e) {
			row = new MyRow();
			return row;

		}

	}
	
	

}
