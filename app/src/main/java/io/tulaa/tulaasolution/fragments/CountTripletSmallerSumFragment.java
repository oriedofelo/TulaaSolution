package io.tulaa.tulaasolution.fragments;

import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.app.ProgressDialog;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import io.tulaa.tulaasolution.R;
import io.tulaa.tulaasolution.application.TulaaApplication;
import io.tulaa.tulaasolution.configs.Config;
import io.tulaa.tulaasolution.models.InterviewRequest;
import io.tulaa.tulaasolution.models.InterviewResponse;
import io.tulaa.tulaasolution.utils.HttpsTrustManager;

public class CountTripletSmallerSumFragment extends Fragment {
    private EditText tripletInputEditText;
    private EditText tripletInputEditTextSum;
    private Button activateButton;

    @Override
    public android.view.View onCreateView(LayoutInflater inflater, ViewGroup container,
                             android.os.Bundle savedInstanceState) {
        android.view.View view = inflater.inflate(io.tulaa.tulaasolution.R.layout.counttriplet_smaller_sum_fragment, container, false);
        tripletInputEditText = (EditText)view.findViewById(R.id.tripletInputEditText);
        tripletInputEditTextSum = (EditText)view.findViewById(R.id.tripletInputEditTextSum);
        activateButton= (Button)view.findViewById(R.id.activate_button);
        return view;
     }

    @Override
    public void onResume(){
        super.onResume();
        setListeners();
    }

    public void setListeners(){
        activateButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(tripletInputEditTextSum.getText().toString().length() > 0){
                    try {
                        Integer.parseInt(tripletInputEditTextSum.getText().toString());
                    }
                    catch(Exception ex){
                        Toast.makeText(CountTripletSmallerSumFragment.this.getActivity(),"Sum must be input and must be a valid number",Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                else {
                    Toast.makeText(CountTripletSmallerSumFragment.this.getActivity(),"Sum must be input",Toast.LENGTH_LONG).show();
                    return;
                }

                if(tripletInputEditText.getText().toString().length() > 0) {
                    try {
                        String inputText=tripletInputEditText.getText().toString();
                        String[] strArray = inputText.split(",");
                        int[] intArray = new int[strArray.length];
                        for(int i = 0; i < strArray.length; i++) {
                            intArray[i] = Integer.parseInt(strArray[i]);
                        }
                        int sum=Integer.parseInt(tripletInputEditTextSum.getText().toString());

                        sendHttpRequest(intArray,sum);}
                    catch(Exception ex){
                        Toast.makeText(CountTripletSmallerSumFragment.this.getActivity(),"Please check input, numbers should be separated by a , e.g. 3,4,5",Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    Toast.makeText(CountTripletSmallerSumFragment.this.getActivity(),"Please check input, numbers should be separated by a , e.g. 3,4,5",Toast.LENGTH_LONG).show();

                }

            }
        });
    }

    public void sendHttpRequest(int[] intArray,int sum){
        final ProgressDialog progressDialog = new ProgressDialog(CountTripletSmallerSumFragment.this.getActivity());
        progressDialog.setCancelable(true);
        progressDialog.setMessage("Performing operation...");
        progressDialog.show();
        InterviewRequest user = new InterviewRequest();
        user.setCountTripletsInput(intArray);
        user.setCountTripletInputSum(sum);
        String request = new Gson().toJson(user);
        JsonObjectRequest req = null;
        try {
            HttpsTrustManager.allowAllSSL();
            req = new JsonObjectRequest(Config.BASE_URL + Config.COUNT_TRIPLETS_SMALLER_SUM_ENDPOINT, new JSONObject(request),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                InterviewResponse interviewResponse = new Gson().fromJson(response.toString(), InterviewResponse.class);
                                progressDialog.dismiss();

                                showDialog("Count is "+interviewResponse.getCountTripletsSmallerSumResponse());

                            } catch (Exception ex) {
                                Log.e("Exception interview ",ex.getMessage());

                                progressDialog.dismiss();
                                Toast.makeText(CountTripletSmallerSumFragment.this.getActivity(), "Check internet connection", Toast.LENGTH_LONG).show();

                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(error.getMessage() != null && error.getMessage().contains("JSONException")){
                        progressDialog.dismiss();
                        Toast.makeText(CountTripletSmallerSumFragment.this.getActivity(), "Check internet connection", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Log.e("Error interview ",error.getMessage());
                        progressDialog.dismiss();
                        Toast.makeText(CountTripletSmallerSumFragment.this.getActivity(), "Check internet connection", Toast.LENGTH_LONG).show();
                    }

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
            progressDialog.dismiss();
        }
        req.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //Creating a Request Queue
        RequestQueue requestQueue = TulaaApplication.getRequestQueue(CountTripletSmallerSumFragment.this.getActivity());
        requestQueue.add(req);
    }

    public void getIfThereIsATriplet(){
        String input = tripletInputEditText.getText().toString();
        if(input.length() > 0){
            //proceed
        }
        else {
            Toast.makeText(CountTripletSmallerSumFragment.this.getContext(),"Please input index k",Toast.LENGTH_SHORT).show();
        }
    }

    public void showDialog(String message) {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(CountTripletSmallerSumFragment.this.getActivity());
        builder1.setMessage(message);
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();


    }
}
