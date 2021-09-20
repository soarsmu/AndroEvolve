package teach.vietnam.asia.view.search;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import teach.vietnam.asia.Constant;
import teach.vietnam.asia.R;
import teach.vietnam.asia.entity.WordEntity;
import teach.vietnam.asia.utils.Common;
import teach.vietnam.asia.utils.Log;
import teach.vietnam.asia.utils.NumberToWord;
import teach.vietnam.asia.utils.Utility;
import teach.vietnam.asia.view.base.BaseActivity;


public class SearchAllAdapter extends BaseAdapter implements SectionIndexer {

    private final String TAG = "SearchAllAdapter";
    private Context context;
    private List<WordEntity> listData;
    private List<WordEntity> listData2;
    private LayoutInflater layoutInflater;
    private String lang = "";
    private String[] alpha;
    boolean modify = false;
    boolean reLoad = false;
    private String currString = "";

    public SearchAllAdapter(Context context, List<WordEntity> listData) {
        int i = 0;
        String word;
        this.context = context;
        this.listData = listData;
        listData2 = new ArrayList();
        try {
            Log.i(SearchAllAdapter.class, "SearchAllAdapter");
            listData2.addAll(listData);
            layoutInflater = LayoutInflater.from(context);
//            lang = context.getString(R.string.language);
            lang = BaseActivity.pref.getStringValue("en", Constant.EN);

            alpha = null;
            alpha = new String[listData.size()];

            for (WordEntity entity : listData) {
                word = android.text.Html.fromHtml(entity.getO1()).toString();
                alpha[i++] = word.split(" ")[0];
            }

        } catch (Exception e) {
            Log.e(SearchAllAdapter.class, "SearchAllAdapter Error: " + e.getMessage());
        }

    }


    public int getCount() {
        return listData.size();
    }

    @Override
    public WordEntity getItem(int position) {
        return listData.get(position);
    }

//    public String getDataVi(int position){
//        return Utility.getVi(listData.get(position), lang);
//    }
//
//    public String getDataDefaultWord(int position){
//        return Utility.getDefaultWord(listData.get(position), lang);
//    }

    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    public View getView(int position, View view, ViewGroup viewGroup) {
        int resourceId;
        final ViewHolder holder;
        String phrases;

        if (view == null) {
            holder = new ViewHolder();
            view = layoutInflater.inflate(R.layout.search_item, null);
            holder.tvOther = (TextView) view.findViewById(R.id.tvOther);
            holder.tvVn = (TextView) view.findViewById(R.id.tvVn);
            holder.imgWord = (ImageView) view.findViewById(R.id.imgWord);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        if (listData.size() <= position)
            return view;

        holder.tvOther.setText(Html.fromHtml(listData.get(position).getO1()));

//        phrases = String.format(listData.get(position).getVi(), "<u>" + listData.get(position).getDefault_word() + "</u>");
        phrases =""; //// FIXME: 5/11/2017 temp
        holder.tvVn.setText(Html.fromHtml(phrases));

        // img.setScaleType(ImageView.ScaleType.FIT_XY);
        resourceId = Utility.getResourcesID(context, listData.get(position).getImg());
        if (resourceId > 0) {
            holder.imgWord.setImageResource(resourceId);
            // holder.imgWord.setTag(resourceId);
        } else {
            //truong hop hinh la food
            resourceId = Utility.getResourcesID(context, "f_" + listData.get(position).getImg());
            if (resourceId > 0)
                holder.imgWord.setImageResource(resourceId);
            else
                holder.imgWord.setImageResource(0);
//            ULog.i(SearchAllAdapter.class, "dont image load");
        }
        return view;
    }

    private void resetAlphaSearch() {
        int i = 0;
        String word;
        alpha = null;
        alpha = new String[listData.size()];

        for (WordEntity entity : listData) {
//            alpha[i++] = Utility.getO1(viet, lang).replaceAll("<u>", "").replaceAll("</u>", "").split(" ")[0];
            word = android.text.Html.fromHtml(entity.getO1()).toString();
            alpha[i++] = word.split(" ")[0];
        }

    }

    @SuppressLint("DefaultLocale")
    public void filter(String charText) {
        if (!modify) {
            modify = true;
            new ResetAdapter(charText).execute();
        } else {
            reLoad = true;
            currString = charText;
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

    public class ViewHolder {
        TextView tvOther;
        TextView tvVn;
        ImageView imgWord;
    }

    public class ResetAdapter extends AsyncTask<Void, Void, Boolean> {
        private String charText;

        public ResetAdapter(String charText) {
            this.charText = charText;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            String word1, word2, wordVN;
            long number;
            Object tmp;
            try {
//                if(modify)
//                    return false;
//                modify = true;
                charText = charText.toLowerCase(Locale.getDefault()).trim();
                charText = Common.stripAccents(charText);
                Log.i(TAG, "filter key: " + charText);
                listData.clear();
                if (charText.length() == 0) {
                    listData.addAll(listData2);
                } else {

                    for (WordEntity vi : listData2) {

                        wordVN = android.text.Html.fromHtml(vi.getVi()).toString().toLowerCase();
                        wordVN = Common.stripAccents(wordVN);

                        if (wordVN.contains(charText)
//                                || charText.contains(wordVN)
                                ) {
                            listData.add(vi);
                        } else {
                            word1 = android.text.Html.fromHtml(vi.getO1()).toString().toLowerCase();
                            if (word1.contains(charText)
//                                    || charText.contains(word1)
                                    ) {
                                listData.add(vi);
                            } else {
                                word2 = android.text.Html.fromHtml(vi.getO2()).toString().toLowerCase();
                                if (!word2.equals("") && (word2.contains(charText)
//                                        || charText.contains(word2)
                                )) {
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
                            listData.add(new WordEntity(NumberToWord.getWordFromNumber(number), charText));
                        }
                    }
                }
                resetAlphaSearch();
            } catch (Exception e) {
                Log.e(SearchAllAdapter.this, "filter error:" + e.getMessage());
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean b) {
            super.onPostExecute(b);
            try {
//                if (b) {
//                    resetAlphaSearch();
                SearchAllAdapter.this.notifyDataSetChanged();
//                }


                if (reLoad) {
                    reLoad = false;
                    Log.i(SearchAllAdapter.class, "reload data...");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            new ResetAdapter(currString).execute();
                        }
                    }, 800);


                } else {
                    modify = false;
//                    resetAlphaSearch();
//                    SearchAllAdapter.this.notifyDataSetChanged();
                }
            } catch (Exception e) {
                Log.e("SearchAllAdapter", "notify Error:" + e.getMessage());
            }
        }
    }

}