package com.example.retrovolley;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONObject;
import org.w3c.dom.Text;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddUserActivity extends AppCompatActivity {

    private EditText edtFullname,edtEmail, edtPassword;
    private Button btnSubmit;
    private String typeConn = "retrofit";
    private TextView txtTitleLibrary;

    private  final String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        edtEmail = findViewById(R.id.edt_email);
        edtFullname = findViewById(R.id.edt_fullname);
        edtPassword = findViewById(R.id.edt_password);
        btnSubmit = findViewById(R.id.btn_submit);
        txtTitleLibrary = findViewById(R.id.txt_title_library);

        setTitle(getString(R.string.tambah_user));

        Bundle extras = getIntent().getExtras();
        if(extras!= null) {
            typeConn=extras.getString("typeConnection");
            if (typeConn.equalsIgnoreCase("retrofit"))
                txtTitleLibrary.setText("Send using Retrofit");
        }else txtTitleLibrary.setText("Send using Volley");
    }

    public void actionSubmit(View view) {
        boolean isInputValid = false;

        if (edtFullname.getText().toString().isEmpty()){
            edtFullname.setError("Tidak boleh kosong");
            edtFullname.requestFocus();
            isInputValid=false;
        }else {
            isInputValid = true;
        }

        if (edtEmail.getText().toString().isEmpty()){
            edtEmail.setError("Tidak boleh kosong");
            edtEmail.requestFocus();
            isInputValid = false;
        }else {
            isInputValid = true;
        }

        if (edtPassword.getText().toString().isEmpty()){
            edtPassword.setError("Tidak boleh kosong");
            edtPassword.requestFocus();
            isInputValid=false;
        }else {
            isInputValid=true;
        }

        if (isInputValid){
            User user=new User();
            user.setUser_fullname(edtFullname.getText().toString());
            user.setUser_email(edtEmail.getText().toString());
            user.setUser_password(edtPassword.getText().toString());
            if (typeConn.equalsIgnoreCase("retrofit"))
                submitByRetrofit(user);
            else submitByVolley(user);
        }
    }

    public void submitByRetrofit(User user){

        ProgressDialog proDialog = new ProgressDialog(this);
        proDialog.setTitle(getString(R.string.retrofit));
        proDialog.setMessage("Sedang Disubmit");
        proDialog.show();


        Retrofit.Builder builder= new Retrofit.Builder().baseUrl("http://192.168.1.4/volley/")
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();
        MethodHTTP client= retrofit.create(MethodHTTP.class);
        Call<Request> call =client.sendUser(user);

        call.enqueue(new Callback<Request>() {
            @Override
            public void onResponse(Call<Request> call, Response<Request> response) {
                proDialog.dismiss();
                if (response.body()!=null){
                    if(response.body().getCode() ==201){
                        Toast.makeText(getApplicationContext(), "Response : "
                                +response.body().getStatus(), Toast.LENGTH_SHORT).show();
                        finish();
                    }else if (response.body().getCode()==406){
                        Toast.makeText(getApplicationContext(), "Response : "
                                +response.body().getStatus(), Toast.LENGTH_SHORT).show();
                        edtEmail.requestFocus();
                    }else {
                        Toast.makeText(getApplicationContext(), "Response : "
                                +response.body().getStatus(), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
                Log.e(TAG,"Error : "+response.message());
            }

            @Override
            public void onFailure(Call<Request> call, Throwable t) {

                proDialog.dismiss();
                Log.e(TAG,"Error2 : "+t.getMessage());
            }
        });
    }

    public void submitByVolley(User user){
        Gson gson = new Gson();
        String URL = "http://192.168.1.4/volley/User_Registration.php";

        ProgressDialog proDialog = new ProgressDialog(this);
        proDialog.setTitle("volley");
        proDialog.setMessage("Sedang disubmit");
        proDialog.show();

        String userRequest=gson.toJson(user);
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest request= new JsonObjectRequest(com.android.volley.Request.Method.POST, URL, null,
                new com.android.volley.Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                proDialog.dismiss();
                if (response != null) {
                    Request requestFormat = gson.fromJson(response.toString(), Request.class);
                    if (requestFormat.getCode() == 201) {
                        Toast.makeText(getApplicationContext(), "Response : " + requestFormat.getStatus(),
                                Toast.LENGTH_SHORT).show();
                        finish();
                    } else if (requestFormat.getCode() == 406) {
                        Toast.makeText(getApplicationContext(), "Response : " + requestFormat.getStatus(),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Response : " + requestFormat.getStatus(),
                                Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }

        }, new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                proDialog.dismiss();
                Log.e(TAG, "Error POST Volley : "+error.getMessage() );
            }
        }) {
            @Override
            public byte[] getBody() {
                // return super.getBody();
                return  userRequest.getBytes();
            }
        };

        requestQueue.add(request);
        requestQueue.start();


    }
}