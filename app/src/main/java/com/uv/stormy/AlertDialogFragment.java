package com.uv.stormy;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;

public class AlertDialogFragment extends DialogFragment {

    // Similar to onCreate(...) method in Activity
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Return the Activity this fragment is currently associated with.
        Context context = getActivity();

        // Creating dialogs involves Buidler Pattern
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.error_title)
                .setMessage(R.string.error_message)
                .setPositiveButton(R.string.error_ok_button_text, null); // null is a onClickListener

        // create actual AlertDialog from builder
        AlertDialog dialog = builder.create();

        return dialog;
    }
}
