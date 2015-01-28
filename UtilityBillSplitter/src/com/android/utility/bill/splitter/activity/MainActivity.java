package com.android.utility.bill.splitter.activity;

import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.utility.bill.splitter.util.NumberUtil;
import com.android.utility.bill.splitter.view.BoxView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

/**
 * This class is the main UI for the application.
 * 
 * @author jantox
 * 
 */
public class MainActivity extends Activity implements Observer {

	private int counterId = 0;
	private final SparseArray<BoxView> boxMap = new SparseArray<BoxView>();

	private EditText mTxtTotalBillView;
	private Button mBtnComputeBillView;
	private ImageButton mBtnAddPersonView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		AdRequest adRequest = new AdRequest.Builder().build();
		AdView adView = (AdView) this.findViewById(R.id.adView);
		adView.loadAd(adRequest);

		setTitle(getAppVersionName());

		// add default box
		addBoxView();

		// default focus and have soft keyboard
		mTxtTotalBillView = (EditText) findViewById(R.id.txtTotalPubBill);
		mTxtTotalBillView.requestFocus();
		mTxtTotalBillView.postDelayed(new Runnable() {
			@Override
			public void run() {
				InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				keyboard.showSoftInput(mTxtTotalBillView, 0);
			}
		}, 200);

		// add add person button
		mBtnAddPersonView = (ImageButton) findViewById(R.id.btnAddPerson);
		mBtnAddPersonView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mBtnAddPersonView.requestFocus();
				mBtnAddPersonView.postDelayed(new Runnable() {
					@Override
					public void run() {
						InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						keyboard.hideSoftInputFromWindow(
								mBtnAddPersonView.getWindowToken(), 0);
					}
				}, 200);

				View scrollView = findViewById(R.id.ScrollView);

				scrollView.getLayoutParams().height = LayoutParams.MATCH_PARENT;
				scrollView.getLayoutParams().width = LayoutParams.MATCH_PARENT;
				scrollView.requestLayout();

				addBoxView();
			}
		});

		// Add compute bill button
		mBtnComputeBillView = (Button) findViewById(R.id.btnComputeBillSplit);
		mBtnComputeBillView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (boxMap.size() != 0) {
					if (computeBill())
						Toast.makeText(
								getApplicationContext(),
								getResources().getString(
										R.string.toast_computation_done),
								Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(
							getApplicationContext(),
							getResources().getString(
									R.string.error_add_a_person),
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		// Lets check if soft keyboard is shown or not
		final View activityRootView = findViewById(R.id.MainLayout);
		activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						Rect r = new Rect();
						// r will be populated with the coordinates of your view
						// that area still visible.
						activityRootView.getWindowVisibleDisplayFrame(r);

						int heightDiff = activityRootView.getRootView()
								.getHeight() - (r.bottom - r.top);
						if (heightDiff > 100) { // if more than 100 pixels, its
												// probably a keyboard...
							adjustScrollView(true);
						} else {
							adjustScrollView(false);
						}
					}

					private void adjustScrollView(boolean isKeyboardOn) {
						View scrollView = findViewById(R.id.ScrollView);
						scrollView.getLayoutParams().height = isKeyboardOn ? 600
								: LayoutParams.MATCH_PARENT;
						scrollView.getLayoutParams().width = LayoutParams.MATCH_PARENT;
						scrollView.requestLayout();
					}
				});

	}

	/**
	 * Add the box view
	 */
	private void addBoxView() {
		LinearLayout layout = (LinearLayout) findViewById(R.id.layoutLinearPersonBills);
		counterId = counterId + 1;
		BoxView box = new BoxView(getApplicationContext(), counterId);
		box.addObserver(this);
		layout.addView(box.getView());
		boxMap.put(counterId, box);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		if (itemId == R.id.action_about) {
			createAlertDialog(R.id.action_about);
			return true;
		} else if (itemId == R.id.action_change_logs) {
			createAlertDialog(R.id.action_change_logs);
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Create dialog box
	 * 
	 * @param choice
	 */
	private void createAlertDialog(int choice) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

		if (choice == R.id.action_about) {
			// set title
			alertDialogBuilder.setTitle("About " + getAppVersionName());
			// set dialog message
			alertDialogBuilder
					.setMessage(getResources().getString(R.string.label_about))
					.setCancelable(false)
					.setPositiveButton("Close",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});
		} else if (choice == R.id.action_change_logs) {
			// set title
			alertDialogBuilder.setTitle(getAppVersionName() + " Change Logs");
			// set dialog message
			alertDialogBuilder
					.setMessage(
							getResources()
									.getString(R.string.label_change_logs))
					.setCancelable(false)
					.setPositiveButton("Close",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});
		}

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}

	/*-
	 * Very simply formula
	 * 
	 * Bill per day = Bill Amount / summation (days spent of each person)
	 * Bill per person = Bill per day * total days spent per person
	 * 
	 * @return
	 */
	private boolean computeBill() {
		String totalBill = mTxtTotalBillView.getText().toString();
		View focusView = null;
		boolean cancel = false;

		if (TextUtils.isEmpty(totalBill)) {
			mTxtTotalBillView.setError(getResources().getString(
					R.string.error_input_total_bill));
			focusView = mTxtTotalBillView;
			cancel = true;
		}

		// Check days spent if not empty or < 0
		for (int i = 0; i <= boxMap.size() - 1; i++) {
			BoxView box = boxMap.get(boxMap.keyAt(i));
			String strDaysSpent = box.getTxtTotalDaysSpent().getText()
					.toString();
			if (TextUtils.isEmpty(strDaysSpent)
					|| Double.parseDouble(strDaysSpent) <= 0) {
				box.getTxtTotalDaysSpent().setError(
						getResources().getString(
								R.string.error_input_days_spent));
				focusView = (EditText) box.getTxtTotalDaysSpent();
				cancel = true;
			}
		}

		if (cancel) {
			focusView.requestFocus();
			return false;
		}

		try {

			double totalDaysSpent = 0.0d;
			for (int i = 0; i <= boxMap.size() - 1; i++) {
				Double daysSpent = Double.parseDouble(boxMap
						.get(boxMap.keyAt(i)).getTxtTotalDaysSpent().getText()
						.toString());
				totalDaysSpent += daysSpent;
			}

			double billPerDay = Double.parseDouble(totalBill) / totalDaysSpent;

			for (int i = 0; i <= boxMap.size() - 1; i++) {
				BoxView box = boxMap.get(boxMap.keyAt(i));
				double daysSpent = Double.parseDouble(box
						.getTxtTotalDaysSpent().getText().toString());
				box.getTxtTotalPayment().setText(
						String.valueOf(NumberUtil.RoundTo2Decimals(daysSpent
								* billPerDay)));
			}

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	@Override
	public void update(Observable observable, Object data) {
		synchronized (boxMap) {
			boxMap.remove(Integer.parseInt(String.valueOf(data)));
			if (boxMap.size() >= 1) {
				boxMap.get(boxMap.keyAt(boxMap.size() - 1)).getTxtPersonName()
						.requestFocus();
			}
		}
	}

	/**
	 * 
	 * @return
	 */
	private String getAppVersionName() {
		return getResources().getString(R.string.app_name);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
}
