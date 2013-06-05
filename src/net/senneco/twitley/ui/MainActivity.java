package net.senneco.twitley.ui;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewSwitcher;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import net.senneco.twitley.R;
import net.senneco.twitley.data.ListTwits;
import net.senneco.twitley.data.Twit;
import net.senneco.twitley.data.User;
import net.senneco.twitley.request.GsonRequest;
import net.senneco.twitley.request.LruBitmapCache;
import net.senneco.twitley.request.ObtainTokenRequest;
import net.senneco.twitley.request.Urls;

import java.util.ArrayList;

/**
 * Created by senneco on 21.05.13.
 */
public class MainActivity extends Activity implements ObtainTokenRequest.OnTokenObtainListener, ErrorListener, View.OnClickListener {

    private static final String TOKEN = "token";
    private static final String TWITS = "twits";

    private ViewSwitcher mLoadingCont;
    private ViewSwitcher mListFooter;
    private TextView mLoadingErrorText;
    private TwitsAdapter mTwitsAdapter;
    private ArrayList<Twit> mTwits;

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private String mToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initVolley();

        restoreSavedState(savedInstanceState);

        initLoadingViews();

        initTwitsList();

        if (TextUtils.isEmpty(mToken)) {
            mRequestQueue.add(new ObtainTokenRequest(this, this));
        } else {
            onTokenObtain(mToken);
        }
    }

    private void initVolley() {
        mRequestQueue = Volley.newRequestQueue(this);
        mImageLoader = new ImageLoader(mRequestQueue, new LruBitmapCache(5 * 1024 * 1024)); // 5MiB
    }

    private void restoreSavedState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mToken = savedInstanceState.getString(TOKEN);
            //noinspection unchecked
            mTwits = (ArrayList<Twit>) savedInstanceState.getSerializable(TWITS);
        }

        if (mTwits == null) mTwits = new ArrayList<Twit>();
    }

    private void initLoadingViews() {
        mLoadingCont = (ViewSwitcher) findViewById(R.id.cont_loading);
        mListFooter = (ViewSwitcher) getLayoutInflater().inflate(R.layout.item_loading, null);

        if (!mTwits.isEmpty()) {
            mLoadingCont.setVisibility(View.GONE);
            mLoadingCont = mListFooter;
        }

        //noinspection ConstantConditions
        mLoadingCont.findViewById(R.id.butt_retry).setOnClickListener(MainActivity.this);
        mLoadingErrorText = (TextView) mLoadingCont.findViewById(R.id.text_loading_error);
    }

    private void initTwitsList() {
        mTwitsAdapter = new TwitsAdapter();

        ListView twitsList = (ListView) findViewById(R.id.list_twits);
        twitsList.addFooterView(mListFooter, null, false);
        twitsList.setAdapter(mTwitsAdapter);
    }

    @Override
    public void onTokenObtain(String token) {
        mToken = token;
        mTwitsAdapter.loadMoreData();
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        String message = error.getCause() != null ? error.getCause().getMessage() : getString(R.string.some_error);
        mLoadingErrorText.setText(message);
        //noinspection ConstantConditions
        if (mLoadingCont.getNextView().getId() != R.id.cont_progress) mLoadingCont.showNext();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.butt_retry) {
            mLoadingCont.showNext();
            mRequestQueue.add(new ObtainTokenRequest(this, this));
        }
    }

    private class TwitsAdapter extends BaseAdapter implements Listener<ListTwits> {

        @Override
        public int getCount() {
            return mTwits.size();
        }

        @Override
        public Twit getItem(int position) {
            return mTwits.get(position);
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (position == getCount() - 1) {
                loadMoreData();
            }

            TwitHolder holder;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_twit, parent, false);

                holder = new TwitHolder();
                //noinspection ConstantConditions
                holder.avatarImage = (NetworkImageView) convertView.findViewById(R.id.image_avatar);
                holder.nameText = (TextView) convertView.findViewById(R.id.text_name);
                holder.screenNameText = (TextView) convertView.findViewById(R.id.text_screen_name);
                holder.createAtText = (TextView) convertView.findViewById(R.id.text_created_at);
                holder.twitText = (TextView) convertView.findViewById(R.id.text_twit);

                convertView.setTag(holder);
            } else {
                holder = (TwitHolder) convertView.getTag();
            }

            Twit twit = getItem(position);
            User user = twit.getUser();

            holder.avatarImage.setImageUrl(user.getProfileImageUrl(), mImageLoader);
            holder.nameText.setText(user.getName());
            holder.screenNameText.setText("@" + user.getScreenName());
            holder.createAtText.setText("");
            holder.twitText.setText(twit.getText());

            return convertView;
        }

        public void loadMoreData() {
            String url = mTwits.isEmpty() ? Urls.USER_TIMELINE_URL : String.format(Urls.USER_TIMELINE_WITH_MAX_ID_URL, mTwits.get(mTwits.size() - 1).getId() - 1);

            mRequestQueue.add(new GsonRequest<ListTwits>(mToken, url, ListTwits.class, this, MainActivity.this));
        }

        @Override
        public void onResponse(ListTwits twits) {
            if (!twits.getAll().isEmpty() && mLoadingCont != mListFooter) {
                mLoadingCont.setVisibility(View.GONE);
                mLoadingCont = mListFooter;
                mLoadingCont.findViewById(R.id.butt_retry).setOnClickListener(MainActivity.this);
                mLoadingErrorText = (TextView) mLoadingCont.findViewById(R.id.text_loading_error);
            }

            mTwits.addAll(twits.getAll());

            notifyDataSetChanged();
        }

        private class TwitHolder {
            NetworkImageView avatarImage;
            TextView nameText;
            TextView screenNameText;
            TextView createAtText;
            TextView twitText;
        }
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(TOKEN, mToken);
        outState.putSerializable(TWITS, mTwits);
    }
}