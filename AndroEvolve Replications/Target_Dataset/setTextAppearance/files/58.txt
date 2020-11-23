package com.added.addedteacher;

import android.app.Fragment;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.added.addedteacher.database.MyDatabase;

import java.util.ArrayList;

import model.LectureTopicModel;

public class LecturesView extends Fragment implements TabHost.TabContentFactory {
	MyDatabase db;
	Context context;
	int[] count;
	ArrayList<LectureTopicModel> topics;
	int ch_id;
	int total_lectures;
	private String activity;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v=inflater.inflate(R.layout.view_lectures,container,false);
		return v;
	}

	/** Called when the activity is first created. */


	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		context=getActivity().getApplicationContext();
		Bundle bundle=this.getArguments();
		ch_id=bundle.getInt("ch_id");
		db=MyDatabase.getDatabaseInstance(context);
		topics=db.getTopicInLecture(ch_id);
		total_lectures=bundle.getInt("total_lectures");
		final TabHost tabHost = (TabHost) getView().findViewById(R.id.tabHost);
		tabHost.setup();

		Resources res = getResources();
		Configuration cfg = res.getConfiguration();
		boolean hor = cfg.orientation == Configuration.ORIENTATION_LANDSCAPE;
		count=new int[total_lectures];
		for(int i=0;i<total_lectures;i++)
		{
			int k=0;
			for(int j=0;j<LectureTopicModel.lecture_topic_content.size();j++){

				if(LectureTopicModel.lecture_topic_content.get(j).lect_no==(i+1))
				{
					k++;
				}

			}
			count[i]=k;
		}
		if (hor) {
			TabWidget tw = tabHost.getTabWidget();
			tw.setOrientation(LinearLayout.VERTICAL);
		}
		for(int i=0;i<total_lectures;i++)
		{

			tabHost.addTab(tabHost.newTabSpec(""+i)
					.setIndicator(createIndicatorView(tabHost, "Lecture "+(i+1)))
					.setContent(this));

		}
	}

	private View createIndicatorView(TabHost tabHost, CharSequence label) {

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View tabIndicator = inflater.inflate(R.layout.tab_indicator,
				tabHost.getTabWidget(), // tab widget is the parent
				false); // no inflate params

		final TextView tv = (TextView) tabIndicator.findViewById(R.id.title);
		tv.setText(label);
		return tabIndicator;
	}

	@Override
	public View createTabContent(String tag) {
		int c=Integer.parseInt(tag);

		ScrollView parentScrollView=new ScrollView(context);
		LinearLayout.LayoutParams parentScrollViewLayoutParams=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		parentScrollView.setLayoutParams(parentScrollViewLayoutParams);
		LinearLayout parentLinearLayout1=new LinearLayout(context);
		LinearLayout.LayoutParams parentLayout1Params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
		parentLinearLayout1.setLayoutParams(parentLayout1Params);
		parentLinearLayout1.setOrientation(LinearLayout.VERTICAL);

		String topic,objective,introduction,questions,textassignment;
		/*for(int i=0;i<count[c];i++)
		{*/

		for(int j=0;j<LectureTopicModel.lecture_topic_content.size();j++){

			if(LectureTopicModel.lecture_topic_content.get(j).lect_no==(c+1))
			{
				topic=LectureTopicModel.lecture_topic_content.get(j).topic;
				objective=LectureTopicModel.lecture_topic_content.get(j).objective;
				introduction=LectureTopicModel.lecture_topic_content.get(j).intro;
				questions=LectureTopicModel.lecture_topic_content.get(j).quest;
				activity=LectureTopicModel.lecture_topic_content.get(j).activity;
				textassignment=LectureTopicModel.lecture_topic_content.get(j).assignment;
				LinearLayout parentLinearLayout=getInsight(topic,objective,introduction,questions,textassignment);
				parentLinearLayout1.addView(parentLinearLayout);
			}

		}



		/*}*/

		parentScrollView.addView(parentLinearLayout1);
		return parentScrollView;
	}

	private LinearLayout getInsight(String  topic ,String objective,String introduction,String questions,String textassignment) {
		// TODO Auto-generated method stub

		LinearLayout parentLinearLayout=new LinearLayout(context);
		LinearLayout.LayoutParams parentLayoutParams=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
		parentLinearLayout.setLayoutParams(parentLayoutParams);
		parentLinearLayout.setOrientation(LinearLayout.VERTICAL);


		TextView topicTextView=new TextView(context);
		topicTextView.setTextAppearance(context, android.R.style.TextAppearance_Medium);
		topicTextView.setTextColor(Color.parseColor("#691A99"));
		topicTextView.setTypeface(null, Typeface.BOLD);
		topicTextView.setText(topic);
		topicTextView.setTextSize(23);
		LinearLayout.LayoutParams  topicLayoutParams=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		topicLayoutParams.setMargins(0, 3, 0, 5);



		TextView objectiveTextView=new TextView(context);
		objectiveTextView.setTextAppearance(context, android.R.style.TextAppearance_Medium);
		objectiveTextView.setTextColor(Color.parseColor("#8E24AA"));
		objectiveTextView.setTypeface(null,Typeface.BOLD);
		objectiveTextView.setText("Objective");

		TextView objectiveContentTextView=new TextView(context);
		objectiveContentTextView.setText(objective);
		objectiveContentTextView.setTextColor(Color.parseColor("#000000"));
		objectiveContentTextView.setLayoutParams(topicLayoutParams);


		TextView introductionTextView=new TextView(context);
		introductionTextView.setTextAppearance(context, android.R.style.TextAppearance_Medium);
		introductionTextView.setText("Introduction");
		introductionTextView.setTextColor(Color.parseColor("#8E24AA"));
		introductionTextView.setTypeface(null,Typeface.BOLD);
		TextView introContentTextView=new TextView(context);
		introContentTextView.setText(introduction);
		introContentTextView.setTextColor(Color.parseColor("#000000"));
		introContentTextView.setLayoutParams(topicLayoutParams);

		TextView questionsTextView=new TextView(context);
		questionsTextView.setTextAppearance(context, android.R.style.TextAppearance_Medium);
		questionsTextView.setText("Questions");
		questionsTextView.setTextColor(Color.parseColor("#8E24AA"));
		questionsTextView.setTypeface(null,Typeface.BOLD);
		TextView questionsContentTextView=new TextView(context);
		questionsContentTextView.setText(questions);
		questionsContentTextView.setTextColor(Color.parseColor("#000000"));
		questionsContentTextView.setLayoutParams(topicLayoutParams);

		TextView activityTextView=new TextView(context);
		activityTextView.setTextAppearance(context, android.R.style.TextAppearance_Medium);
		activityTextView.setText("Activity");
		activityTextView.setTextColor(Color.parseColor("#8E24AA"));
		activityTextView.setTypeface(null,Typeface.BOLD);
		TextView activityContentTextView=new TextView(context);
		activityContentTextView.setText(activity);
		activityContentTextView.setTextColor(Color.parseColor("#000000"));
		activityContentTextView.setLayoutParams(topicLayoutParams);


		TextView testAssignmentTextView=new TextView(context);
		testAssignmentTextView.setTextAppearance(context, android.R.style.TextAppearance_Medium);
		testAssignmentTextView.setText("Test/Assignment");
		testAssignmentTextView.setTextColor(Color.parseColor("#8E24AA"));
		testAssignmentTextView.setTypeface(null,Typeface.BOLD);
		TextView testAssignmentContentTextView=new TextView(context);
		testAssignmentContentTextView.setText(textassignment);
		testAssignmentContentTextView.setTextColor(Color.parseColor("#000000"));
		testAssignmentContentTextView.setLayoutParams(topicLayoutParams);

		View v=new View(context);
		LinearLayout.LayoutParams viewLayoutParams=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 3);
		v.setLayoutParams(viewLayoutParams);
		v.setBackgroundColor(Color.parseColor("#000000"));
		/*TextView durationTextView=new TextView(context);
		durationTextView.setTextAppearance(context, android.R.style.TextAppearance_Medium);
		durationTextView.setText("Duration");
		TextView durationContentTextView=new TextView(context);
		durationContentTextView.setText(duration);
		durationContentTextView.setLayoutParams(topicLayoutParams);
		*/parentLinearLayout.addView(topicTextView);
		parentLinearLayout.addView(objectiveTextView);
		parentLinearLayout.addView(objectiveContentTextView);
		parentLinearLayout.addView(introductionTextView);
		parentLinearLayout.addView(introContentTextView);
		parentLinearLayout.addView(questionsTextView);
		parentLinearLayout.addView(questionsContentTextView);
		parentLinearLayout.addView(activityTextView);
		parentLinearLayout.addView(activityContentTextView);
		parentLinearLayout.addView(testAssignmentTextView);
		parentLinearLayout.addView(testAssignmentContentTextView);
		parentLinearLayout.addView(v);
		/*parentLinearLayout.addView(durationTextView);
		parentLinearLayout.addView(durationContentTextView);
*/
		return parentLinearLayout;
	}
}