package app.com.classmates.multipleclasses;


import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import app.com.classmates.Global;
import app.com.classmates.R;
import app.com.classmates.fonts.Fontchange;


public class ChatArrayAdapter extends BaseAdapter {


    private TextView chatText, msg_chat_name_TV, addedto_chat_name_TV;
    private ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
    private Context context;
    Global global;
    SharedPreferences sp;
    String added;

    public ChatArrayAdapter(Context context, ArrayList<HashMap<String, String>> list) {
        global = (Global) context.getApplicationContext();
        sp = context.getSharedPreferences(GlobalConstants.spName, Context.MODE_PRIVATE);
        this.context = context;
        this.list = list;
        notifyDataSetChanged();
    }

    public int getCount() {
        return list.size();
    }

    public Object getItem(int index) {
        return index;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        try {
            String user_id = list.get(position).get("user_id");
            String userids = sp.getString(GlobalConstants.user_id, "");
            if (user_id.equalsIgnoreCase(userids)) {
                row = inflater.inflate(R.layout.right_chat, parent, false);
                Fontchange.overrideFonts(context, ((LinearLayout) row.findViewById(R.id.left_main_LL)));
                addedto_chat_name_TV = (TextView) row.findViewById(R.id.addedto_chat_name_TV);

            } else {
                row = inflater.inflate(R.layout.left_chat, parent, false);
                Fontchange.overrideFonts(context, ((LinearLayout) row.findViewById(R.id.right_main_LL)));
                msg_chat_name_TV = (TextView) row.findViewById(R.id.msg_chat_name_TV);
                msg_chat_name_TV.setVisibility(View.VISIBLE);
                String name = list.get(position).get("fname") + " " + list.get(position).get("lname");
                msg_chat_name_TV.setText(name);
                addedto_chat_name_TV = (TextView) row.findViewById(R.id.addedto_chat_name_TV);
            }

            chatText = (TextView) row.findViewById(R.id.msgr);
            chatText.setText(list.get(position).get("message"));


            Log.e("typeeeeeee name ", list.get(position).get("type"));

            Log.e("myyy saved name ", sp.getString(GlobalConstants.user_id, ""));
            Log.e("coming name ", user_id);

            if (list.get(position).get("type").equalsIgnoreCase("join")) {
                if (user_id.equalsIgnoreCase(sp.getString(GlobalConstants.user_id, ""))) {
                    chatText.setVisibility(View.GONE);
                    addedto_chat_name_TV.setVisibility(View.VISIBLE);
                    addedto_chat_name_TV.setText("You were Added.");
                } else {
                    chatText.setVisibility(View.GONE);
                    msg_chat_name_TV.setVisibility(View.GONE);
                    addedto_chat_name_TV.setVisibility(View.VISIBLE);

                    added = list.get(position).get("fname") + " " + list.get(position).get("lname") + " is Added.";
                    addedto_chat_name_TV.setText(added);
                }

            } else {
                addedto_chat_name_TV.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            Log.wtf("Exception in chat array Adapter", e + "");
        }
        return row;
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

}


