package teach.vietnam.asia.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import teach.vietnam.asia.R;
import teach.vietnam.asia.activity.PhrasesActivity;
import teach.vietnam.asia.activity.SearchWordsActivity;
import teach.vietnam.asia.sound.AudioPlayer;
import teach.vietnam.asia.sound.IAudioPlayer;
import teach.vietnam.asia.utils.Constant;
import teach.vietnam.asia.utils.NumberToWord;
import teach.vietnam.asia.utils.ULog;
import teach.vietnam.asia.utils.Utility;

public class PhrasesAdapter extends BaseAdapter implements SectionIndexer {

    public AudioPlayer audio;
    private PhrasesActivity activity;
    private List listData;
    private List listData2;
    private LayoutInflater layoutInflater;
    private String lang = "";
    private String[] alpha;

    @SuppressLint("DefaultLocale")
    public PhrasesAdapter(PhrasesActivity activity, List listData, IAudioPlayer iAudioPlayer) {
        int i = 0;
        String word;
        audio = new AudioPlayer(activity, iAudioPlayer);
//        this.iAudioPlayer = iAudioPlayer;
        ULog.i(PhrasesAdapter.class, "PracticeAdapter locale:" + Locale.getDefault().getLanguage());
        this.activity = activity;
        this.listData = listData;
        listData2 = new ArrayList();
        listData2.addAll(listData);
        lang = activity.lang;
        layoutInflater = LayoutInflater.from(activity);

        alpha = null;
        alpha = new String[listData.size()];

        for (Object viet : listData) {
            word = android.text.Html.fromHtml(Utility.getO1(viet, lang)).toString();
            alpha[i++] = word.split(" ")[0];
        }

    }

    public int getCount() {
        return listData.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    private void resetAlphaSearch() {
        int i = 0;
        String word;
        alpha = null;
        alpha = new String[listData.size()];

        for (Object viet : listData) {
            word = android.text.Html.fromHtml(Utility.getO1(viet, lang)).toString();
            alpha[i++] = word.split(" ")[0];
        }

    }

    @SuppressLint("InflateParams")
    public View getView(final int position, View view, ViewGroup viewGroup) {
        String phrases;
        String word_default;
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = layoutInflater.inflate(R.layout.phrases_item, null);

            holder.llWord = (RelativeLayout) view.findViewById(R.id.llWord);
            holder.tvViet = (TextView) view.findViewById(R.id.tvViet);
            holder.tvOther = (TextView) view.findViewById(R.id.tvOther);
            holder.btnSpeak = (Button) view.findViewById(R.id.btnSpeak);
            holder.imgSearch = (ImageView) view.findViewById(R.id.imgSearch);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        word_default = Utility.getDefaultWord(listData.get(position), lang);
        phrases = String.format(Utility.getVi(listData.get(position), lang), "<u><font color=\"blue\">"
                + word_default + " </font></u>");

        holder.tvViet.setText(Html.fromHtml(phrases));
        holder.tvOther.setText(Html.fromHtml(Utility.getO1(listData.get(position), lang)));

        if (word_default != null && !word_default.trim().equals("")) {
            holder.llWord.setTag(word_default);
            holder.llWord.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    ULog.i(PhrasesAdapter.class, "click tag:" + v.getTag());
                    if (activity.isClick)
                        return;
                    activity.isClick = true;
                    Intent i = new Intent(activity, SearchWordsActivity.class);
                    i.putExtra(Constant.INTENT_POSITION, position);
                    activity.startActivityForResult(i, PhrasesActivity.REQUEST_CODE_SEARCH);
                }
            });
            holder.imgSearch.setVisibility(View.VISIBLE);
        } else {
            holder.imgSearch.setVisibility(View.GONE);
            holder.llWord.setOnClickListener(null);
        }

        holder.btnSpeak.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String word;
                word = String.format(Utility.getVi(listData.get(position), lang), Utility.getDefaultWord(listData.get(position), lang));
                ULog.i(PhrasesAdapter.class, "onClick word:" + word);
                if (Constant.isPro) {
                    word = android.text.Html.fromHtml(word).toString();
                    audio.speakWord(word.toLowerCase().replaceAll("\\?", "").replaceAll("\\.", "").replaceAll("!", "").replaceAll(",", ""));
                }else
                    Utility.installPremiumApp(activity);
            }
        });

        return view;
    }

    // ///filter
    @SuppressLint("DefaultLocale")
    public void filter(String charText) {
        String word1, word2, wordVN;
        long number;

        try {
            charText = charText.toLowerCase(Locale.getDefault()).trim();
            ULog.i(PhrasesAdapter.this, "key: " + charText);
            listData.clear();
            if (charText.length() == 0) {
                ULog.i(PhrasesAdapter.class, "add all word");
                listData.addAll(listData2);
            } else {
                for (Object vi : listData2) {
                    wordVN = android.text.Html.fromHtml(Utility.getVi(vi, lang)).toString().toLowerCase();

                    if (wordVN.contains(charText) || charText.contains(wordVN)) {
                        listData.add(vi);
                    } else {
                        word1 = android.text.Html.fromHtml(Utility.getO1(vi, lang)).toString().toLowerCase();
                        if (word1.contains(charText) || charText.contains(word1)) {
                            listData.add(vi);
                        } else {
                            word2 = android.text.Html.fromHtml(Utility.getO2(vi, lang)).toString().toLowerCase();
                            if (!word2.equals("") && (word2.contains(charText) || charText.contains(word2))) {
                                listData.add(vi);
                            }
                        }
                    }
                }
            }

            if (listData.size() == 0) {
                if (!charText.equals("")) {
                    number = Utility.convertToLong(charText);
                    if (number > -1) {
                        listData.add(Utility.getDataObject(lang, NumberToWord.getWordFromNumber(number), charText));
                    }
                }
            } else
                resetAlphaSearch();

            notifyDataSetChanged();
        } catch (Exception e) {
            ULog.e(PhrasesAdapter.this, "filter error:" + e.getMessage());
        }
    }

    @Override
    public int getPositionForSection(int section) {
        return section;
    }

    @Override
    public int getSectionForPosition(int arg0) {
        return 0;
    }

    @Override
    public Object[] getSections() {
        return alpha;
    }

    public void setSlowly(boolean b) {
        audio.isSlowly = b;
    }



    public class ViewHolder {
        TextView tvViet;
        TextView tvOther;
        Button btnSpeak;
        ImageView imgSearch;
        RelativeLayout llWord;
    }
}