package com.example.newsreaderapp.ui.main;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

public class PageViewModel extends ViewModel {


    private MutableLiveData<Integer> mIndex = new MutableLiveData<>();
    private String[] TAB_TITLES = new String[]{"topstories", "Education", "Technology"};


    private String[] links = {"https://timesofindia.indiatimes.com/rssfeedstopstories.cms", "https://timesofindia.indiatimes.com/rssfeeds/913168846.cms", "https://timesofindia.indiatimes.com/rssfeeds/66949542.cms"};
    private String link = links[1];
    private LiveData<String> mText = Transformations.map(mIndex, new Function<Integer, String>() {
        @Override
        public String apply(Integer input) {
            link = links[input - 1];
            return "What's new about " + TAB_TITLES[input - 1];
        }
    });

    public String getLink() {
        return link;
    }

    public void setIndex(int index) {
        mIndex.setValue(index);
    }

    public LiveData<String> getText() {
        return mText;
    }
}