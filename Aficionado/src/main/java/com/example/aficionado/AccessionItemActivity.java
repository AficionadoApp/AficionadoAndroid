package com.example.aficionado;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aficionado.data.Callback;
import com.example.aficionado.data.Comment;
import com.example.aficionado.data.MuseumItem;
import com.example.aficionado.data.RestClient;
import com.example.aficionado.data.ServerGet;
import com.example.aficionado.data.ServerPost;

import org.apache.http.HttpResponse;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import static com.example.aficionado.R.id.imageView;

public class AccessionItemActivity extends Activity {
    private RestClient mRestClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();

            mRestClient = new RestClient();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.paiting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public class PlaceholderFragment extends Fragment {
        private EditText mAccessionNumberEdit;
        private EditText mCommentInput;
        private EditText mNameInput;
        private ListView mResult;
        private TextView mTitle;
        private String mAccessionNumber;

        private UrlImageView mImageView;

        private RelativeLayout mCommentContainer;


        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_main, container, false);

            mCommentInput = (EditText) view.findViewById(R.id.comment_input);
            mNameInput = (EditText) view.findViewById(R.id.name_input);
            mTitle = (TextView) view.findViewById(R.id.item_title);

            mImageView = (UrlImageView) view.findViewById(imageView);

            mCommentContainer = (RelativeLayout) view.findViewById(R.id.commentContainer);

            mAccessionNumberEdit = (EditText) view.findViewById(R.id.accessionNumber);
            final Button accessionNumberGo = (Button) view.findViewById(R.id.accessionNumberGo);

            mResult = (ListView) view.findViewById(R.id.result);

            accessionNumberGo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mImageView.setVisibility(View.GONE);
                    mAccessionNumber = mAccessionNumberEdit.getText().toString();
                    ServerGet request = new ServerGet(new Callback<MuseumItem>() {
                        @Override
                        public void success(MuseumItem mi) {
                            CommentAdapter commentAdapter =
                                    new CommentAdapter(getActivity(), R.layout.comment_item, mi.getComments());
                            mResult.setAdapter(commentAdapter);
                            mTitle.setText(mi.getTitle());
                            try {
                                mImageView.setImageURL(new URL(mi.getmImageUrl()));
                                mImageView.setVisibility(View.VISIBLE);
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void failure() {
                            mResult.setAdapter(null);
                            Toast.makeText(getActivity(), "Network Fail", 1000).show();
                        }
                    });

                    request.execute(mAccessionNumber);

                    clearKeyboard(mAccessionNumberEdit);

                    mCommentContainer.setVisibility(View.VISIBLE);
                }
            });

            Button postButton = (Button) view.findViewById(R.id.post_button);

            postButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ServerPost post = new ServerPost(new Callback<HttpResponse>() {
                        @Override
                        public void success(HttpResponse result) {
                            Toast.makeText(getActivity(), "Posted successfully!", 1000).show();
                            accessionNumberGo.callOnClick();
                        }

                        @Override
                        public void failure() {
                            Toast.makeText(getActivity(), "Post failed.", 1000).show();
                        }
                    });

                    post.execute(new Comment(mAccessionNumber, mCommentInput.getText().toString(),
                            mNameInput.getText().toString(), new Date()));
                    mCommentInput.setText("");
                    clearKeyboard(mCommentInput);
                }
            });


            return view;
        }

        public void clearKeyboard(EditText editText) {
            InputMethodManager imm = (InputMethodManager)getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
    }

    public class CommentAdapter extends ArrayAdapter<Comment> {
        private final Context context;
        private final Comment[] values;


        public CommentAdapter(Context context, int resource, Comment[] comments) {
            super(context, resource, comments);
            this.context = context;
            this.values = comments;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater =
                    (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.comment_item, parent, false);
            TextView commentBody = (TextView) rowView.findViewById(R.id.comment_body);
            TextView name = (TextView) rowView.findViewById(R.id.comment_name);
            commentBody.setText(values[position].getComment());
            name.setText(values[position].getName());

            return rowView;
        }
    }
}
