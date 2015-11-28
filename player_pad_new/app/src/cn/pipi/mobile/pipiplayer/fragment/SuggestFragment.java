package cn.pipi.mobile.pipiplayer.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import cn.pipi.mobile.pipiplayer.config.AppConfig;
import cn.pipi.mobile.pipiplayer.hd.R;
import cn.pipi.mobile.pipiplayer.util.CommonUtil;
import cn.pipi.mobile.pipiplayer.util.ToastUtil;
import cn.pipi.mobile.pipiplayer.util.XMLPullParseUtil;

/**
 * 意见和建议
 * 
 * @author qiny
 * 
 */
public class SuggestFragment extends SetItemBaseFragment implements
		OnClickListener {

	private final int COMMIT_ADVICES = 1;

	private Context context;

	private View view;

	private EditText suggestEditText;

	private EditText telEditText;

	private EditText qqEditText;

	private EditText emailEditText;

	private Button backButton;

	private Button cannelBtn;

	private Button commitBtn;

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.suggestion, null);

		// return super.onCreateView(inflater, container, savedInstanceState);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// widgetInit();
		context = getActivity();
		widgetInit();
		super.onActivityCreated(savedInstanceState);
	}

	private void showErrorTips(EditText editText, String context) {
		CharSequence html = Html.fromHtml("<font color='red'>" + context
				+ "</font>");
		editText.setError(html);
	}

	@Override
	public void widgetInit() {
		if (view == null)
			return;
		suggestEditText = (EditText) view
				.findViewById(R.id.suggestion_adviceContent);
		telEditText = (EditText) view.findViewById(R.id.suggestion_advicetp);
		qqEditText = (EditText) view.findViewById(R.id.suggestion_adviceqq);
		emailEditText = (EditText) view
				.findViewById(R.id.suggestion_adviceemail);
		backButton = (Button) view.findViewById(R.id.suggest_backbtn);
		cannelBtn = (Button) view.findViewById(R.id.suggestion_cannelbtn);
		commitBtn = (Button) view.findViewById(R.id.suggestion_commitbtn);
		backButton.setOnClickListener(this);
		cannelBtn.setOnClickListener(this);
		commitBtn.setOnClickListener(this);
	}

	/**
	 * 清除所有已输入内容
	 */
	private void emptyAllEditContent() {
		suggestEditText.setText("");
		telEditText.setText("");
		qqEditText.setText("");
		emailEditText.setText("");
	}

	private void commitSuggest() {
		String tel = "";
		String qq = "";
		String email = "";
		if (TextUtils.isEmpty(suggestEditText.getText().toString())
				|| suggestEditText.getText().toString().trim().equals("")) {
			showErrorTips(suggestEditText, "意见不能为空!");
			return;
		}
		String advice = suggestEditText.getText().toString();
		if (!TextUtils.isEmpty(telEditText.getText().toString())) {
			tel = telEditText.getText().toString().trim();
			boolean isTelNum = CommonUtil.isTelePhone(tel);
			if (!isTelNum) {
				showErrorTips(telEditText, "手机号码格式错误");
				return;
			}
		}
		if (!TextUtils.isEmpty(qqEditText.getText().toString())) {
			qq = qqEditText.getText().toString().trim();
			boolean isQQ = CommonUtil.isAvailableQQNumber(qq);
			if (!isQQ) {
				showErrorTips(qqEditText, "QQ格式不正确!");
				return;
			}
		}
		if (!TextUtils.isEmpty(emailEditText.getText().toString())) {
			email = emailEditText.getText().toString().trim();
			boolean isEmail = CommonUtil.isAvailableEmail(email);
			if (!isEmail) {
				showErrorTips(emailEditText, "邮箱格式不合法!");
				return;
			}
		}
		if (!CommonUtil.isNetworkConnect(getActivity())) {
			ToastUtil.ToastShort(getActivity(), "无网络!");
			return;
		}
		commitAdvice(advice, tel, qq, email);
	}

	private void commitAdvice(final String... parameter) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				String result = XMLPullParseUtil.requestSuggestion(
						parameter[0], parameter[1], parameter[2], parameter[3]);
				CommonUtil.sendMessage(COMMIT_ADVICES, handler, result);
			}
		}).start();
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message message) {
			switch (message.what) {
			case COMMIT_ADVICES:
				ToastUtil.ToastShort(getActivity(), "已提交");
				break;

			default:
				break;
			}
		};
	};

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.suggest_backbtn:
			if (hideActivityInterface != null) {
				hideActivityInterface.hideActivityView(false);
			}
			break;

		case R.id.suggestion_cannelbtn:
			emptyAllEditContent();
			break;

		case R.id.suggestion_commitbtn:
			commitSuggest();
			break;
		}

	}

}
