package cn.ucai.live.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.ucai.live.R;
import cn.ucai.live.data.NetDao;
import cn.ucai.live.data.model.Result;
import cn.ucai.live.data.model.Wallet;
import cn.ucai.live.utils.CommonUtils;
import cn.ucai.live.utils.OnCompleteListener;
import cn.ucai.live.utils.PreferenceManager;
import cn.ucai.live.utils.ResultUtils;

public class ChangeActivity extends AppCompatActivity {

    @BindView(R.id.tv_change_balance)
    TextView tvChangeBalance;
    @BindView(R.id.target_layout)
    LinearLayout targetLayout;
    View loadingView;
    int change;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change);
        ButterKnife.bind(this);
        loadingView = LayoutInflater.from(ChangeActivity.this).inflate(R.layout.rp_loading, targetLayout, false);
        targetLayout.addView(loadingView);
        setChange();
        initData();
    }

    private void setChange() {
        change = PreferenceManager.getInstance().getCurrentChange();
        tvChangeBalance.setText("￥" + Float.valueOf(String.valueOf(change)));
    }

    private void initData() {
        NetDao.loadChange(ChangeActivity.this, EMClient.getInstance().getCurrentUser(), new OnCompleteListener<String>() {
            @Override
            public void onSuccess(String s) {
                boolean success = false;
                if (s != null) {
                    Result result = ResultUtils.getResultFromJson(s, Wallet.class);
                    if (result != null && result.isRetMsg()) {
                        success = true;
                        Wallet wallet = (Wallet) result.getRetData();
                        PreferenceManager.getInstance().setCurrentChange(wallet.getBalance());
                        change = wallet.getBalance();
                        setChange();
                    }
                }
                if (!success) {
                    PreferenceManager.getInstance().setCurrentChange(0);
                }
                loadingView.setVisibility(View.GONE);
            }

            @Override
            public void onError(String error) {
                loadingView.setVisibility(View.GONE);
                CommonUtils.showShortToast(error);
            }
        });

    }
}
