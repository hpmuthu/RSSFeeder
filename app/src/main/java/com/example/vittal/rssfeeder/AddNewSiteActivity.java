package com.example.vittal.rssfeeder;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class AddNewSiteActivity extends AppCompatActivity {

    Button btnSubmit;
    Button btnCancel;
    EditText txtUrl;
    TextView lblMessage;

    RSSParser rssParser = new RSSParser();

    RSSFeed rssFeed;

    // Progress Dialog
    private ProgressDialog pDialog;

    private MySQLiteHelper mySQLiteHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_site);

        mySQLiteHelper = new MySQLiteHelper(this, null, null, 2);

        // buttons
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        txtUrl = (EditText) findViewById(R.id.txtUrl);
        lblMessage = (TextView) findViewById(R.id.lblMessage);

        // Submit button click event
        btnSubmit.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                String url = txtUrl.getText().toString();

                // Validation url
                Log.d("URL Length", "" + url.length());
                // check if user entered any data in EditText
                if (url.length() > 0) {
                    lblMessage.setText("");
                    String urlPattern = "^http(s{0,1})://[a-zA-Z0-9_/\\-\\.]+\\.([A-Za-z/]{2,5})[a-zA-Z0-9_/\\&\\?\\=\\-\\.\\~\\%]*";
                    if (url.matches(urlPattern)) {
                        // valid url
                        new loadRSSFeed().execute(url);
                    } else {
                        // URL not valid
                        lblMessage.setText("Please enter a valid url");
                    }
                } else {
                    // Please enter url
                    lblMessage.setText("Please enter website url");
                }

            }
        });

        // Cancel button click event
        btnCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_add) {
            Intent i = new Intent(getApplicationContext(), AddNewSiteActivity.class);
            // starting new activity and expecting some response back
            // depending on the result will decide whether new website is
            // added to SQLite database or not
            startActivityForResult(i, 100);
            return true;
        }

        if (item.getItemId() == R.id.action_logout) {
            SessionManager sessionManager = new SessionManager(getApplicationContext());
            sessionManager.logoutUser();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Background Async Task to get RSS data from URL
     * */
    class loadRSSFeed extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AddNewSiteActivity.this);
            pDialog.setMessage("Fetching RSS Information ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting Inbox JSON
         * */
        @Override
        protected String doInBackground(String... args) {
            String url = args[0];
            rssFeed = rssParser.getRSSFeed(url);
            Log.d("rssFeed", " "+ rssFeed);
            if (rssFeed != null) {
                Log.e("RSS URL",
                        rssFeed.getTitle() + "" + rssFeed.getLink() + ""
                                + rssFeed.getDescription() + ""
                                + rssFeed.getLanguage());

                SessionManager sessionManager = new SessionManager(AddNewSiteActivity.this);
                int user_id = Integer.parseInt(sessionManager.getCurrentUserId());
                WebSite site = new WebSite(user_id, rssFeed.getTitle(), rssFeed.getLink(), rssFeed.getRSSLink(),
                        rssFeed.getDescription());
                mySQLiteHelper.addSite(site);
                Intent i = getIntent();
                // send result code 100 to notify about product update
                setResult(100, i);
                finish();
            } else {
                // updating UI from Background Thread
                runOnUiThread(new Runnable() {
                    public void run() {
                        lblMessage.setText("Rss url not found. Please check the url or try again");
                    }
                });
            }
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String args) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    if (rssFeed != null) {

                    }

                }
            });

        }

    }
}
