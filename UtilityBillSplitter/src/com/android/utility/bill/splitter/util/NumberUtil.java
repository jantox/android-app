package com.android.utility.bill.splitter.util;

import java.text.DecimalFormat;

/**
 * 
 * @author jantox
 *
 */
public class NumberUtil {
	
	/**
	 * Round to 2 decimal places
	 * 
	 * @param val
	 * @return
	 */
	public static double RoundTo2Decimals(double val) {
		DecimalFormat df2 = new DecimalFormat("###.##");
		return Double.valueOf(df2.format(val));
	}
}
