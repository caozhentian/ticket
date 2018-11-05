package zt.com.ti.ticket.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.routon.iDR410SDK.Ctrl;
import com.routon.iDR410SDK.DeviceModel;
import com.routon.iDR410SDK.Reader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import zt.com.ti.ticket.R;

public class ReadCardActivity extends AppCompatActivity implements OnClickListener {
	private final String TAG = "ReadCardActivity";
	private static final String CARD_TYPE = "card_type";
	private static final int CARD_TYPE_AUTO = 0;
	private static final int CARD_TYPE_A = 1;
	private static final int CARD_TYPE_B = 2;
	private static final int SAMPOWER_TIME = 2100; // 1500->2100 for iDR500-1
	private static final int READCARD_INTERVAL = 200;
	private int mCardType;
	private Reader mReader;
	private Reader.IDCardInfo mCardInfo;
	private byte[] mFingerPrint;
	private byte[] mWltData;
	private String mStrCardNo;
	private String mStrStatus;
	private ReadCardTask mReadCardThread;
	private TextView mTextViewName;
	private TextView mTextViewGender;
	private TextView mTextViewNationTitle;
	private TextView mTextViewNation;
	private TextView mTextViewYear;
	private TextView mTextViewMonth;
	private TextView mTextViewDay;
	private TextView mTextViewAddress;
	private TextView mTextViewIDNoTitle;
	private TextView mTextViewIDNo;
	private TextView mTextViewAgency;
	private TextView mTextViewExpire;
	private ImageView mImageViewPortrait;
	private TextView mTextViewStatus;
	private TextView mTextViewCardNo;
	private TextView mTextViewPassportNum, mTextViewIssuranceTimes;
	private String cardNo;
	private CheckBox mCheckBoxReadCardCid;
	// private String portName="/dev/ttyS4";
	private String portName = "/dev/ttyMT1";
	private int baudrate = 115200;
	private Button buttonBack;
	private DeviceModel mDevModel = DeviceModel.UNKNOWN;
	private Ctrl mCtrl = null;

	// 2017.8.8 加入外国人证信息界面根据不同卡的类型进行判断
	private LinearLayout linearLayoutChName;
	private TextView mTextViewIName;
	private LinearLayout linearLayoutAddress, linearlayoutGATPassportNum, linearlayoutGATIssuanceTimes;
	private static final int CHINESE = 'C';// 读卡为中文类型
	private static final int FOREIGN = 'I';// 读卡为外国人证
	private static final int GAT = 'J';// 读卡港澳台通行证
	private boolean is_need_IINSNDN = true;
	private boolean is_found_card = false;

