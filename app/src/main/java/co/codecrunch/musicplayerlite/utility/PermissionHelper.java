package co.codecrunch.musicplayerlite.utility;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.codecrunch.musicplayerlite.activities.MusicPlayerBaseActivity;


/**
 * Created by Jinesh Soni on 22/09/2016.
 */

public class PermissionHelper  {

	MusicPlayerBaseActivity activity;
	final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;

	public PermissionHelper(MusicPlayerBaseActivity activity) {
		this.activity = activity;
	}


	//Static permission check
	public void checkPermission() {
		List<String> permissionsNeeded = new ArrayList<String>();

		final List<String> permissionsList = new ArrayList<String>();
		if (!addPermission(permissionsList, Manifest.permission.READ_EXTERNAL_STORAGE))
			permissionsNeeded.add("Read File - to get songs from your device");


		if (permissionsList.size() > 0) {
			if (permissionsNeeded.size() > 0) {
				// Need Rationale
				String message = "You need to grant access to " + permissionsNeeded.get(0);
				for (int i = 1; i < permissionsNeeded.size(); i++)
					message = message + ", " + permissionsNeeded.get(i);
				showMessageOKCancel(message,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
									activity.requestPermissions(permissionsList.toArray(new String[permissionsList.size()]), REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
								}
							}
						});
				return;
			}
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				activity.requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
						REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
			}
			return;
		}
	}


	//Automated permission check
	public void checkPermission(List<String> permissions) {

		final List<String> permissionsList = new ArrayList<String>();

		for(int i=0;i<permissions.size();i++){
			if (!addPermission(permissionsList, permissions.get(i)));
		}


		if (permissionsList.size() > 0) {
			showMessageOKCancel("App need few permissions to work on",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
								activity.requestPermissions(permissionsList.toArray(new String[permissionsList.size()]), REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
							}
						}
					});
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				activity.requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
						REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
			}
			return;
		}
	}

	private boolean addPermission(List<String> permissionsList, String permission) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (activity.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
				permissionsList.add(permission);
				// Check for Rationale Option
				if (!activity.shouldShowRequestPermissionRationale(permission))
					return false;
			}
		}
		return true;
	}

	private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
		new AlertDialog.Builder(activity)
				.setMessage(message)
				.setPositiveButton("Ok", okListener)
				.setNegativeButton("Cancel", null)
				.create()
				.show();
	}

}
