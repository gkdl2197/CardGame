package edu.android.cardgame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import edu.android.cardgame.level2.Main2Activity;

import static edu.android.cardgame.MainActivity.SCORE_CHO;

public class ResultActivity extends AppCompatActivity {

    private TextView textResult;
    private Button btnRetry;
    SharedPreferences spf = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        textResult = findViewById(R.id.textResult);
        btnRetry = findViewById(R.id.btnRetry);

        int score_cho = getIntent().getIntExtra(SCORE_CHO,0); // 남은 시간
        int score = 25 - score_cho; // 걸린 시간 (30 - 남은시간)



//        spf = getSharedPreferences("spfScore", MODE_PRIVATE);

        textResult.setText("걸린 시간: " + score + "초");

//        if(spf.getInt("spfscore",0) < score_cho){ //내점수가 저번 점수보다 크면
//            spf.edit().putInt("spfscore",score_cho).commit(); //반영의 commit(). 현재상태 저장
//            textResult.setText("신기록달성\n" + score_cho);
//        }



        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResultActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void Next(View view) {

        Intent intent = new Intent(this, Main2Activity.class);
        startActivity(intent);
    }
}
