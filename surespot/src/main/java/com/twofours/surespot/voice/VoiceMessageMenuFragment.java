package com.twofours.surespot.voice;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.twofours.surespot.R;
import com.twofours.surespot.activities.MainActivity;
import com.twofours.surespot.chat.SurespotMessage;
import com.twofours.surespot.identity.IdentityController;
import com.twofours.surespot.network.IAsyncCallback;
import com.twofours.surespot.ui.UIUtils;

import java.util.ArrayList;

public class VoiceMessageMenuFragment extends DialogFragment {
	protected static final String TAG = "VoiceMessageMenuFragment";
	private SurespotMessage mMessage;
	private ArrayList<String> mItems;
//	private BillingController mBillingController;

	public static DialogFragment newInstance(SurespotMessage message) {
		VoiceMessageMenuFragment f = new VoiceMessageMenuFragment();

		Bundle args = new Bundle();
		args.putString("message", message.toJSONObject().toString());
		f.setArguments(args);

		return f;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		String messageString = getArguments().getString("message");
		if (messageString != null) {
			mMessage = SurespotMessage.toSurespotMessage(messageString);
		}

		final MainActivity mActivity = (MainActivity) getActivity();

	//	mBillingController = SurespotApplication.getBillingController();

		mItems = new ArrayList<String>(2);

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// builder.setTitle(R.string.pick_color);

		// nag nag nag
//		if (!mBillingController.hasVoiceMessaging()) {
//			mItems.add(getString(R.string.menu_purchase_voice_messaging));
//		}

		// can always delete
		mItems.add(getString(R.string.menu_delete_message));

		builder.setItems(mItems.toArray(new String[mItems.size()]), new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialogi, int which) {
				if (mMessage == null)
					return;

				String itemText = mItems.get(which);

				if (itemText.equals(getString(R.string.menu_delete_message))) {
					SharedPreferences sp = mActivity.getSharedPreferences(IdentityController.getLoggedInUser(), Context.MODE_PRIVATE);
					boolean confirm = sp.getBoolean("pref_delete_message", true);
					if (confirm) {
						AlertDialog dialog = UIUtils.createAndShowConfirmationDialog(mActivity, getString(R.string.delete_message_confirmation_title),
								getString(R.string.delete_message), getString(R.string.ok), getString(R.string.cancel), new IAsyncCallback<Boolean>() {
									public void handleResponse(Boolean result) {
										if (result) {
											mActivity.getChatController().deleteMessage(mMessage);
										}
										else {
											dialogi.cancel();
										}
									};
								});
						mActivity.setChildDialog(dialog);
					}
					else {
						mActivity.getChatController().deleteMessage(mMessage);
					}

					return;
				}

//				if (itemText.equals(getString(R.string.menu_purchase_voice_messaging))) {
//					mActivity.showVoicePurchaseDialog(false);
//					return;
//				}

			}
		});

		AlertDialog dialog = builder.create();
		return dialog;
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		super.onDismiss(dialog);
		MainActivity activity = (MainActivity) getActivity();
		if (activity != null) {
			activity.setButtonText();
		}
	}	
}
