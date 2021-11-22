package com.example.retrovolley;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class UserAdapter extends ArrayAdapter<User> {
    public UserAdapter(Context context, List<User> objects) {
        super(context, objects.hashCode()); }

    @NonNull
    @Override
    public View getView (int position, @Nullable View convertview, @NonNull ViewGroup parent){
        User user = getItem(position);
        if (convertview == null){
            convertview = LayoutInflater.from(getContext()).inflate(R.layout.item_user, parent, false);

        }

        TextView txtFullname = convertview.findViewById(R.id.txt_user_fullname);
        TextView txtEmail = convertview.findViewById(R.id.txt_user_email);
        TextView txtId = convertview.findViewById(R.id.txt_id);

        txtFullname.setText(user.getUser_fullname());
        txtEmail.setText(user.getUser_email());
        txtId.setText(user.getId());

        return convertview;
    }
}