	public static final Intent getIntent(Context context){
		Intent  readCardIntent = new Intent(context, ReadCardActivity.class);
		readCardIntent.putExtra(CARD_TYPE, CARD_TYPE_B);
		return readCardIntent;
	}
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.read_card);

		Bundle extras = getIntent().getExtras();
		mCardType = extras.getInt(CARD_TYPE);

		switch (mCardType) {
			case CARD_TYPE_AUTO:
				setTitle(R.string.auto);
				break;

			case CARD_TYPE_A:
				setTitle(R.string.typeA);
				break;

			case CARD_TYPE_B:
				setTitle(R.string.typeB);
				break;
		}

		mTextViewPassportNum = (TextView) findViewById(R.id.textViewPassportNum);// 通行证号码
		mTextViewIssuranceTimes = (TextView) findViewById(R.id.textViewIssuanceTimes);// 签发次数
		mTextViewName = (TextView) findViewById(R.id.textViewName);
		mTextViewGender = (TextView) findViewById(R.id.textViewGender);
		mTextViewNationTitle = (TextView) findViewById(R.id.textViewNationTitle);
		mTextViewNation = (TextView) findViewById(R.id.textViewNation);
		mTextViewYear = (TextView) findViewById(R.id.textViewYear);
		mTextViewMonth = (TextView) findViewById(R.id.textViewMonth);
		mTextViewDay = (TextView) findViewById(R.id.textViewDay);
		mTextViewAddress = (TextView) findViewById(R.id.textViewAddress);
		mTextViewIDNoTitle = (TextView) findViewById(R.id.textViewIDNoTitle);
		mTextViewIDNo = (TextView) findViewById(R.id.textViewIDNo);
		mTextViewAgency = (TextView) findViewById(R.id.textViewAgency);
		mTextViewExpire = (TextView) findViewById(R.id.textViewExpire);
		mImageViewPortrait = (ImageView) findViewById(R.id.imageViewPortrait);
		mTextViewStatus = (TextView) findViewById(R.id.textViewStatus);
		mTextViewCardNo = (TextView) findViewById(R.id.textViewCardNo);
		linearLayoutChName = (LinearLayout) findViewById(R.id.linearLayoutChName);
		mTextViewIName = (TextView) findViewById(R.id.textViewIName);
		linearLayoutAddress = (LinearLayout) findViewById(R.id.linearLayoutAddress);
		linearlayoutGATPassportNum = (LinearLayout) findViewById(R.id.GATPassportNum);
		linearlayoutGATIssuanceTimes = (LinearLayout) findViewById(R.id.GATIssuanceTimes);
		mCheckBoxReadCardCid = (CheckBox) findViewById(R.id.readCardCid);
		buttonBack = (Button) findViewById(R.id.buttonBack);
		buttonBack.setOnClickListener(this);

		mReader = new Reader();
		mCardInfo = mReader.new IDCardInfo();
		mFingerPrint = new byte[1024];
		mWltData = new byte[1024];

		mCtrl = new Ctrl();
		mDevModel = DeviceModel.valueOf(mCtrl.iDR_getDeviceModel());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch(item.getItemId()) //得到被点击的item的itemId
		{
			case  R.id.menu_manu_check :
				startActivity(ForgetIdCardActivity.newIntent(this));
				break;
		}
		return true;
	}

	@Override
	protected void onStart() {
		super.onStart();
		mReadCardThread = new ReadCardTask();
		mReadCardThread.execute(mCardType);
	}

	@Override
	protected void onStop() {
		super.onStop();
		mReadCardThread.cancel(false);
	}

	private void updateIDCardInfo(boolean display) {
		if (display) {
			if (mCardInfo.cardType == CHINESE) {// 身份证
				mTextViewName.setText(mCardInfo.name);
				mTextViewNationTitle.setText(R.string.nation);
				linearlayoutGATPassportNum.setVisibility(View.GONE);// 通行证号码
				linearlayoutGATIssuanceTimes.setVisibility(View.GONE);// 签发次数
				linearLayoutChName.setVisibility(View.GONE);
				mTextViewIDNoTitle.setText(R.string.idno);
				linearLayoutAddress.setVisibility(View.VISIBLE);
			} else if (mCardInfo.cardType == FOREIGN) {// 外国人证
				linearLayoutAddress.setVisibility(View.GONE);
				linearlayoutGATPassportNum.setVisibility(View.GONE);// 通行证号码
				linearlayoutGATIssuanceTimes.setVisibility(View.GONE);// 签发次数
				linearLayoutChName.setVisibility(View.VISIBLE);
				mTextViewName.setText(mCardInfo.englishName);
				mTextViewIName.setText(mCardInfo.name);
				mTextViewNationTitle.setText(R.string.nation_ex);
				mTextViewIDNoTitle.setText(R.string.idno_foreign);
			} else if (mCardInfo.cardType == GAT) {// 港澳通行证
				linearLayoutAddress.setVisibility(View.VISIBLE);
				linearlayoutGATPassportNum.setVisibility(View.VISIBLE);// 通行证号码
				linearlayoutGATIssuanceTimes.setVisibility(View.VISIBLE);// 签发次数
				mTextViewName.setText(mCardInfo.name);
				mTextViewPassportNum.setText(mCardInfo.passportNum);
				mTextViewIssuranceTimes.setText(mCardInfo.issuanceTimes);
			}
			mTextViewCardNo.setText(cardNo.toUpperCase());
			mTextViewStatus.setText(mStrStatus);
			mTextViewGender.setText(mCardInfo.gender);
			mTextViewNation.setText(mCardInfo.nation);
			mTextViewYear.setText(mCardInfo.birthday.substring(0, 4));
			mTextViewMonth.setText(mCardInfo.birthday.substring(4, 6));
			mTextViewDay.setText(mCardInfo.birthday.substring(6, 8));
			if (mCardInfo.address != null) {
				mTextViewAddress.setText(mCardInfo.address);
			}
			mTextViewIDNo.setText(mCardInfo.id);
			if (mCardInfo.agency != null) {
				mTextViewAgency.setText(mCardInfo.agency);
			}
			if (isExpire(mCardInfo.expireEnd)) {
				mTextViewExpire.setTextColor(Color.RED);
				mTextViewExpire.setText(mCardInfo.expireStart + " - " + mCardInfo.expireEnd + "(过期)");
			} else {
				mTextViewExpire.setTextColor(Color.BLACK);
				mTextViewExpire.setText(mCardInfo.expireStart + " - " + mCardInfo.expireEnd);
			}
			mImageViewPortrait.setImageBitmap(mCardInfo.photo);
		} else {
			mTextViewCardNo.setText("");
			mTextViewStatus.setText(mStrStatus);
			linearLayoutAddress.setVisibility(View.VISIBLE);
			linearLayoutChName.setVisibility(View.GONE);
			mTextViewNationTitle.setText(R.string.nation);
			mTextViewName.setText("");
			mTextViewGender.setText("");
			mTextViewNation.setText("");
			mTextViewYear.setText("");
			mTextViewMonth.setText("");
			mTextViewDay.setText("");
			mTextViewAddress.setText("");
			mTextViewIDNoTitle.setText(R.string.idno);
			mTextViewIDNo.setText("");
			mTextViewAgency.setText("");
			mTextViewExpire.setText("");
			mTextViewPassportNum.setText("");
			mTextViewIssuranceTimes.setText("");
			mImageViewPortrait.setImageBitmap(null);
		}
	}

	/**
	 * 吴冰 2017.8.8 根据A卡是否显示将界面进行调整
	 *
	 * @param display
	 *            A卡是否显示
	 */
	private void updateTypeAInfo(boolean display) {

		linearLayoutChName.setVisibility(View.GONE);
		linearLayoutAddress.setVisibility(View.VISIBLE);
		mTextViewName.setText("");
		mTextViewGender.setText("");
		mTextViewNationTitle.setText(R.string.nation);
		mTextViewNation.setText("");
		mTextViewYear.setText("");
		mTextViewMonth.setText("");
		mTextViewDay.setText("");
		mTextViewAddress.setText("");
		mTextViewAgency.setText("");
		mTextViewExpire.setText("");
		mImageViewPortrait.setImageBitmap(null);
		mTextViewCardNo.setText("");
		mTextViewStatus.setText(mStrStatus);
		if (display) {
			mTextViewIDNoTitle.setText(R.string.idno_A);
			mTextViewIDNo.setText(mStrCardNo.toUpperCase());
		} else {
			mTextViewIDNoTitle.setText(R.string.idno);
			mTextViewIDNo.setText("");
		}
	}

	private class ReadCardTask extends AsyncTask<Integer, Integer, Long> {
		private int mReadStatus = 0; // 读卡状态： 1 - 读卡成功，2 - 读卡失败，0 - 未放卡或卡移走
		private int mLastCardType = 2; // 上一次读成功的卡类型： 1 - A卡，2 - B卡

		protected Long doInBackground(Integer... cardTypes) {
			int cardType = cardTypes[0];
			int ret = mReader.SDT_OpenPort();
			Log.d(TAG, "OPEN RET= " + ret);
			if (ret < 0) {
				SystemClock.sleep(500);
				ret = mReader.SDT_OpenPort();
				if (ret < 0) {
					return -1L;
				}
			}

			mStrStatus = "打开端口成功";
			byte[] CMD_GET_VER = new byte[] { (byte) 0xff, (byte) 0x97, 0x10, 0x10, 0x00 };// 获取单片机版本号命令
			byte[] out_data = new byte[6];
			ret = mReader.RTN_GetMcuVersion(CMD_GET_VER, CMD_GET_VER.length, out_data, out_data.length);
			// Log.d("TAG","mReader.RTN_GetMcuVersion end");
			if (ret == 0) {
				// Log.d("getMcuVersion",String.format("%02x,%02x,%02x",
				// out_data[0],out_data[1],out_data[2]));
				String mcu_16 = String.format("%02x,%02x,%02x", out_data[0], out_data[1], out_data[2]);
				String[] mcu_array = new String[3];
				mcu_array = mcu_16.split(",");
				// mcu_ver = "V" + Integer.toString(out_data[0]) + "." +
				// Integer.toString(out_data[1])+ "." +
				// Integer.toString(out_data[2]);
				String mcu_ver = "V" + Integer.valueOf(mcu_array[0], 16) + "." + Integer.valueOf(mcu_array[1], 16) + "." + Integer.valueOf(mcu_array[2], 16);
				Log.d(TAG, "mcu_ver " + mcu_ver);
			}

			ret = mReader.RTN_SetSAMPower(0);
			// if (ret != 0x90) {
			// mReader.SDT_ClosePort();
			// return -2L;
			// }

			SystemClock.sleep(SAMPOWER_TIME);

			// Reader.SAMIDInfo samInfo = mReader.new SAMIDInfo();
			// mReader.SDT_GetSAMIDToStr(samInfo);
			// Log.d(TAG,"samInfo is " + samInfo.SAMID);
			//
			// mStrStatus = "安全模块上电成功";
			//
			// ret = mReader.SDT_GetSAMStatus();
			// Log.d(TAG,"samstatus ="+ret);
			// if (ret != 0x90) {
			// mReader.SDT_ClosePort();
			// return -3L;
			// }
			//
			// mStrStatus = "查询安全模块状态成功";

			while (false == isCancelled()) {

				SystemClock.sleep(READCARD_INTERVAL);

				switch (cardType) {
					case CARD_TYPE_A:
						ReadTypeA();
						break;

					case CARD_TYPE_B:
						ReadTypeB();
						break;

					default:
						ReadTypeAB();
						break;
				}
			}

			ret = mReader.RTN_SetSAMPower(0xff);

			mReader.SDT_ClosePort();

			return 0L;
		}

		protected void onProgressUpdate(Integer... progress) {
			switch (progress[0]) {
				case 1:
					updateTypeAInfo(true);
					break;

				case 2:
					updateTypeAInfo(false);
					break;

				case 3:
					updateIDCardInfo(true);
					break;

				case 4:
					updateIDCardInfo(false);
					break;
			}
		}

		protected void onPostExecute(Long result) {
		}

		private int ReadTypeA() {
			int ret;
			byte data[] = new byte[50];

			ret = mReader.RTN_TypeASearch(data);
			if (ret == 0x90) { // Found card
				if (mReadStatus == 1) {
					// do nothing

					return 3;
				} else {
					Integer[] len = new Integer[1];
					ret = mReader.RTN_TypeAReadCIDEX(data, len);
					if (ret == 0x90) {
						Log.d(TAG, "typeA len = " + len[0].toString());
						mReadStatus = 1;
						mStrStatus = "读TypeA卡成功";
						// mStrCardNo = String.format("0x%02x%02x%02x%02x",
						// data[0], data[1], data[2], data[3]);
						mStrCardNo = "";
						for (int i = 0; i < len[0]; i++) {
							mStrCardNo += String.format("%02x", data[i]);
						}
						publishProgress(1);

						return 1;
					} else {
						mReadStatus = 2;
						mStrStatus = "读TypeA卡失败";
						publishProgress(2);

						return 2;
					}
				}
			} else {
				if (mReadStatus == 1 || mReadStatus == 2) // Card removed
				{
					mReadStatus = 0;
					mStrStatus = "TypeA卡已移走";
					publishProgress(2);

					return 4;
				} else // not found
				{
					return 0;
				}
			}
		}

		/**
		 * 读身份证基本信息和卡体管理号的原则： 1. USB读卡器(iDR410,iDR410-1,iDR500):
		 * RTN_ReadNewAppMsg->RTN_Authenticate->SDT_ReadIINSNDN->SDT_ReadBaseMsg 2.
		 * 串口读卡器(iDR420,iDR420-1,iDR500-1):
		 * RTN_ReadNewAppMsg->SDT_ReadIINSNDN->SDT_ReadBaseMsg
		 *
		 * @return
		 */
		private int ReadTypeB() {
			int ret;
			byte[] data = new byte[2400];
			is_need_IINSNDN = mCheckBoxReadCardCid.isChecked();
			if (mReadStatus == 1 || mReadStatus == 2) {
				Reader.MoreAddrInfo addrInfo = mReader.new MoreAddrInfo();
				ret = mReader.RTN_ReadNewAppMsg(addrInfo);
				if (ret < 0) { // Card removed
					mReadStatus = 0;
					mStrStatus = "卡片已移走";
					publishProgress(4);

					return 4;
				} else {
					// do nothing

					return 3;
				}
			} else {// end if (mReadStatus == 1 || mReadStatus == 2)
				// 需要读卡体管理号时，分型号处理
				if (is_need_IINSNDN) {
					// USB型设备
					if (mDevModel.equals(DeviceModel.iDR410) || mDevModel.equals(DeviceModel.iDR410_1) || mDevModel.equals(DeviceModel.iDR500)) {
						ret = mReader.RTN_Authenticate();
						if (ret == 0) {
							is_found_card = true;
						} else {
							is_found_card = false;
						}
					} // end USB设备

					// 读卡体管理号
					byte[] data_no = new byte[8];
					int retNo = 0;
					// 串口型设备，读卡体管理号时：找卡->选卡->读卡体管理号
					if (mDevModel.equals(DeviceModel.iDR420) || mDevModel.equals(DeviceModel.CI_14T) || mDevModel.equals(DeviceModel.iDR420_1) || mDevModel.equals(DeviceModel.iDR500_1)) {
						// 读卡体管理号
						retNo = mReader.SDT_ReadIINSNDN(data_no);
						cardNo = "";
						if (retNo > 0) {
							for (int i = 0; i < data_no.length; i++) {
								cardNo += String.format("%02x", data_no[i]);
							}
							is_found_card = true;
						} else {
							is_found_card = false;
						}
					} else {// end 串口设备
						// USB型设备，读卡体管理号时：仅读卡体管理号
						if (is_found_card) {
							retNo = mReader.SDT_ReadIINSNDN(data_no);
							cardNo = "";
							if (retNo > 0) {
								for (int i = 0; i < data_no.length; i++) {
									cardNo += String.format("%02x", data_no[i]);
								}
							}
						} // end if(is_found_card)
					} // end else 串口设备
				} else {
					// 不需要读卡体管理号时，直接找卡、选卡
					ret = mReader.RTN_Authenticate();
					if (ret == 0) {
						is_found_card = true;
					} else {
						is_found_card = false;
					}
					cardNo = "";
				} // end else 不需要读卡体管理号

				if (is_found_card) { // Found card
					ret = mReader.SDT_ReadBaseMsg(data);
					// ret = mReader.SDT_ReadBaseFPMsg(data);
					if (ret == 0x90) {
						mReadStatus = 1;
						mStrStatus = "读身份证成功";
						DecodeBaseMsg(data);
						// DecodeBaseFPMsg(data);

						publishProgress(3);

						return 1;
					} else {
						mReadStatus = 2;
						mStrStatus = "读身份证失败";
						publishProgress(4);

						return 2;
					}
				} // end if (is_found_card) { // Found card
			} // end if (mReadStatus == 1 || mReadStatus == 2) else

			return 0;
		}

		private int ReadTypeAB() {
			int ret;
			byte[] data = new byte[32];
			// ret = mReader.SDT_GetSAMID(data);
			// Log.d(TAG,"SDT_GetSAMID ret="+ret);

			if (mLastCardType == CARD_TYPE_B) {
				// First read typeB
				ret = ReadTypeB();
				if (ret >= 1 && ret <= 3) {
					return ret;
				}

				// Then read typeA
				ret = ReadTypeA();
				if (ret >= 1 && ret <= 3) {
					mLastCardType = CARD_TYPE_A;
				}
			} else if (mLastCardType == CARD_TYPE_A) {
				// First read typeA
				ret = ReadTypeA();
				if (ret >= 1 && ret <= 3) {
					return ret;
				}

				// Then read typeB
				ret = ReadTypeB();
				if (ret >= 1 && ret <= 3) {
					mLastCardType = CARD_TYPE_B;
				}
			}

			return 0;
		}

		private void DecodeBaseMsg(byte[] data) {
			// Log.d(TAG, "DecodeBaseMsg " + Utils.toHexString(data, data.length));
			// parse data
			ByteBuffer buffer = ByteBuffer.wrap(data);
			buffer.order(ByteOrder.BIG_ENDIAN);

			// data length
			short mlen, plen;

			mlen = buffer.getShort();
			plen = buffer.getShort();

			byte[] msg = new byte[mlen];
			byte[] photo = new byte[plen];

			buffer.get(msg, 0, mlen);
			buffer.get(photo, 0, plen);

			// 吴冰 2017.8.9 在此处进行mCardInfo的初始化，因为mCardInfo是全局变量这样会存储上次刷卡的信息
			mCardInfo = mReader.new IDCardInfo();
			Reader.RTN_DecodeBaseMsg(msg, mCardInfo);

			System.arraycopy(photo, 0, mWltData, 0, plen);

			mCardInfo.photo = Reader.RTN_DecodeWlt(photo);
		}

		private void DecodeBaseFPMsg(byte[] data) {
			// parse data
			ByteBuffer buffer = ByteBuffer.wrap(data);
			buffer.order(ByteOrder.BIG_ENDIAN);

			// data length
			short mlen, plen, flen;

			mlen = buffer.getShort();
			plen = buffer.getShort();
			flen = buffer.getShort();

			byte[] msg = new byte[mlen];
			byte[] photo = new byte[plen];
			byte[] fingerPrint = new byte[flen];

			buffer.get(msg, 0, mlen);
			buffer.get(photo, 0, plen);
			buffer.get(fingerPrint, 0, flen);

			mCardInfo = mReader.new IDCardInfo();
			Reader.RTN_DecodeBaseMsg(msg, mCardInfo);

			System.arraycopy(photo, 0, mWltData, 0, plen);

			mCardInfo.photo = Reader.RTN_DecodeWlt(photo);

			System.arraycopy(fingerPrint, 0, mFingerPrint, 0, flen);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == buttonBack) {
			finish();
		}
	}

	/**
	 * 吴冰 2017.8.8 判断身份证是否过期
	 *
	 * @param expireEnd
	 * @return
	 */
	private boolean isExpire(String expireEnd) {
		if (expireEnd.startsWith("长期")) {
			return false;
		} else {
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
			Calendar cal = Calendar.getInstance();
			try {
				cal.setTime(format.parse(expireEnd));
			} catch (ParseException e) {
				e.printStackTrace();
				return false;
			}
			cal.roll(Calendar.DAY_OF_MONTH, 1);
			return cal.getTime().compareTo(new Date()) < 0;
		}
	}
}
