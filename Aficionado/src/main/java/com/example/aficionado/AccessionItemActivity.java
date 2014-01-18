package com.example.aficionado;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.KeyEvent;
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
    private PlaceholderFragment mPlaceholderFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            mPlaceholderFragment = new PlaceholderFragment();
            getFragmentManager().beginTransaction()
                    .add(R.id.container, mPlaceholderFragment)
                    .commit();

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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && !mPlaceholderFragment.isAccessionVisible()) {
            mPlaceholderFragment.onBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
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
        private Button accessionNumberGo;

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
            accessionNumberGo = (Button) view.findViewById(R.id.accessionNumberGo);

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

                    mAccessionNumberEdit.setVisibility(View.GONE);
                    accessionNumberGo.setVisibility(View.GONE);

                    mCommentContainer.setVisibility(View.VISIBLE);
                }
            });

            Button postButton = (Button) view.findViewById(R.id.post_button);

            postButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    postComment();
                }
            });

            mCommentInput.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View view, int i, KeyEvent keyEvent) {
                    if(keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                        postComment();
                    }
                    return false;
                }
            });


            return view;
        }

        private void postComment() {
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

        public void clearKeyboard(EditText editText) {
            InputMethodManager imm = (InputMethodManager)getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }

        public void onBack() {
            mAccessionNumberEdit.setVisibility(View.VISIBLE);
            accessionNumberGo.setVisibility(View.VISIBLE);
        }

        public boolean isAccessionVisible() {
            return mAccessionNumberEdit.getVisibility() == View.VISIBLE;
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
