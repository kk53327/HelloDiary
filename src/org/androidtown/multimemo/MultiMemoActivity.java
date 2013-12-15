package org.androidtown.multimemo;

import java.io.File;

import org.androidtown.multimemo.common.TitleBitmapButton;
import org.androidtown.multimemo.db.MemoDatabase;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

/**
 * ��Ƽ�޸� ���� ��Ƽ��Ƽ
 *
 * @author Mike
 */
public class MultiMemoActivity extends Activity {

	public static final String TAG = "MultiMemoActivity";

	/**
	 * �޸� ����Ʈ��
	 */
	ListView mMemoListView;

	/**
	 * �޸� ����Ʈ �����
	 */
	MemoListAdapter mMemoListAdapter;

	/**
	 * �޸� ����
	 */
	int mMemoCount = 0;


	/**
	 * �����ͺ��̽� �ν��Ͻ�
	 */
	public static MemoDatabase mDatabase = null;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.multimemo);

        // SD Card checking
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
    		Toast.makeText(this, "SD ī�尡 �����ϴ�. SD ī�带 ���� �� �ٽ� �����Ͻʽÿ�.", Toast.LENGTH_LONG).show();
    		return;
    	} else {
    		String externalPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    		if (!BasicInfo.ExternalChecked && externalPath != null) {
    			BasicInfo.ExternalPath = externalPath + File.separator;
    			Log.d(TAG, "ExternalPath : " + BasicInfo.ExternalPath);

    			BasicInfo.FOLDER_PHOTO = BasicInfo.ExternalPath + BasicInfo.FOLDER_PHOTO;
    			BasicInfo.FOLDER_VIDEO = BasicInfo.ExternalPath + BasicInfo.FOLDER_VIDEO;
    			BasicInfo.FOLDER_VOICE = BasicInfo.ExternalPath + BasicInfo.FOLDER_VOICE;
    			BasicInfo.FOLDER_HANDWRITING = BasicInfo.ExternalPath + BasicInfo.FOLDER_HANDWRITING;
    			BasicInfo.DATABASE_NAME = BasicInfo.ExternalPath + BasicInfo.DATABASE_NAME;

    			BasicInfo.ExternalChecked = true;
    		}
    	}


        // �޸� ����Ʈ
        mMemoListView = (ListView)findViewById(R.id.memoList);
    	mMemoListAdapter = new MemoListAdapter(this);
    	mMemoListView.setAdapter(mMemoListAdapter);
    	mMemoListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				viewMemo(position);
			}
		});


        // �� �޸� ��ư ����
        TitleBitmapButton newMemoBtn = (TitleBitmapButton)findViewById(R.id.newMemoBtn);
    	newMemoBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Log.d(TAG, "newMemoBtn clicked.");

				Intent intent = new Intent(getApplicationContext(), MemoInsertActivity.class);
				intent.putExtra(BasicInfo.KEY_MEMO_MODE, BasicInfo.MODE_INSERT);
				startActivityForResult(intent, BasicInfo.REQ_INSERT_ACTIVITY);
			}
		});

    	// �ݱ� ��ư ����
        TitleBitmapButton closeBtn = (TitleBitmapButton)findViewById(R.id.closeBtn);
        closeBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
        
        startActivity(new Intent(this, MainActivity.class));
    }


	protected void onStart() {

        // �����ͺ��̽� ����
        openDatabase();

        // �޸� ������ �ε�
        loadMemoListData();


		super.onStart();
	}

	/**
     * �����ͺ��̽� ���� (�����ͺ��̽��� ���� ���� �����)
     */
    public void openDatabase() {
		// open database
    	if (mDatabase != null) {
    		mDatabase.close();
    		mDatabase = null;
    	}

    	mDatabase = MemoDatabase.getInstance(this);
    	boolean isOpen = mDatabase.open();
    	if (isOpen) {
    		Log.d(TAG, "Memo database is open.");
    	} else {
    		Log.d(TAG, "Memo database is not open.");
    	}
    }

    /**
     * �޸� ����Ʈ ������ �ε�
     */
    public int loadMemoListData() {
     	String SQL = "select _id, INPUT_DATE, CONTENT_TEXT, ID_PHOTO, ID_TAG, ID_VIDEO, ID_VOICE, ID_HANDWRITING from MEMO order by INPUT_DATE desc";

     	int recordCount = -1;
     	if (MultiMemoActivity.mDatabase != null) {
	   		Cursor outCursor = MultiMemoActivity.mDatabase.rawQuery(SQL);

	   		recordCount = outCursor.getCount();
			Log.d(TAG, "cursor count : " + recordCount + "\n");

			mMemoListAdapter.clear();
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

				String tagId = outCursor.getString(5);
				
				String videoId = outCursor.getString(5);
				String videoUriStr = null;

				String voiceId = outCursor.getString(6);
				String voiceUriStr = null;

				String handwritingId = outCursor.getString(7);
				String handwritingUriStr = null;

				mMemoListAdapter.addItem(new MemoListItem(memoId, dateStr, memoStr, handwritingId, handwritingUriStr, photoId, photoUriStr, tagId,videoId, videoUriStr, voiceId, voiceUriStr));
			}

			outCursor.close();

			mMemoListAdapter.notifyDataSetChanged();
	   }

	   return recordCount;
    }

	/**
	 * ���� ������ URI ��������
	 */
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


    private void viewMemo(int position) {
    	MemoListItem item = (MemoListItem)mMemoListAdapter.getItem(position);

    	// �޸� ���� ��Ƽ��Ƽ ����
		Intent intent = new Intent(getApplicationContext(), MemoInsertActivity.class);
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



    /**
     * �ٸ� ��Ƽ��Ƽ�� ���� ó��
     */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch(requestCode) {
			case BasicInfo.REQ_INSERT_ACTIVITY:
				if(resultCode == RESULT_OK) {
					loadMemoListData();
				}

				break;

			case BasicInfo.REQ_VIEW_ACTIVITY:
				loadMemoListData();

				break;

		}
	}


}