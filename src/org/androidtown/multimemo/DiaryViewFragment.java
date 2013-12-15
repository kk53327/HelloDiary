package org.androidtown.multimemo;

import org.androidtown.multimemo.db.MemoDatabase;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

@SuppressLint({ "SetJavaScriptEnabled", "ValidFragment" })
public class DiaryViewFragment extends Fragment {
	/*
	private EditText et;
	private WebView web;
	private WebSettings webs;
	private String inputUrl = "http://www.naver.com/";
	private ProgressBar proBar;*/
	
	private InputMethodManager inputMethodManager;
	
	ListView DiaryListView;
	MemoListAdapter DiaryListAdapter;
	int DiaryCount = 0;
	
	private Context mContext;
	
	public DiaryViewFragment(Context context) {
		mContext = context;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.diary_activity, null);
	//	view.findViewById(R.id.newDiaryBtn).setOnClickListener(onClickListener);

		inputMethodManager = (InputMethodManager)mContext.
							getSystemService(Context.INPUT_METHOD_SERVICE);
		
		DiaryListView = (ListView)view.findViewById(R.id.DiaryList);
    	DiaryListAdapter = new MemoListAdapter(getActivity().getApplicationContext());
    	DiaryListView.setAdapter(DiaryListAdapter);
    	DiaryListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				viewMemo(position);
			}
		});
		
		return view;
	}
	
    private void viewMemo(int position) {
    	MemoListItem item = (MemoListItem)DiaryListAdapter.getItem(position);

    	// 메모 보기 액티비티 띄우기
		Intent intent = new Intent(getActivity().getApplicationContext(), MemoInsertActivity.class);
		intent.putExtra(BasicInfo.KEY_MEMO_MODE, BasicInfo.MODE_VIEW);
		intent.putExtra(BasicInfo.KEY_MEMO_ID, item.getId());
		intent.putExtra(BasicInfo.KEY_MEMO_DATE, item.getData(0));
		intent.putExtra(BasicInfo.KEY_MEMO_TEXT, item.getData(1));

		intent.putExtra(BasicInfo.KEY_ID_HANDWRITING, item.getData(2));
		intent.putExtra(BasicInfo.KEY_URI_HANDWRITING, item.getData(3));

		intent.putExtra(BasicInfo.KEY_ID_PHOTO, item.getData(4));
		intent.putExtra(BasicInfo.KEY_URI_PHOTO, item.getData(5));

		intent.putExtra(BasicInfo.KEY_ID_VIDEO, item.getData(6));
		intent.putExtra(BasicInfo.KEY_URI_VIDEO, item.getData(7));

		intent.putExtra(BasicInfo.KEY_ID_VOICE, item.getData(8));
		intent.putExtra(BasicInfo.KEY_URI_VOICE, item.getData(9));

		startActivityForResult(intent, BasicInfo.REQ_VIEW_ACTIVITY);
    }
    
	public void onStart() {

//		openDatabase();
        // 메모 데이터 로딩
        loadMemoListData();

		super.onStart();
	}
	

    public int loadMemoListData() {
     	String SQL = "select _id, INPUT_DATE, CONTENT_TEXT, ID_PHOTO, ID_TAG ,ID_VIDEO, ID_VOICE, ID_HANDWRITING from MEMO order by INPUT_DATE desc";

     	int recordCount = -1;
     	if (MultiMemoActivity.mDatabase != null) {
	   		Cursor outCursor = MultiMemoActivity.mDatabase.rawQuery(SQL);

	   		recordCount = outCursor.getCount();
		//	Log.d(TAG, "cursor count : " + recordCount + "\n");

			DiaryListAdapter.clear();
			Resources res = getResources();

			for (int i = 0; i < recordCount; i++) {
				outCursor.moveToNext();

				String memoId = outCursor.getString(0);

				String dateStr = outCursor.getString(1);
				if (dateStr.length() > 10) {
					dateStr = dateStr.substring(0, 10);
				}

				String memoStr = outCursor.getString(2);
				String photoId = outCursor.getString(3);
				String photoUriStr = getPhotoUriStr(photoId);

				String tagId = outCursor.getString(4);
				
				String videoId = outCursor.getString(5);
				String videoUriStr = null;

				String voiceId = outCursor.getString(6);
				String voiceUriStr = null;

				String handwritingId = outCursor.getString(7);
				String handwritingUriStr = null;

				DiaryListAdapter.addItem(new MemoListItem(memoId, dateStr, memoStr, handwritingId, handwritingUriStr, photoId, photoUriStr, tagId, videoId, videoUriStr, voiceId, voiceUriStr));
			}

			outCursor.close();

			DiaryListAdapter.notifyDataSetChanged();
	   }

	   return recordCount;
    }
    
	public String getPhotoUriStr(String id_photo) {
		String photoUriStr = null;
		if (id_photo != null && !id_photo.equals("-1")) {
			String SQL = "select URI from " + MemoDatabase.TABLE_PHOTO + " where _ID = " + id_photo + "";
			Cursor photoCursor = MultiMemoActivity.mDatabase.rawQuery(SQL);
	    	if (photoCursor.moveToNext()) {
	    		photoUriStr = photoCursor.getString(0);
	    	}
	    	photoCursor.close();
		} else if(id_photo == null || id_photo.equals("-1")) {
			photoUriStr = "";
		}

		return photoUriStr;
	}
	/*
	View.OnClickListener onClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch(v.getId()) {
			case R.id.newDiaryBtn:
				Intent intent = new Intent(getActivity().getApplicationContext(), MemoInsertActivity.class);
				intent.putExtra(BasicInfo.KEY_MEMO_MODE, BasicInfo.MODE_INSERT);
				startActivityForResult(intent, BasicInfo.REQ_INSERT_ACTIVITY);
				break;
			}

		}
	};*/
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch(requestCode) {
			case BasicInfo.REQ_INSERT_ACTIVITY:
		//		if(resultCode == RESULT_OK) {
					loadMemoListData();
		//		}

				break;

			case BasicInfo.REQ_VIEW_ACTIVITY:
				loadMemoListData();

				break;

		}
	}
	
}
