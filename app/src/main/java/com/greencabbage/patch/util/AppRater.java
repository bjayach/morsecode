package com.greencabbage.patch.util;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AppRater  {

    OnDismissListener onDismissCallback;

    public void showRateDialog(Context context) {
        //Context context = StartingPoint.this;
        onDismissCallback = (OnDismissListener)context;
        final Dialog dialog = new Dialog(context);
        dialog.setTitle(context.getString(ResourceUtils.app_name));

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        TextView tv = new TextView(context);
        tv.setText("Please rate this app. A good rating means the world to us.");
        tv.setWidth(240);
        tv.setPadding(4,0,4,10);
        linearLayout.addView(tv);
        final Context finalContext = context;
        Button b1 = new Button(context);
        b1.setText("Rate Now");
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check what the user wants to do
                checkAcceptOrCancel(finalContext);
                dialog.dismiss();
                onDismissCallback.onDismiss("rated_app");
            }
        });
        linearLayout.addView(b1);
        Button b2 = new Button(context);
        b2.setText("Ask Me Later");
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDismissCallback.onDismiss("ask_later");
                dialog.dismiss();
            }
        });
        linearLayout.addView(b2);
        Button b3 = new Button(context);
        b3.setText("Never Ask Again");
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDismissCallback.onDismiss("never_ask_again");
                dialog.dismiss();
            }
        });
        linearLayout.addView(b3);
        dialog.setContentView(linearLayout);
        dialog.show();
    }

    public void checkAcceptOrCancel (Context context) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        final Context finalContext = context;
        builder.setMessage("Do you like this App")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //user clicked yes
                        //Open gplaystore
                        try {
                            finalContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+finalContext.getString(ResourceUtils.package_name))));
                        } catch (Exception e) {
                            Toast.makeText(finalContext, "Unable to open Google Play", Toast.LENGTH_LONG).show();
                        }


                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        //Route to please submit feedback page
                        startSubmitCommentActivity(finalContext);
                    }
                });
        // Create the AlertDialog object and return it
         builder.create().show();
        //return false;
    }

    private void startSubmitCommentActivity(Context context) {
        CabbageUtils cabbageUtils = new CabbageUtils();
        // Start Activity Feedback
        Intent commentIntent = new Intent(
                "com.greencabbage.patch.activity.SUBMITCOMMENT");
        ResolveInfo rInfo = cabbageUtils.getResolveInfoForActivity(
                context, commentIntent);
        if (rInfo != null) {
            commentIntent.setPackage(rInfo.activityInfo.packageName);
        }
        //startActivity(feedbackIntent);
        ((Activity)context).startActivityForResult(commentIntent,ResourceUtils.activity_id);
    }
    public interface OnDismissListener {
        void onDismiss(String value);
    }
}
