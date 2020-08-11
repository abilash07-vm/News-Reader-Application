package com.example.newsreaderapp.ui.main;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newsreaderapp.NewsItem;
import com.example.newsreaderapp.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {
    private static final String TAG = "PlaceholderFragment";

    private static final String ARG_SECTION_NUMBER = "section_number";

    private int position;
    private PageViewModel pageViewModel;
    private ArrayList<NewsItem> newsArr;
    private RecyclerView recyclerView;
    private NewsAdaptor adaptor;
    private String link;

    public static PlaceholderFragment newInstance(int index) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);
        link = pageViewModel.getLink();

    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, container, false);
        final TextView textView = root.findViewById(R.id.txt);
        recyclerView = root.findViewById(R.id.recylerView);
        newsArr = new ArrayList<>();
        adaptor = new NewsAdaptor(container.getContext());
        recyclerView.setAdapter(adaptor);
        recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));


        pageViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
                new getNews().execute();
            }
        });
        return root;

    }

    private class getNews extends AsyncTask<Void, Void, Void> {
        private String link = pageViewModel.getLink();

        @Override
        protected Void doInBackground(Void... voids) {
            InputStream inputStream = getInputStream();
            if (inputStream != null) {
                try {
                    initXMLPullparser(inputStream);
                } catch (XmlPullParserException | IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adaptor.setNews(newsArr);
            Log.d(TAG, "onPostExecute: executing...");
        }

        private void initXMLPullparser(InputStream inputStream) throws XmlPullParserException, IOException {
            Log.d(TAG, "initXMLPullparser: inialling  xml pull parser");
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(inputStream, null);

            parser.next();
            parser.require(XmlPullParser.START_TAG, null, "rss");
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                parser.require(XmlPullParser.START_TAG, null, "channel");
                while ((parser.next() != XmlPullParser.END_TAG)) {
                    if (parser.getEventType() != XmlPullParser.START_TAG) {
                        continue;
                    }
                    if (parser.getName().equals("item")) {
                        parser.require(XmlPullParser.START_TAG, null, "item");
                        String title = "", description = "", link = "", date = "";
                        while (parser.next() != XmlPullParser.END_TAG) {
                            if (parser.getEventType() != XmlPullParser.START_TAG) {
                                continue;
                            }
                            String tagName = parser.getName();
                            if (tagName.equals("title")) {
                                title = getContent(parser, "title");
                            } else if (tagName.equals("description")) {
                                description = getContent(parser, "description");
                                while (description.contains("<")) {
                                    int i = description.indexOf('<');
                                    int j = description.indexOf('>');
                                    description = description.replaceAll("<[^<>]*>", "");
                                }
                            } else if (tagName.equals("link")) {
                                link = getContent(parser, "link");
                            } else if (tagName.equals("pubDate")) {
                                date = getContent(parser, "pubDate");
                            } else {
                                skipParser(parser);
                            }

                        }
                        NewsItem item = new NewsItem(title, description, link, date);
                        newsArr.add(item);
                        if (newsArr.size() > 10) {
                            break;
                        }
                    } else {
                        skipParser(parser);
                    }

                }
            }
        }

        private void skipParser(XmlPullParser parser) throws XmlPullParserException, IOException {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                throw new IllegalStateException();
            }
            int number = 1;
            while (number != 0) {
                switch (parser.next()) {
                    case XmlPullParser.START_TAG:
                        number++;
                        break;
                    case XmlPullParser.END_TAG:
                        number--;
                        break;
                    default:
                        break;
                }
            }
        }

        private String getContent(XmlPullParser parser, String tagName) throws IOException, XmlPullParserException {
            String content = "";
            parser.require(XmlPullParser.START_TAG, null, tagName);
            if (parser.next() == XmlPullParser.TEXT) {
                content = parser.getText();
                parser.next();
            }
            return content;
        }

        private InputStream getInputStream() {
            try {
                URL url = new URL(link);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                return connection.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}