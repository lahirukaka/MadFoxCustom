package madfox.colhh.madfoxcustom.dialogs;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class FlexibleDialogBox extends DialogFragment implements DialogInterface.OnClickListener {
	
	private String title;
	private int icon;
	private String msg;
	private String[] btn_text;
	private boolean[] btn_draw;
	private int layout;
	private boolean cancelable;

	public FlexibleDialogBox() {
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		if(savedInstanceState!=null) restoreBundle(savedInstanceState);
		
		AlertDialog.Builder builder = new Builder(getActivity());
		//setting
		if(layout>0)
		{
			builder.setView(getActivity().getLayoutInflater().
					inflate(layout, null));
		}
		if(title!=null) builder.setTitle(title);
		if(icon>0) builder.setIcon(icon);
		if(msg!=null) builder.setMessage(msg);
		if(btn_draw!=null)
		{
			if(btn_draw[0]) builder.setPositiveButton(btn_text[0], this);
			if(btn_draw[1]) builder.setNegativeButton(btn_text[1], this);
			if(btn_draw[2]) builder.setNegativeButton(btn_text[2], this);
		}
		builder.setCancelable(cancelable);
		builder.setInverseBackgroundForced(false);

		return builder.create();
	}

	private void restoreBundle(Bundle in) {
		title=in.getString("title");
		msg=in.getString("msg");
		btn_text=in.getStringArray("btn_text");
		icon=in.getInt("icon",0);
		layout=in.getInt("layout",0);
		btn_draw=in.getBooleanArray("btn_draw");
		cancelable=in.getBoolean("cancel",true);
	}

	@Override
	public void onSaveInstanceState(Bundle out) {
		super.onSaveInstanceState(out);
		out.putString("title", title);
		out.putString("msg", msg);
		out.putStringArray("btn_text", btn_text);
		out.putInt("icon", icon);
		out.putInt("layout", layout);
		out.putBooleanArray("btn_draw", btn_draw);
		out.putBoolean("cancel", cancelable);
	}
	
	@Override
	public void onDismiss(DialogInterface dialog) {
		super.onDismiss(dialog);
		if(EL!=null) EL.onCancel(null);
	}
	
	/*Getters & Setters*/
	/**
	 * @param title the title to set
	 */
	public final FlexibleDialogBox setTitle(String title) {
		this.title = title;
		return this;
	}

	/**
	 * @param icon the icon to set
	 */
	public final FlexibleDialogBox setIcon(int icon) {
		this.icon = icon;
		return this;
	}

	/**
	 * @param msg the msg to set
	 */
	public final FlexibleDialogBox setMsg(String msg) {
		this.msg = msg;
		return this;
	}

	/**
	 * @param btn_text the btn_text to set
	 */
	public final FlexibleDialogBox setBtn_text(String[] btn_text) {
		this.btn_text = btn_text;
		return this;
	}

	/**
	 * @param btn_draw the btn_draw to set
	 */
	public final FlexibleDialogBox setBtn_draw(boolean[] btn_draw) {
		this.btn_draw = btn_draw;
		return this;
	}

	/**
	 * @param layout the layout to set
	 */
	public final FlexibleDialogBox setLayout(int layout) {
		this.layout = layout;
		return this;
	}

	/*Interface stuff*/
	EventListener EL;
	interface EventListener
	{
		public void onPositiveReturn(Object value);
		public void onNegativeReturn(Object value);
		public void onNeutralReturn(Object value);
		public void onCancel(Object value);
	}
	public void registerEventListener(EventListener EL)
	{
		this.EL=EL;
	}
	
	/*Listener*/
	@Override
	public void onClick(DialogInterface dialog, int which) {
		if(EL!=null)
		{
			switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					EL.onPositiveReturn(null);
					break;
				case DialogInterface.BUTTON_NEGATIVE:
					EL.onNegativeReturn(null);
					break;
				case DialogInterface.BUTTON_NEUTRAL:
					EL.onNeutralReturn(null);
					break;
				default:
					break;
			}
		}
	}
}
