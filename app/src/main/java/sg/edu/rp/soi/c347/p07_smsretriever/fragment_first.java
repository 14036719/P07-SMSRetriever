package sg.edu.rp.soi.c347.p07_smsretriever;


import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.PermissionChecker;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import static android.content.ContentValues.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class fragment_first extends Fragment {
    Button btnSMSRetrieve, btnEmail;
    EditText et1;
    TextView tvFrag1;
    String smsBody = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_first, container, false);
        btnSMSRetrieve = (Button) view.findViewById(R.id.btnSMSRetrieve);
        btnEmail = (Button) view.findViewById(R.id.btnEmail);
        tvFrag1 = (TextView) view.findViewById(R.id.tvFrag1);
        et1 = (EditText) view.findViewById(R.id.et1);

        btnSMSRetrieve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                int permissionCheck = PermissionChecker.checkSelfPermission
                        (getActivity(), Manifest.permission.READ_SMS);

                if (permissionCheck != PermissionChecker.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.READ_SMS}, 0);
                    // stops the action from proceeding further as permission not
                    //  granted yet
                    return;
                }

                String num = et1.getText().toString();

                Uri uri = Uri.parse("content://sms");
                String[] reqCols = new String[]{"date", "address", "body", "type"};
                ContentResolver cr = getActivity().getContentResolver();
                String filter = "address = ?";
                String[] filterArgs = {num};
                Cursor cursor = cr.query(uri, reqCols, filter, filterArgs, null);

                if (cursor.moveToFirst()) {
                    do {
                        long dateInMillis = cursor.getLong(0);
                        String date = (String) DateFormat.format("dd MM yyyy h:mm:ss aa", dateInMillis);
                        String address = cursor.getString(1);
                        String body = cursor.getString(2);
                        String type = cursor.getString(3);

                        if (type.equalsIgnoreCase("1")) {
                            type = "Inbox:";
                            smsBody += type + " " + address + "\n at " + date + "\n\"" + body + "\"\n\n";
                        }
                    }
                    while (cursor.moveToNext());
                }
                tvFrag1.setText(smsBody);
                Log.d("sdf", smsBody+"");
            }
        });

        btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{"jason_lim@rp.edu.sg"});
                email.putExtra(Intent.EXTRA_SUBJECT, "Message from my SMS database");
                email.putExtra(Intent.EXTRA_TEXT, smsBody);
                email.setType("message/rfc822");
                startActivity(Intent.createChooser(email, "Choose an Email client: "));
            }
        });
        return view;
    }

}
