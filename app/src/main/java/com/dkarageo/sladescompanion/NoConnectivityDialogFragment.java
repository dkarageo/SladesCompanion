package com.dkarageo.sladescompanion;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.dkarageo.sladescompanion.R;
import com.dkarageo.sladescompanion.preferences.PreferencesActivity;
import com.dkarageo.sladescompanion.preferences.PreferencesController;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;


public class NoConnectivityDialogFragment extends DialogFragment {

    public static NoConnectivityDialogFragment newInstance(boolean firstRun) {
        NoConnectivityDialogFragment f = new NoConnectivityDialogFragment();

        Bundle args = new Bundle();
        args.putBoolean("first_run", firstRun);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setPositiveButton(
                getString(R.string.no_connectivity_dialog_positive_button_text),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getContext(), PreferencesActivity.class));
                    }
                });

        builder.setNegativeButton(
                getString(R.string.no_connectivity_dialog_negative_button_text),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try { getActivity().finish(); } catch (NullPointerException ex) {}
                    }
                });

        Bundle args = getArguments();
        if (args != null && args.getBoolean("first_run", false)) {
            builder.setMessage(R.string.no_connectivity_dialog_welcome_message);
            builder.setTitle(R.string.no_connectivity_dialog_welcome_title);
        } else {
            builder.setMessage(R.string.no_connectivity_dialog_message);
        }

        return builder.create();
    }

}
