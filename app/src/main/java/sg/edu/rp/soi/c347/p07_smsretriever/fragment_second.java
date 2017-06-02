package sg.edu.rp.soi.c347.p07_smsretriever;


import android.Manifest;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.PermissionChecker;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class fragment_second extends Fragment {
    Button btnSMSRetrieve2;
    TextView tvFrag2;
    EditText et2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_second, container, false);

        btnSMSRetrieve2 = (Button) view.findViewById(R.id.btnSMSRetrieve2);
        et2 = (EditText) view.findViewById(R.id.et2);
        tvFrag2 = (TextView) view.findViewById(R.id.tvFrag2);


        btnSMSRetrieve2.setOnClickListener(new View.OnClickListener() {
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




                String word = et2.getText().toString();

                String[] split = word.split(" ");

                for (int i = 0; i < split.length; i++) {
                    Uri uri = Uri.parse("content://sms");
                    String[] reqCols = new String[]{"date", "address", "body", "type"};
                    ContentResolver cr = getActivity().getContentResolver();
                    String filter = "body LIKE ?";
                    String[] filterArgs = {"%" + split[i] + "%"};
                    Cursor cursor = cr.query(uri, reqCols, filter, filterArgs, null);

                    String smsBody = "";

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
                    tvFrag2.setText(smsBody);
                }
            }
        });
        return view;
    }

}
