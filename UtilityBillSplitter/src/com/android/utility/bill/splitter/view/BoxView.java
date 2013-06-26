package com.android.utility.bill.splitter.view;

import java.util.Observable;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.android.utility.bill.splitter.activity.R;

/**
 * This class creates the box row of when the user clicks "+" to add a person.
 * 
 * @author jantox
 * 
 */
public class BoxView extends Observable {
	private Context context;
	private EditText txtPersonName;
	private EditText txtDaysSpent;
	private EditText txtPayment;
	private ImageButton btnDeletePerson;
	private View view;

	private int counterId;

	public BoxView(Context context, int counterId) {
		this.context = context;
		this.counterId = counterId;

		init();
	}

	private void init() {
		LayoutInflater inflator = LayoutInflater.from(context);
		this.view = inflator.inflate(R.layout.box, null);
		this.view.setId(counterId);

		this.txtPersonName = (EditText) view.findViewById(R.id.txtPerson);
		this.txtDaysSpent = (EditText) view.findViewById(R.id.txtDaysStayed);
		this.txtPayment = (EditText) view.findViewById(R.id.txtPayment);
		this.btnDeletePerson = (ImageButton) view
				.findViewById(R.id.btnDeletePerson);

		this.txtPersonName.setId(counterId);
		this.txtDaysSpent.setId(counterId);
		this.txtPayment.setId(counterId);
		this.btnDeletePerson.setId(counterId);

		this.txtPayment.setText("0.00");
		this.txtPersonName.setHint(Html.fromHtml("<small>"
				+ "Input name" + "</small>"));
		this.txtDaysSpent.setHint(Html.fromHtml("<small>"
				+ "Input days" + "</small>"));

		this.txtPersonName.requestFocus();
		
		this.btnDeletePerson.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((LinearLayout) getView().getParent()).removeView(getView());
				setChanged();
				notifyObservers(counterId);
			}
		});

	}

	public int getId() {
		return counterId;
	}

	public View getView() {
		return view;
	}

	public EditText getTxtPersonName() {
		return txtPersonName;
	}

	public void setTxtPersonName(EditText txtPersonName) {
		this.txtPersonName = txtPersonName;
	}

	public EditText getTxtTotalDaysSpent() {
		return txtDaysSpent;
	}

	public void setTxtTotalDaysSpent(EditText txtTotalDaysSpent) {
		this.txtDaysSpent = txtTotalDaysSpent;
	}

	public EditText getTxtTotalPayment() {
		return txtPayment;
	}

	public void setTxtTotalPayment(EditText txtTotalPayment) {
		this.txtPayment = txtTotalPayment;
	}

	public ImageButton getBtnDeletePerson() {
		return btnDeletePerson;
	}

	public void setBtnDeletePerson(ImageButton btnDeletePerson) {
		this.btnDeletePerson = btnDeletePerson;
	}

}