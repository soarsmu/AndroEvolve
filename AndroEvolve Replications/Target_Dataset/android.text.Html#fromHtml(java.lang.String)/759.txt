package io.github.mattlavallee.budgetbeaver.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import io.github.mattlavallee.budgetbeaver.R;
import io.github.mattlavallee.budgetbeaver.handlers.BudgetBeaverFabSetup;

public class FeedbackFragment extends Fragment{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View fragmentView = inflater.inflate(R.layout.fragment_feedback, container, false);
        getActivity().setTitle("About & Feedback");

        //no FAB on Add/Edit transaction layout
        RelativeLayout parent = (RelativeLayout)getActivity().findViewById(R.id.budget_beaver_fragment_wrapper);
        BudgetBeaverFabSetup.removeExistingFab(parent);

        TextView credits = (TextView)fragmentView.findViewById(R.id.feedback_credits);
        TextView about = (TextView)fragmentView.findViewById(R.id.feedback_feedback);
        String content = "App icon made by <a href=\"http://www.freepik.com\">Freepik</a> from <a href=\"http://www.flaticon.com\">www.flaticon.com</a>. Icon is licensed by <a href=\"http://creativecommons.org/licenses/by/3.0/\">CC 3.0 BY</a>";

        String aboutTxt = "Thanks for using my app!<br />I am an individual developer creating this app in my free time." +
                " I welcome any feedback that you may have. If something does not work as expected or " +
                "if there is something missing that you would like added, please contact me at<br />" +
                "<a href=\"mailto:matt.lavallee.dev@gmail.com\">matt.lavallee.dev@gmail.com</a>";

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            credits.setText(Html.fromHtml(content,Html.FROM_HTML_MODE_LEGACY));
            about.setText(Html.fromHtml(aboutTxt, Html.FROM_HTML_MODE_LEGACY));
        } else {
            credits.setText(Html.fromHtml(content));
            about.setText(Html.fromHtml(aboutTxt));
        }

        return fragmentView;
    }
}
